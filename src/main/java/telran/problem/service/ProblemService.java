package telran.problem.service;

import telran.problem.dto.CreateProblemDto;
import telran.problem.dto.EditProblemDto;
import telran.problem.dto.ProblemDto;
import java.util.Set;

public interface ProblemService {


    CreateProblemDto addProblem(CreateProblemDto problemDto);

    EditProblemDto editProblem(String userId, String problemId, EditProblemDto Problem);
    ProblemDto deleteProblem(String problemId);

    boolean addLike(String problemId);
    boolean addDisLike(String problemId);

    boolean subscribed(String problemId);

    boolean unsubscribed(String problemId);

    ProblemDto findProblemById(String problemId);

    Set<ProblemDto> getProblems();

}
