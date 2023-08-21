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
    private ProblemDto problemToDelete;
    @Setter
    private ProblemDto problem;

    @Bean
    public Supplier<ProfileDto> sendUpdatedProfile() {
        return () -> {
            if (profile != null) {
                //streamBridge.send("sendUpdatedProfile-out-0", profile);
                ProfileDto sentMessage = profile;
                profile = null;
                return sentMessage;
            }
            return null;
        };
    }

    @Bean
    public Supplier<ProblemDto> sendProblemToComment() {
        return () -> {
            if (problem != null) {
                //streamBridge.send("sendProblemToComment-out-0", problem);
                ProblemDto sentMessage = problem;
                problem = null;
                return sentMessage;
            }
            return null;
        };
    }

    @Bean
    public Supplier<ProblemDto> sendProblemIdToDelete() {
        return () -> {
            if (problemToDelete != null) {
                //streamBridge.send("sendProblemIdToDelete-out-0", problemToDelete);
                ProblemDto sentMessage = problemToDelete;
                problemToDelete = null;
                return sentMessage;
            }
            return null;
        };
    }
}
