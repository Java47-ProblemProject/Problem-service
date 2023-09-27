package telran.problem.service;

import telran.problem.dto.CreateProblemDto;
import telran.problem.dto.DonationDto;
import telran.problem.dto.EditProblemDto;
import telran.problem.dto.ProblemDto;


import java.util.List;
import java.util.Set;


public interface ProblemService {
    ProblemDto addProblem(CreateProblemDto problemDto);
    ProblemDto editProblem(EditProblemDto problem,String userId,String problemId);
    ProblemDto deleteProblem(String problemId, String userId);
    ProblemDto addLike(String problemId);
    ProblemDto addDisLike(String problemId);
    ProblemDto subscribe(String problemId);
    ProblemDto donate(String problemId, DonationDto donation);
    ProblemDto getProblemById(String problemId);
    Set<ProblemDto> findProblemsByCommunities(Set<String> communities);
    Set<ProblemDto> findProblemsByProfileId(String profileId);
    Set<ProblemDto> getProblems();
    Double getCurrentAwardByProblemId(String problemId);
    ProblemDto deleteProblem(String problemId);
}
