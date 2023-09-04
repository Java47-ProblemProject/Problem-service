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
    boolean addLike(String problemId);
    boolean addDisLike(String problemId);
    boolean subscribe(String problemId);
    boolean donate(String problemId, DonationDto donation);
    ProblemDto findProblemById(String problemId);
    public Set<ProblemDto> findProblemsByCommunities(Set<String> communities);
    public Set<ProblemDto> findProblemsByProfileId(String profileId);
    List<ProblemDto> getProblems();
    Double getCurrentAwardByProblemId(String problemId);
    ProblemDto deleteProblem(String problemId);
}
