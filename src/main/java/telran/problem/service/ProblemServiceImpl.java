package telran.problem.service;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import telran.problem.dao.ProblemRepository;
import telran.problem.dto.CreateProblemDto;
import telran.problem.dto.EditProblemDto;
import telran.problem.dto.ProblemDto;
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
    public CreateProblemDto addProblem(CreateProblemDto problemDto) {
        Problem problem = modelMapper.map(problemDto, Problem.class);
        System.out.println(problem);
        problem = problemRepository.save(problem);
        return modelMapper.map(problem, CreateProblemDto.class);
    }

    @Override
    public EditProblemDto editProblem(String userId, String problemId, EditProblemDto editProblemDto) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);
        problem.setTitle(editProblemDto.getTitle());
        problem.setDetails(editProblemDto.getDetails());
        Problem updatedProblem = problemRepository.save(problem);
        return modelMapper.map(updatedProblem, EditProblemDto.class);
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
        problemRepository.save(problem);
        return true;
    }

    @Override
    public boolean addDisLike(String problemId) {
        Problem problem = problemRepository.findById(problemId)
                                            .orElseThrow(ProblemNotFoundException::new);
        problem.getReactions().addDislike();
        problemRepository.save(problem);
        return true;
    }

    @Override
    public boolean subscribed(String problemId) {
        return false;
    }

    @Override
    public boolean unsubscribed(String problemId) {
        return false;
    }

    @Override
    public ProblemDto findProblemById(String problemId) {
        Problem problem = problemRepository.findById(problemId)
                                            .orElseThrow(ProblemNotFoundException::new);
        return modelMapper.map(problem, ProblemDto.class);
    }


    @Override
    public Set<ProblemDto> getProblems() {
        List<Problem> problems = problemRepository.findAll();
        return problems.stream()
                        .map(problem -> modelMapper.map(problem, ProblemDto.class))
                        .collect(Collectors.toSet());
    }
}