package telran.problem.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import telran.problem.configuration.KafkaConsumer;
import telran.problem.configuration.KafkaProducer;
import telran.problem.dao.ProblemRepository;
import telran.problem.dto.accounting.ActivityDto;
import telran.problem.dto.accounting.ProfileDto;
import telran.problem.dto.exceptions.ProblemNotFoundException;
import telran.problem.dto.problems.CreateProblemDto;
import telran.problem.dto.problems.DonationDto;
import telran.problem.dto.problems.EditProblemDto;
import telran.problem.dto.problems.ProblemDto;
import telran.problem.model.Donation;
import telran.problem.model.Problem;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ProblemServiceImpl implements ProblemService {
    final ProblemRepository problemRepository;
    final ModelMapper modelMapper;
    final KafkaConsumer kafkaConsumer;
    final KafkaProducer kafkaProducer;

    @Override
    @Transactional
    public ProblemDto addProblem(CreateProblemDto newProblem) {
        Problem problem = modelMapper.map(newProblem, Problem.class);
        ProfileDto profile = kafkaConsumer.getProfile();
        problem.setAuthor(profile.getUsername());
        problem.setAuthorId(profile.getEmail());
        problem = problemRepository.save(problem);
        ProblemDto problemDto = modelMapper.map(problem, ProblemDto.class);
        profile.addActivity(problem.getId(), new ActivityDto(problem.getType(), false, false));
        profile.addFormulatedProblem();
        editProfile(profile);
        kafkaProducer.setProblem(problemDto);
        return problemDto;
    }

    @Override
    @Transactional
    public ProblemDto editProblem(EditProblemDto editProblemDto, String userId, String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (problem.getAuthorId().equals(profile.getEmail()) && userId.equals(profile.getEmail())) {
            problem.setTitle(editProblemDto.getTitle());
            problem.setDetails(editProblemDto.getDetails());
            problem.setCommunityNames(editProblemDto.getCommunityNames());
            Problem updatedProblem = problemRepository.save(problem);
            return modelMapper.map(updatedProblem, ProblemDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not author of that problem");
    }


    @Override
    @Transactional
    public ProblemDto deleteProblem(String problemId, String userId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (problem.getAuthorId().equals(profile.getEmail()) && userId.equals(profile.getEmail())) {
            ProblemDto problemDto = modelMapper.map(problem, ProblemDto.class);
            profile.removeActivity(problemId);
            profile.removeFormulatedProblem();
            kafkaProducer.setProblem(new ProblemDto());
            kafkaProducer.setProblemToDelete(problemDto);
            editProfile(profile);
            problemRepository.delete(problem);
            return problemDto;
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not author of that problem");
    }

    @Override
    @Transactional
    public boolean addLike(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ActivityDto activity = profile.getActivities().computeIfAbsent(problemId, a -> new ActivityDto(Problem.class.getSimpleName().toUpperCase(), false, false));
        if (!activity.getLiked()) {
            activity.setLiked(true);
            if (activity.getDisliked()) {
                activity.setDisliked(false);
                problem.getReactions().removeDislike();
            }
            problem.getReactions().addLike();
            problem.updateRating();
            problemRepository.save(problem);
            profile.addActivity(problemId, activity);
            editProfile(profile);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean addDisLike(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ActivityDto activity = profile.getActivities().computeIfAbsent(problemId, a -> new ActivityDto(Problem.class.getSimpleName().toUpperCase(), false, false));
        if (!activity.getDisliked()) {
            activity.setDisliked(true);
            if (activity.getLiked()) {
                activity.setLiked(false);
                problem.getReactions().removeLike();
            }
            problem.getReactions().addDislike();
            problem.updateRating();
            problemRepository.save(problem);
            profile.addActivity(problemId, activity);
            editProfile(profile);
            return true;
        }
        return false;
    }


    @Override
    @Transactional
    public boolean subscribe(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (!profile.getActivities().containsKey(problemId)) {
            profile.addActivity(problemId, new ActivityDto(Problem.class.getSimpleName().toUpperCase(), false, false));
            editProfile(profile);
        }
        if (!problem.getSubscribers().contains(profile.getEmail())) {
            problem.addSubscriber(profile.getEmail());
            problemRepository.save(problem);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean unsubscribe(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (profile.getActivities().containsKey(problemId) &&
                !profile.getActivities().get(problemId).getLiked() && !profile.getActivities().get(problemId).getDisliked()) {
            profile.removeActivity(problemId);
            editProfile(profile);
        }
        if (problem.getSubscribers().contains(profile.getEmail())) {
            problem.removeSubscriber(profile.getEmail());
            problemRepository.save(problem);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean donate(String problemId, DonationDto amount) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        Donation donation = modelMapper.map(amount, Donation.class);
        donation.setAmount(amount.getAmount());
        donation.setUserId(profile.getEmail());
        problem.addDonation(donation);
        problem.checkCurrentAward();
        problemRepository.save(problem);
        if (!profile.getActivities().containsKey(problemId)) {
            profile.addActivity(problemId, new ActivityDto(Problem.class.getSimpleName().toUpperCase(), false, false));
            editProfile(profile);
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemDto findProblemById(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProblemDto problemDto = modelMapper.map(problem, ProblemDto.class);
        kafkaProducer.setProblem(problemDto);
        return problemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProblemDto> getProblems() {
        return problemRepository.findAll().stream().map(e -> modelMapper.map(e, ProblemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double getCurrentAwardByProblemId(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        return problem.getCurrentAward();
    }

    //Administrative block
    @Override
    @Transactional
    public ProblemDto deleteProblem(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (problem.getAuthorId().equals(profile.getEmail()) || profile.getRoles().contains("ADMINISTRATOR")) {
            ProblemDto problemDto = modelMapper.map(problem, ProblemDto.class);
            profile.removeActivity(problemId);
            kafkaProducer.setProblem(new ProblemDto());
            kafkaProducer.setProblemToDelete(problemDto);
            editProfile(profile);
            problemRepository.delete(problem);
            return problemDto;
        } else
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not author of that problem, or you have no roles to delete it");
    }

    private void editProfile(ProfileDto profile) {
        kafkaConsumer.setProfile(profile);
        kafkaProducer.setProfile(profile);
    }
}
