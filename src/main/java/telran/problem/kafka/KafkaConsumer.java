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
import telran.problem.kafka.kafkaDataDto.commentDataDto.CommentMethodName;
import telran.problem.kafka.kafkaDataDto.commentDataDto.CommentServiceDataDto;
import telran.problem.kafka.kafkaDataDto.profileDataDto.ProfileDataDto;
import telran.problem.kafka.kafkaDataDto.profileDataDto.ProfileMethodName;
import telran.problem.model.Problem;
import telran.problem.model.ProfileDetails;
import telran.problem.model.Status;
import telran.problem.security.JwtTokenService;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Configuration
public class KafkaConsumer {
    private final ProblemRepository problemRepository;
    private final ProblemCustomRepository problemCustomRepository;
    private final JwtTokenService jwtTokenService;
    private final Map<String, ProfileDataDto> profiles = new ConcurrentHashMap<>();

    @Bean
    @Transactional
    protected Consumer<ProfileDataDto> receiveProfile() {
        return data -> {
            String email = data.getEmail();
            String userName = data.getUserName();
            ProfileMethodName methodName = data.getMethodName();
            ProfileDataDto profile = this.profiles.get(email);
            if (!profiles.containsKey(email)) {
                this.profiles.put(email, data);
                profile = data;
            }
            if (methodName.equals(ProfileMethodName.SET_PROFILE)) {
                //if (jwtTokenService.getCurrentProfileToken(email) == null) {
                    jwtTokenService.setCurrentProfileToken(email, data.getToken());
               // }
                this.profiles.get(email).setToken("");
            } else if (methodName.equals(ProfileMethodName.UNSET_PROFILE)) {
                jwtTokenService.deleteCurrentProfileToken(email);
                this.profiles.remove(email);
            } else if (methodName.equals(ProfileMethodName.UPDATED_PROFILE)) {
                this.profiles.put(email, profile);
            } else if (methodName.equals(ProfileMethodName.EDIT_PROFILE_NAME)) {
                problemCustomRepository.changeAuthorName(email, userName);
                this.profiles.get(email).setUserName(profile.getUserName());
            } else if (methodName.equals(ProfileMethodName.DELETE_PROFILE)) {
                jwtTokenService.deleteCurrentProfileToken(email);
                problemCustomRepository.deleteProblemsByAuthorId(email);
                this.profiles.remove(email);
            }
        };
    }

    @Bean
    @Transactional
    protected Consumer<CommentServiceDataDto> receiveDataFromComment() {
        return data -> {
            String profileId = data.getProfileId();
            ProfileDataDto profile = this.profiles.get(profileId);

            String problemId = data.getProblemId();
            CommentMethodName methodName = data.getMethodName();
            String commentId = data.getCommentsId();
            if (methodName.equals(CommentMethodName.ADD_COMMENT)) {
                Problem problem = problemRepository.findById(problemId).get();
                problem.addComment(commentId);
                Set<String> problemSubscribers = problem.getInteractions().getSubscriptions().stream().map(ProfileDetails::getProfileId).collect(Collectors.toSet());
                if (!problemSubscribers.contains(profile.getEmail())) {
                    problem.getInteractions().setSubscription(profile.getEmail(), profile.getRating());
                }
                problem.calculateRating();
                problemRepository.save(problem);
            } else if (methodName.equals(CommentMethodName.DELETE_COMMENT)) {
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
            String problemId = data.getProblemId();
            SolutionMethodName methodName = data.getMethodName();
            String commentId = data.getSolutionId();
            if (methodName.equals(SolutionMethodName.ADD_SOLUTION)) {
                Problem problem = problemRepository.findById(problemId).get();
                problem.addSolution(commentId);
                problem.calculateRating();
                problem.setStatus(Status.PENDING);
                problemRepository.save(problem);
            } else if (methodName.equals(SolutionMethodName.DELETE_SOLUTION) || methodName.equals(SolutionMethodName.DELETE_SOLUTION_AND_PROBLEM)) {
                Problem problem = problemRepository.findById(problemId).get();
                problem.removeSolution(commentId);
                problem.calculateRating();
                problem.setStatus(problem.getSolutions().isEmpty() ? Status.OPENED : Status.PENDING);
                problemRepository.save(problem);
            }
        };
    }
}
