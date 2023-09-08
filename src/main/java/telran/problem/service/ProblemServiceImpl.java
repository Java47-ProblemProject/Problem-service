package telran.problem.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import telran.problem.kafka.KafkaConsumer;
import telran.problem.kafka.KafkaProducer;
import telran.problem.dao.ProblemRepository;
import telran.problem.kafka.kafkaDataDto.accounting.ProfileDto;
import telran.problem.dto.exceptions.ProblemNotFoundException;
import telran.problem.kafka.kafkaDataDto.problemDataDto.ProblemMethodName;
import telran.problem.kafka.kafkaDataDto.problemDataDto.ProblemServiceDataDto;
import telran.problem.dto.CreateProblemDto;
import telran.problem.dto.DonationDto;
import telran.problem.dto.EditProblemDto;
import telran.problem.dto.ProblemDto;
import telran.problem.model.Problem;
import telran.problem.model.ProfileDetails;

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
        problem.calculateRating();
        checkAndSubscribe(problem, profile);
        problem = problemRepository.save(problem);
        ProblemDto problemDto = modelMapper.map(problem, ProblemDto.class);
        ProblemServiceDataDto data = addDataToTransfer(profile, problem, ProblemMethodName.ADD_PROBLEM);
        kafkaProducer.setProblemData(data);
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
            ProblemServiceDataDto data = addDataToTransfer(profile, updatedProblem, ProblemMethodName.EDIT_PROBLEM);
            kafkaProducer.setProblemData(data);
            return modelMapper.map(updatedProblem, ProblemDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not author of that problem");
    }

    @Override
    @Transactional
    public ProblemDto deleteProblem(String problemId, String userId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (problem.getAuthorId().equals(profile.getEmail()) && userId.equals(profile.getEmail())) {
            ProblemServiceDataDto data = addDataToTransfer(profile, problem, ProblemMethodName.DELETE_PROBLEM);
            kafkaProducer.setProblemData(data);
            problemRepository.delete(problem);
            return modelMapper.map(problem, ProblemDto.class);
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not author of that problem");
    }

    @Override
    @Transactional
    public boolean addLike(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        Double profileRating = profile.getStats().getRating();
        boolean result = problem.getInteractions().setLike(profile.getEmail(), profileRating);
        checkAndSubscribe(problem, profile);
        problem.calculateRating();
        problemRepository.save(problem);
        ProblemServiceDataDto data = addDataToTransfer(profile, problem, ProblemMethodName.ADD_LIKE);
        kafkaProducer.setProblemData(data);
        return result;
    }

    @Override
    @Transactional
    public boolean addDisLike(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        Double profileRating = profile.getStats().getRating();
        boolean result = problem.getInteractions().setDislike(profile.getEmail(), profileRating);
        checkAndSubscribe(problem, profile);
        problem.calculateRating();
        problemRepository.save(problem);
        ProblemServiceDataDto data = addDataToTransfer(profile, problem, ProblemMethodName.ADD_DISLIKE);
        kafkaProducer.setProblemData(data);
        return result;
    }

    @Override
    @Transactional
    public boolean subscribe(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        Double profileRating = profile.getStats().getRating();
        boolean result = problem.getInteractions().setSubscription(profile.getEmail(), profileRating);
        problem.calculateRating();
        problemRepository.save(problem);
        ProblemServiceDataDto data = addDataToTransfer(profile, problem, ProblemMethodName.SUBSCRIBE);
        kafkaProducer.setProblemData(data);
        return result;
    }

    @Override
    @Transactional
    public boolean donate(String problemId, DonationDto amount) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        problem.getInteractions().setDonation(profile.getEmail(), profile.getUsername(), amount.getAmount());
        problem.checkCurrentAward();
        problem.calculateRating();
        checkAndSubscribe(problem, profile);
        problemRepository.save(problem);
        ProblemServiceDataDto data = addDataToTransfer(profile, problem, ProblemMethodName.DONATE);
        kafkaProducer.setProblemData(data);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemDto findProblemById(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        ProblemServiceDataDto data = addDataToTransfer(profile, problem, ProblemMethodName.ADD_PROBLEM);
        kafkaProducer.setProblemData(data);
        return modelMapper.map(problem, ProblemDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ProblemDto> findProblemsByCommunities(Set<String> communities) {
        return problemRepository.findAllByCommunityNamesContaining(communities)
                .map(p -> modelMapper.map(p, ProblemDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ProblemDto> findProblemsByProfileId(String profileId) {
        return problemRepository.findAllByAuthorId(profileId)
                .map(p -> modelMapper.map(p, ProblemDto.class))
                .collect(Collectors.toSet());
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
        return problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new).getCurrentAward();
    }

    //Administrative block
    @Override
    @Transactional
    public ProblemDto deleteProblem(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        if (problem.getAuthorId().equals(profile.getEmail()) || profile.getRoles().contains("ADMINISTRATOR")) {
            ProblemServiceDataDto data = addDataToTransfer(profile, problem, ProblemMethodName.DELETE_PROBLEM);
            kafkaProducer.setProblemData(data);
            problemRepository.delete(problem);
            return modelMapper.map(problem, ProblemDto.class);
        } else
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "You are not author of that problem, or you have no roles to delete it");
    }

    private ProblemServiceDataDto addDataToTransfer(ProfileDto profile, Problem problem, ProblemMethodName methodName) {
        Set<String> subscribers = problem.getInteractions().getSubscriptions().stream().map(ProfileDetails::getProfileId).collect(Collectors.toSet());
        return new ProblemServiceDataDto(profile.getEmail(), problem.getId(), problem.getAuthorId(), problem.getRating(), methodName, problem.getComments(), problem.getSolutions(), subscribers, problem.getCommunityNames());
    }

    private void checkAndSubscribe(Problem problem, ProfileDto profile){
        Set<String> problemSubscribers = problem.getInteractions().getSubscriptions().stream().map(ProfileDetails::getProfileId).collect(Collectors.toSet());
        if (!problemSubscribers.contains(profile.getEmail())) {
            problem.getInteractions().setSubscription(profile.getEmail(), profile.getStats().getRating());
        }
    }
}
