package telran.problem.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import telran.problem.dao.ProblemCustomRepository;
import telran.problem.dao.ProblemRepository;
import telran.problem.kafka.kafkaDataDto.SolutionDataDto.SolutionMethodName;
import telran.problem.kafka.kafkaDataDto.SolutionDataDto.SolutionServiceDataDto;
import telran.problem.kafka.kafkaDataDto.accounting.ProfileDto;
import telran.problem.kafka.kafkaDataDto.commentDataDto.CommentMethodName;
import telran.problem.kafka.kafkaDataDto.commentDataDto.CommentServiceDataDto;
import telran.problem.kafka.kafkaDataDto.problemDataDto.ProblemServiceDataDto;
import telran.problem.model.Problem;
import telran.problem.model.ProfileDetails;
import telran.problem.model.Status;
import telran.problem.security.JwtTokenService;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Configuration
public class KafkaConsumer {
    private final ProblemRepository problemRepository;
    private final ProblemCustomRepository problemCustomRepository;
    private final JwtTokenService jwtTokenService;
    private ProfileDto profile;
    private String token;

    @Bean
    @Transactional
    protected Consumer<Map<String, ProfileDto>> receiveProfile() {
        return data -> {
            if (!data.isEmpty()) {
                Map.Entry<String, ProfileDto> entry = data.entrySet().iterator().next();
                if (entry.getValue().getUsername().equals("DELETED_PROFILE")) {
                    //profile was deleted ->
                    jwtTokenService.deleteCurrentProfileToken(entry.getValue().getEmail());
                    problemCustomRepository.deleteProblemsByAuthorId(entry.getValue().getEmail());
                    this.profile = null;
                    this.token = null;
                } else {
                    if (this.profile != null && entry.getValue().getEmail().equals(this.profile.getEmail()) && !entry.getValue().getUsername().equals(this.profile.getUsername())) {
                        problemCustomRepository.changeAuthorName(entry.getValue().getEmail(), entry.getValue().getUsername());
                    }
                    this.profile = entry.getValue();
                    if (!entry.getKey().isEmpty()) {
                        this.token = entry.getKey();
                    }
                    jwtTokenService.setCurrentProfileToken(this.profile.getEmail(), this.token);
                    System.out.println("Token pushed - " + this.token);
                }
            }
        };
    }

    @Bean
    @Transactional
    protected Consumer<CommentServiceDataDto> receiveDataFromComment() {
        return data -> {
            String problemId = data.getProblemId();
            CommentMethodName methodName = data.getMethodName();
            String commentId = data.getCommentsId();
            if (methodName.equals(CommentMethodName.ADD_COMMENT)) {
                Problem problem = problemRepository.findById(problemId).get();
                problem.addComment(commentId);
                Set<String> problemSubscribers = problem.getInteractions().getSubscriptions().stream().map(ProfileDetails::getProfileId).collect(Collectors.toSet());
                if (!problemSubscribers.contains(profile.getEmail())) {
                    problem.getInteractions().setSubscription(profile.getEmail(), profile.getStats().getRating());
                }
                problem.calculateRating();
                problemRepository.save(problem);
            }
            if (methodName.equals(CommentMethodName.DELETE_COMMENT)) {
                Problem problem = problemRepository.findById(problemId).get();
                problem.removeComment(commentId);
                problem.calculateRating();
                problemRepository.save(problem);
            }
        };
    }

    @Bean
    @Transactional
    protected Consumer<SolutionServiceDataDto> receiveDataFromSolution() {
        return data -> {
            String profileId = data.getProfileId();
            String problemId = data.getProblemId();
            SolutionMethodName methodName = data.getMethodName();
            String commentId = data.getSolutionId();
            if (methodName.equals(SolutionMethodName.ADD_SOLUTION)) {
                Problem problem = problemRepository.findById(problemId).get();
                problem.addSolution(commentId);
                problem.calculateRating();
                problem.setStatus(Status.PENDING);
                problemRepository.save(problem);
            }
            if (methodName.equals(SolutionMethodName.DELETE_SOLUTION) || methodName.equals(SolutionMethodName.DELETE_SOLUTION_AND_PROBLEM)) {
                Problem problem = problemRepository.findById(problemId).get();
                problem.removeSolution(commentId);
                problem.calculateRating();
                problem.setStatus(problem.getSolutions().isEmpty() ? Status.OPENED : Status.PENDING);
                problemRepository.save(problem);
            }
        };
    }
}
