package telran.problem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.problem.dao.ProblemRepository;
import telran.problem.dto.exceptions.WrongAuthorityException;
import telran.problem.kafka.KafkaConsumer;
import telran.problem.kafka.kafkaDataDto.profileDataDto.ProfileDataDto;
import telran.problem.model.Problem;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomSecurity {
    final KafkaConsumer kafkaConsumer;
    final ProblemRepository problemRepository;

    public boolean checkProblemAuthor(String problemId, String authorId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(NoSuchElementException::new);
        ProfileDataDto profile = kafkaConsumer.getProfile();
        if (authorId.equals(profile.getEmail()) && authorId.equals(problem.getAuthorId())){
            return true;
        }else{
            throw new WrongAuthorityException();
        }
    }
}
