package telran.problem.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import telran.problem.dto.accounting.ProfileDto;

import java.util.function.Consumer;

@Getter
@Configuration
public class KafkaConsumer {
    @Setter
    ProfileDto profile;

    @Bean
    protected Consumer<ProfileDto> receiveData() {
        return data -> {
            System.out.println(" - " + data.getEmail());
            profile = data;
        };
    }
}
