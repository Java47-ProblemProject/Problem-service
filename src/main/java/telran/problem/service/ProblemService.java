package telran.problem.service;

import telran.problem.dto.problems.CreateProblemDto;
import telran.problem.dto.problems.DonationDto;
import telran.problem.dto.problems.EditProblemDto;
import telran.problem.dto.problems.ProblemDto;

import java.util.List;


public interface ProblemService {


    ProblemDto addProblem(CreateProblemDto problemDto);

    ProblemDto editProblem(EditProblemDto problem,String userId,String problemId);
    ProblemDto deleteProblem(String problemId, String userId);

    boolean addLike(String problemId);
    boolean addDisLike(String problemId);

    boolean subscribed(String problemId);

    void updateRating(String problemId);

    boolean donate(String problemId, DonationDto donation);


    boolean unsubscribed(String problemId);

    ProblemDto findProblemById(String problemId);

    List<ProblemDto> getProblems();


    Double getCurrentAwardByProblemId(String problemId);

    ProblemDto deleteProblemAdmin(String problemId);
}
