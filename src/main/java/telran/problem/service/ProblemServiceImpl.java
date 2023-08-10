package telran.problem.service;



import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import telran.problem.dao.ProblemRepository;
import telran.problem.dto.problems.CreateProblemDto;
import telran.problem.dto.problems.DonationDto;
import telran.problem.dto.problems.EditProblemDto;
import telran.problem.dto.problems.ProblemDto;
import telran.problem.model.Donation;
import telran.problem.model.Problem;
import telran.problem.dto.exceptions.ProblemNotFoundException;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ProblemServiceImpl implements ProblemService {
    final ProblemRepository problemRepository;
    final ModelMapper modelMapper;


    @Override
    public ProblemDto addProblem(CreateProblemDto problemDto) {
        Problem problem = modelMapper.map(problemDto, Problem.class);
        problem = problemRepository.save(problem);
        return modelMapper.map(problem, ProblemDto.class);
    }

    @Override
    public ProblemDto editProblem(EditProblemDto editProblemDto,String userId, String problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
        problem.setTitle(editProblemDto.getTitle());
        problem.setDetails(editProblemDto.getDetails());
        problem.setCommunityNames(editProblemDto.getCommunityNames());
        Problem updatedProblem = problemRepository.save(problem);
        return modelMapper.map(updatedProblem, ProblemDto.class);
    }


    @Override
    public ProblemDto deleteProblem(String problemId) {
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
        problem.getReactions().addDislike();
        problem.updateRating();
        problemRepository.save(problem);
        return true;
    }


    @Override
    public boolean subscribed(String problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
        String userResponded = "Blah-blah-blah"; // hardcoded, userId to be received from ProfileService via Kafka
        problem.getSubscribers().add(userResponded);
        problemRepository.save(problem);
       return true;
    }

    @Override
    public boolean donate(String problemId, DonationDto amount){
        Problem problemToDonate = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        String userId = "Donation maker1"; // hardcoded, userId to be received from ProfileService via Kafka
        DonationDto donation = modelMapper.map(amount,DonationDto.class);
        donation.setAmount(amount.getAmount());
        donation.setUserId(userId);
        donation.setDateDonated(amount.getDateDonated());
        problemToDonate.getDonationHistory().add(modelMapper.map(amount,Donation.class));
        Double newCurrentAward = problemToDonate.getCurrentAward();
        problemToDonate.setCurrentAward(newCurrentAward + donation.getAmount());
        problemRepository.save(problemToDonate);
        return true;
    }

    @Override
    public boolean unsubscribed(String problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
        String userToBeUnsibscribed = "Blah-blah-blah";// hardcoded, userId to be received from ProfileService via Kafka
        boolean deletedSuccessfully = problem.getSubscribers().remove(userToBeUnsibscribed);
        problemRepository.save(problem);
        return deletedSuccessfully;
    }

    @Override
    public ProblemDto findProblemById(String problemId) {
        Problem problem = problemRepository.findById(problemId)
                                            .orElseThrow(ProblemNotFoundException::new);
        return modelMapper.map(problem, ProblemDto.class);
    }


    @Override
    public List<ProblemDto> getProblems() {
        Set<Problem> problems = problemRepository.findAllByAuthorIsNotNull();
        return problems.stream()
                .map(problem -> modelMapper.map(problem, ProblemDto.class))
                .collect(Collectors.toList());
    }
    @Override
    public Double getCurrentAwardByProblemId(String problemId){
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
