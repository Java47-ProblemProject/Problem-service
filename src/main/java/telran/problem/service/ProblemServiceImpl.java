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
        String data = profile.getEmail() + "," + problem.getId() + ",addProblem";
        kafkaProducer.setDataForAccounting(data);
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
            String data = profile.getEmail() + "," + problem.getId() + ",deleteProblem," + String.join(";", problem.getComments()) + ";" + String.join(";", problem.getSolutions());
            problemRepository.delete(problem);
            kafkaProducer.setDataForAccounting(data);
            return modelMapper.map(problem, ProblemDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not author of that problem");
    }

    @Override
    @Transactional
    public boolean addLike(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        String data;
        boolean hasActivity = profile.getActivities().containsKey(problemId);
        if (!hasActivity) {
            problem.getReactions().addLike();
            problem.updateRating();
            data = profile.getEmail() + "," + problem.getId() + ",addLike";
            kafkaProducer.setDataForAccounting(data);
            return true;
        }
        boolean liked = profile.getActivities().get(problemId).getLiked();
        boolean disliked = profile.getActivities().get(problemId).getDisliked();
        if (!liked) {
            problem.getReactions().addLike();
            problem.updateRating();
            if (disliked) {
                problem.getReactions().removeDislike();
                problem.updateRating();
            }
            data = profile.getEmail() + "," + problem.getId() + ",addLike";
            kafkaProducer.setDataForAccounting(data);
            problemRepository.save(problem);
            return true;
        } else {
            problem.getReactions().removeLike();
            problem.updateRating();
            data = problem.getSubscribers().contains(profile.getEmail()) || problem.getAuthorId().equals(profile.getEmail())
                    ? profile.getEmail() + "," + problem.getId() + ",removeLike"
                    : profile.getEmail() + "," + problem.getId() + ",removeLikeRemoveActivity";
            kafkaProducer.setDataForAccounting(data);
            problemRepository.save(problem);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean addDisLike(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        String data;
        boolean hasActivity = profile.getActivities().containsKey(problemId);
        if (!hasActivity) {
            problem.getReactions().addDislike();
            problem.updateRating();
            data = profile.getEmail() + "," + problem.getId() + ",addDislike";
            kafkaProducer.setDataForAccounting(data);
            return true;
        }
        boolean liked = profile.getActivities().get(problemId).getLiked();
        boolean disliked = profile.getActivities().get(problemId).getDisliked();

        if (!disliked) {
            problem.getReactions().addDislike();
            problem.updateRating();
            if (liked) {
                problem.getReactions().removeLike();
                problem.updateRating();
            }
            problemRepository.save(problem);
            data = profile.getEmail() + "," + problem.getId() + ",addDislike";
            kafkaProducer.setDataForAccounting(data);
            return true;
        } else {
            problem.getReactions().removeDislike();
            problem.updateRating();
            data = problem.getSubscribers().contains(profile.getEmail()) || problem.getAuthorId().equals(profile.getEmail())
                    ? profile.getEmail() + "," + problem.getId() + ",removeDislike"
                    : profile.getEmail() + "," + problem.getId() + ",removeDislikeRemoveActivity";
            kafkaProducer.setDataForAccounting(data);
            problemRepository.save(problem);
            return false;
        }
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
            editProfile(profile);
            problemRepository.delete(problem);
            return problemDto;
        } else
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not author of that problem, or you have no roles to delete it");
    }

    private void editProfile(ProfileDto profile) {
        kafkaConsumer.setProfile(profile);
    }
}
