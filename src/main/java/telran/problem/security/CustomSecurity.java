package telran.problem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.problem.configuration.KafkaConsumer;
import telran.problem.dao.ProblemRepository;
import telran.problem.dto.accounting.ProfileDto;
import telran.problem.model.Problem;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomSecurity {
    final KafkaConsumer kafkaConsumer;
    final ProblemRepository problemRepository;
    public boolean checkProblemAuthor(String problemId){
        Problem problem = problemRepository.findById(problemId).orElseThrow(NoSuchElementException::new);
        ProfileDto user = kafkaConsumer.getProfile();
        return user.getEmail().equals(problem.getAuthorId());
    }
}
