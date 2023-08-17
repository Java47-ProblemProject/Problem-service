package telran.problem.configuration;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import telran.problem.dto.accounting.ProfileDto;

import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class KafkaProducer {
    private final StreamBridge streamBridge;
    @Setter
    private ProfileDto profile;
    @Setter
    private String problemIdToDelete;

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
