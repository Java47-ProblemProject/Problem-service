package telran.problem.configuration;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class KafkaProducer {
    @Setter
    private String dataForAccounting;

    @Bean
    public Supplier<String> sendDataToAccounting() {
        return () -> {
            if (dataForAccounting != null) {
                String sentMessage = dataForAccounting;
                dataForAccounting = null;
                return sentMessage;
            }
            return null;
        };
    }

}
