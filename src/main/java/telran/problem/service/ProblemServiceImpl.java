package telran.problem.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import telran.problem.configuration.KafkaConsumer;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ProblemServiceImpl implements ProblemService {
    final ProblemRepository problemRepository;
    final ModelMapper modelMapper;
    final KafkaConsumer kafkaConsumer;

    @Override
    public ProblemDto addProblem(CreateProblemDto problemDto) {
        Problem problem = modelMapper.map(problemDto, Problem.class);
        ProfileDto profile = kafkaConsumer.getProfile();
        problem.setAuthor(profile.getUsername());
        problem.setAuthorId(profile.getEmail());
        problem = problemRepository.save(problem);
        profile.addActivity(problem.getId(), new ActivityDto(false,false));
        kafkaConsumer.setProfile(profile);
        //send profile back to Accounting to update it in ProfileRepository.
        return modelMapper.map(problem, ProblemDto.class);
    }

    @Override
    public ProblemDto editProblem(EditProblemDto editProblemDto, String userId, String problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
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
    public ProblemDto deleteProblem(String problemId, String userId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (problem.getAuthorId().equals(profile.getEmail()) && userId.equals(profile.getEmail())) {
            problemRepository.delete(problem);
            profile.removeActivity(problemId);
            kafkaConsumer.setProfile(profile);
            //send profile back to Accounting to update it in ProfileRepository.
            return modelMapper.map(problem, ProblemDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not author of that problem");
    }

    @Override
    public boolean addLike(String problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ActivityDto activity = profile.getActivities().computeIfAbsent(problemId, a -> new ActivityDto(false, false));
        profile.getActivities().entrySet().forEach(System.out::println);
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
            kafkaConsumer.setProfile(profile);
            // send profile back to Accounting to update it in ProfileRepository.
            return true;
        }
        return false;
    }

    @Override
    public boolean addDisLike(String problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ActivityDto activity = profile.getActivities().computeIfAbsent(problemId, a -> new ActivityDto(false, false));
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
            kafkaConsumer.setProfile(profile);
            // send profile back to Accounting to update it in ProfileRepository.
            return true;
        }
        return false;
    }


    @Override
    public boolean subscribed(String problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
        String userId = "Blah-blah-blah"; // hardcoded, userId to be received from ProfileService via Kafka
        if (problem.getSubscribers().contains(userId)) {
            return false;
        }
        problem.addSubscriber(userId);
        problemRepository.save(problem);
        return true;
    }

    @Override
    public boolean unsubscribed(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        String userId = "Blah-blah-blah"; // hardcoded, userId to be received from ProfileService via Kafka
        if (problem.getSubscribers().contains(userId)) {
            return false;
        }
        problem.removeSubscriber(userId);
        problemRepository.save(problem);
        return true;
    }

    @Override
    public boolean donate(String problemId, DonationDto amount) {
        Problem problemToDonate = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        String userId = "Donation maker1"; // hardcoded, userId to be received from ProfileService via Kafka
        Donation donation = modelMapper.map(amount, Donation.class);
        donation.setAmount(amount.getAmount());
        donation.setUserId(userId);
        donation.setDateDonated(LocalDateTime.now());
        problemToDonate.addDonation(donation);
        problemToDonate.checkCurrentAward();
        problemRepository.save(problemToDonate);
        return true;
    }

    @Override
    public ProblemDto findProblemById(String problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
        return modelMapper.map(problem, ProblemDto.class);
    }

    @Override
    public List<ProblemDto> getProblems() {
        return problemRepository.findAll().stream().map(e -> modelMapper.map(e, ProblemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Double getCurrentAwardByProblemId(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        return problem.getCurrentAward();
    }

    //Administrative block
    @Override
    public ProblemDto deleteProblem(String problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (problem.getAuthorId().equals(profile.getEmail()) || profile.getRoles().contains("ADMINISTRATOR")) {
            problemRepository.delete(problem);
            return modelMapper.map(problem, ProblemDto.class);
        } else
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not author of that problem, or you have no roles to delete it");
    }
}
