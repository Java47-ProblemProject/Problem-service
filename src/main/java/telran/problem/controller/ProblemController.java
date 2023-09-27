package telran.problem.controller;


import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import telran.problem.dto.CreateProblemDto;
import telran.problem.dto.DonationDto;
import telran.problem.dto.EditProblemDto;
import telran.problem.dto.ProblemDto;
import telran.problem.service.ProblemService;


import java.util.List;
import java.util.Set;


@RequiredArgsConstructor
@RestController
@RequestMapping("/problem")
@CrossOrigin(origins = "http://127.0.0.1:5173", allowedHeaders = "*")
public class ProblemController {
    private final ProblemService problemService;

    @PostMapping("/createproblem")
    public ProblemDto addProblem(@RequestBody CreateProblemDto problem) {
        return problemService.addProblem(problem);
    }

    @PutMapping("/editproblem/{userId}/{problemId}")
    public ProblemDto editProblem(@RequestBody EditProblemDto problem, @PathVariable String userId, @PathVariable String problemId) {
        return problemService.editProblem(problem, userId, problemId);
    }

    @DeleteMapping("/deleteproblem/{userId}/{problemId}")
    public ProblemDto deleteProblem(@PathVariable String problemId, @PathVariable String userId) {
        return problemService.deleteProblem(problemId, userId);
    }

    @PutMapping("/likeproblem/{problemId}")
    public ProblemDto likeProblem(@PathVariable String problemId) {
        return problemService.addLike(problemId);
    }

    @PutMapping("/dislikeproblem/{problemId}")
    public ProblemDto dislikeProblem(@PathVariable String problemId) {
        return problemService.addDisLike(problemId);
    }

    @GetMapping("/getproblem/{problemId}")
    public ProblemDto getProblem(@PathVariable String problemId) {
        return problemService.getProblemById(problemId);
    }

    @GetMapping("/getcomunityproblems")
    public Set<ProblemDto> findProblemsByCommunities(@RequestBody Set<String> problemIds){
        return problemService.findProblemsByCommunities(problemIds);
    }

    @GetMapping("/getproblems/{profileId}")
    public Set<ProblemDto> findProblemsByProfileId(@PathVariable String profileId){
        return problemService.findProblemsByProfileId(profileId);
    }

    @GetMapping("/getproblems")
    public Set<ProblemDto> getProblems() {
        return problemService.getProblems();
    }

    @PutMapping("/subscribeonproblem/{problemId}")
    public ProblemDto subscribe(@PathVariable String problemId) {
        return problemService.subscribe(problemId);
    }

    @PutMapping("/donate/{problemId}")
    public ProblemDto donate(@PathVariable String problemId, @RequestBody DonationDto donation) {
        return problemService.donate(problemId, donation);
    }

    @GetMapping("/getcurrentaward/{problemId}")
    public Double getCurrAward(@PathVariable String problemId) {
        return problemService.getCurrentAwardByProblemId(problemId);
    }

    //Administrative block
    @DeleteMapping("/deleteproblem/{problemId}")
    public ProblemDto deleteProblem(@PathVariable String problemId) {
        return problemService.deleteProblem(problemId);
    }
}

