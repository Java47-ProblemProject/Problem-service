package telran.problem.service;

import telran.problem.dto.problem.CreateProblemDto;
import telran.problem.dto.problem.DonationDto;
import telran.problem.dto.problem.EditProblemDto;
import telran.problem.dto.problem.ProblemDto;


import java.util.List;


public interface ProblemService {
    ProblemDto addProblem(CreateProblemDto problemDto);
    ProblemDto editProblem(EditProblemDto problem,String userId,String problemId);
    ProblemDto deleteProblem(String problemId, String userId);
    boolean addLike(String problemId);
    boolean addDisLike(String problemId);
    boolean subscribe(String problemId);
    boolean donate(String problemId, DonationDto donation);
    boolean unsubscribe(String problemId);
    ProblemDto findProblemById(String problemId);
    List<ProblemDto> getProblems();
    Double getCurrentAwardByProblemId(String problemId);
    ProblemDto deleteProblem(String problemId);
}
