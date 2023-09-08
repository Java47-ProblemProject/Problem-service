package telran.problem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.problem.kafka.KafkaConsumer;
import telran.problem.dao.ProblemRepository;
import telran.problem.kafka.kafkaDataDto.accounting.ProfileDto;
import telran.problem.model.Problem;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomSecurity {
    final KafkaConsumer kafkaConsumer;
    final ProblemRepository problemRepository;

    public boolean checkProblemAuthor(String problemId, String authorId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(NoSuchElementException::new);
        ProfileDto profile = kafkaConsumer.getProfile();
        return authorId.equals(profile.getEmail()) && authorId.equals(problem.getAuthorId());
    }
}
