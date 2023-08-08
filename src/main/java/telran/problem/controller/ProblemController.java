package telran.problem.controller;


import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import telran.problem.dto.*;

import telran.problem.service.ProblemService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/problem")
public class ProblemController {

    private final ProblemService problemService;
    @PostMapping("/createproblem")
    public CreateProblemDto addProblem(@RequestBody CreateProblemDto problem) {
        return problemService.addProblem(problem);
    }

//    @PutMapping("/{problemId}")
//    public ResponseEntity<EditProblemDto> editProblem(
//            @PathVariable String problemId,
//            @RequestBody EditProblemDto editProblemDto) {
//        EditProblemDto updatedProblem = problemService.editProblem(problemId, editProblemDto);
//        return ResponseEntity.ok(updatedProblem);
//    }

}

