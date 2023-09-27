package telran.problem.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import telran.problem.dao.ProblemCustomRepository;
import telran.problem.dao.ProblemRepository;
import telran.problem.donation.PaymentService;
import telran.problem.dto.CreateProblemDto;
import telran.problem.dto.DonationDto;
import telran.problem.dto.EditProblemDto;
import telran.problem.dto.ProblemDto;
import telran.problem.dto.exceptions.ProblemNotFoundException;
import telran.problem.kafka.KafkaConsumer;
import telran.problem.kafka.KafkaProducer;
import telran.problem.kafka.kafkaDataDto.problemDataDto.ProblemMethodName;
import telran.problem.kafka.kafkaDataDto.problemDataDto.ProblemServiceDataDto;
import telran.problem.kafka.kafkaDataDto.profileDataDto.ProfileDataDto;
import telran.problem.model.Problem;
import telran.problem.model.ProfileDetails;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {
    final ProblemRepository problemRepository;
    final ProblemCustomRepository problemCustomRepository;
    final ModelMapper modelMapper;
    final KafkaConsumer kafkaConsumer;
    final KafkaProducer kafkaProducer;
    final PaymentService paymentService;

    @Override
    @Transactional
    public ProblemDto addProblem(CreateProblemDto newProblem) {
        Problem problem = modelMapper.map(newProblem, Problem.class);
        ProfileDataDto profile = kafkaConsumer.getProfiles().get(SecurityContextHolder.getContext().getAuthentication().getName());
        problem.setAuthor(profile.getUserName());
        problem.setAuthorId(profile.getEmail());
        problem.calculateRating();
        checkAndSubscribe(problem, profile);
        problem = problemRepository.save(problem);
        ProblemDto problemDto = modelMapper.map(problem, ProblemDto.class);
        transferData(profile, problem, ProblemMethodName.ADD_PROBLEM);
        return problemDto;
    }

    @Override
    @Transactional
    public ProblemDto editProblem(EditProblemDto editProblemDto, String userId, String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDataDto profile = kafkaConsumer.getProfiles().get(SecurityContextHolder.getContext().getAuthentication().getName());
        problem.setTitle(editProblemDto.getTitle());
        problem.setDetails(editProblemDto.getDetails());
        problem.setCommunityNames(editProblemDto.getCommunityNames());
        Problem updatedProblem = problemRepository.save(problem);
        transferData(profile, updatedProblem, ProblemMethodName.EDIT_PROBLEM);
        return modelMapper.map(updatedProblem, ProblemDto.class);
    }

    @Override
    @Transactional
    public ProblemDto deleteProblem(String problemId, String userId) {
        return getDeletedProblemDto(problemId);

    }

    @Override
    @Transactional
    public ProblemDto addLike(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDataDto profile = kafkaConsumer.getProfiles().get(SecurityContextHolder.getContext().getAuthentication().getName());
        Double profileRating = profile.getRating();
        problem.getInteractions().setLike(profile.getEmail(), profileRating);
        checkAndSubscribe(problem, profile);
        problem.calculateRating();
        problemRepository.save(problem);
        transferData(profile, problem, ProblemMethodName.ADD_LIKE);
        return modelMapper.map(problem, ProblemDto.class);
    }

    @Override
    @Transactional
    public ProblemDto addDisLike(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDataDto profile = kafkaConsumer.getProfiles().get(SecurityContextHolder.getContext().getAuthentication().getName());
        Double profileRating = profile.getRating();
        problem.getInteractions().setDislike(profile.getEmail(), profileRating);
        checkAndSubscribe(problem, profile);
        problem.calculateRating();
        problemRepository.save(problem);
        transferData(profile, problem, ProblemMethodName.ADD_DISLIKE);
        return modelMapper.map(problem, ProblemDto.class);
    }

    @Override
    @Transactional
    public ProblemDto subscribe(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDataDto profile = kafkaConsumer.getProfiles().get(SecurityContextHolder.getContext().getAuthentication().getName());
        Double profileRating = profile.getRating();
        problem.getInteractions().setSubscription(profile.getEmail(), profileRating);
        problem.calculateRating();
        problemRepository.save(problem);
        transferData(profile, problem, ProblemMethodName.SUBSCRIBE);
        return modelMapper.map(problem, ProblemDto.class);
    }

    @Override
    @Transactional
    public ProblemDto donate(String problemId, DonationDto amount) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDataDto profile = kafkaConsumer.getProfiles().get(SecurityContextHolder.getContext().getAuthentication().getName());

       //paymentService = paymentService.processPayment(cardNumber, cardHolder, expirationDate, cvv, amount);

        problem.getInteractions().setDonation(profile.getEmail(), profile.getUserName(), amount.getAmount());
        problem.checkCurrentAward();
        problem.calculateRating();
        checkAndSubscribe(problem, profile);
        problemRepository.save(problem);
        transferData(profile, problem, ProblemMethodName.DONATE);
        return modelMapper.map(problem, ProblemDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ProblemDto getProblemById(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDataDto profile = kafkaConsumer.getProfiles().get(SecurityContextHolder.getContext().getAuthentication().getName());
        transferData(profile, problem, ProblemMethodName.GET_PROBLEM);
        return modelMapper.map(problem, ProblemDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ProblemDto> findProblemsByCommunities(Set<String> communities) {
        return problemRepository.findAllByCommunityNamesContainingOrderByDateCreatedDesc(communities)
                .map(p -> modelMapper.map(p, ProblemDto.class))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<ProblemDto> findProblemsByProfileId(String profileId) {
        return problemCustomRepository.findAllByProfileId(profileId).stream()
                .map(p -> modelMapper.map(p, ProblemDto.class))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ProblemDto> getProblems() {
        return problemRepository.findAllByOrderByDateCreatedDesc().map(e -> modelMapper.map(e, ProblemDto.class))
                .collect(Collectors.toCollection(LinkedHashSet::new));
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
        return getDeletedProblemDto(problemId);
    }

    private ProblemDto getDeletedProblemDto(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        ProfileDataDto profile = kafkaConsumer.getProfiles().get(SecurityContextHolder.getContext().getAuthentication().getName());
        transferData(profile, problem, ProblemMethodName.DELETE_PROBLEM);
        problemRepository.delete(problem);
        return modelMapper.map(problem, ProblemDto.class);
    }

    private void checkAndSubscribe(Problem problem, ProfileDataDto profile) {
        Set<String> problemSubscribers = problem.getInteractions().getSubscriptions().stream().map(ProfileDetails::getProfileId).collect(Collectors.toSet());
        if (!problemSubscribers.contains(profile.getEmail())) {
            problem.getInteractions().setSubscription(profile.getEmail(), profile.getRating());
        }
    }

    private void transferData(ProfileDataDto profile, Problem problem, ProblemMethodName methodName) {
        Set<String> subscribers = problem.getInteractions().getSubscriptions().stream().map(ProfileDetails::getProfileId).collect(Collectors.toSet());
        ProblemServiceDataDto data = new ProblemServiceDataDto(profile.getEmail(), problem.getId(), problem.getAuthorId(), problem.getRating(), methodName, problem.getComments(), problem.getSolutions(), subscribers, problem.getCommunityNames());
        kafkaProducer.setProblemData(data);
    }
}
