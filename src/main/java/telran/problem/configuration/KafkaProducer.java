package telran.problem.configuration;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import telran.problem.dto.accounting.ProfileDto;
import telran.problem.dto.problems.ProblemDto;

import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class KafkaProducer {
    private final StreamBridge streamBridge;
    @Setter
    private ProfileDto profile;
    @Setter
    private ProfileDto profileToComment;
    @Setter
    private String problemIdToDelete;
    @Setter
    private ProblemDto problem;

    @Bean
    public Supplier<ProfileDto> sendUpdatedProfile() {
        return () -> {
            if (profile != null) {
                streamBridge.send("sendUpdatedProfile-out-0", profile);
                ProfileDto sentMessage = profile;
                profile = null;
                return sentMessage;
            }
            return null;
        };
    }

    @Bean
    public Supplier<ProfileDto> sendProfileToComment() {
        return () -> {
            if (profileToComment != null) {
                streamBridge.send("sendAuthenticatedProfileToComment-out-0",profileToComment);
                ProfileDto sentMessage = profileToComment;
                profileToComment = null;
                return sentMessage;
            }
            return null;
        };
    }

    @Bean
    public Supplier<ProblemDto> sendProblemToComment() {
        return () -> {
            if (problem != null) {
                System.out.println("Problem : " + problem);
                streamBridge.send("sendProblemToComment-out-0", problem);
                ProblemDto sentMessage = problem;
                problem = null;
                return sentMessage;
            }
            return null;
        };
    }

    @Bean
    public Supplier<String> sendProblemIdToDelete() {
        return () -> {
            if (problemIdToDelete != null) {
                streamBridge.send("sendProblemIdToDelete-out-0", problemIdToDelete);
                String sentMessage = problemIdToDelete;
                problemIdToDelete = null;
                return sentMessage;
            }
            return null;
        };
    }
}
