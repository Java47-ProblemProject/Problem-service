package telran.problem.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import telran.problem.configuration.KafkaConsumer;
import telran.problem.dao.ProblemRepository;
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
        return modelMapper.map(problem, ProblemDto.class);
    }

    @Override
    public ProblemDto editProblem(EditProblemDto editProblemDto, String userId, String problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (problem.getAuthorId().equals(profile.getEmail())) {
            problem.setTitle(editProblemDto.getTitle());
            problem.setDetails(editProblemDto.getDetails());
            problem.setCommunityNames(editProblemDto.getCommunityNames());
        }
        Problem updatedProblem = problemRepository.save(problem);
        return modelMapper.map(updatedProblem, ProblemDto.class);
    }


    @Override
    public ProblemDto deleteProblem(String problemId, String userId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
        problemRepository.delete(problem);
        return modelMapper.map(problem, ProblemDto.class);
    }

    @Override
    public boolean addLike(String problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
        problem.getReactions().addLike();
        problem.updateRating();
        problemRepository.save(problem);
        return true;
    }

    @Override
    public boolean addDisLike(String problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);

        int initialLikes = problem.getReactions().getLikes();
        int initialDislikes = problem.getReactions().getDislikes();

        problem.getReactions().addDislike();
        problem.updateRating();
        problemRepository.save(problem);

        int finalLikes = problem.getReactions().getLikes();
        int finalDislikes = problem.getReactions().getDislikes();

        if (initialLikes == finalLikes && initialDislikes == finalDislikes) {
            return false;
        }

        if (initialLikes > finalLikes) {
            problem.getReactions().subtractLike();
        }

        return true;
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

    @Override
    public void updateRating(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        if (problem != null) {
            problem.updateRating();
            problemRepository.save(problem);
        }
    }

}
