package telran.problem.kafka;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import telran.problem.kafka.kafkaDataDto.problemDataDto.ProblemServiceDataDto;

import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class KafkaProducer {
    @Setter
    private ProblemServiceDataDto problemData;

    @Bean
    public Supplier<ProblemServiceDataDto> sendData() {
        return () -> {
            if (problemData != null) {
                ProblemServiceDataDto sentMessage = problemData;
                problemData = null;
                return sentMessage;
            }
            return null;
        };
    }
}
