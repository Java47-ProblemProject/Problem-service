package telran.problem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Service;
import telran.problem.configuration.KafkaConsumer;
import telran.problem.dao.ProblemRepository;
import telran.problem.dto.accounting.ProfileDto;
import telran.problem.model.Problem;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

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
