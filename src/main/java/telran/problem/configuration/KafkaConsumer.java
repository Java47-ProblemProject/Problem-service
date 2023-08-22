package telran.problem.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import telran.problem.dao.ProblemCustomRepository;
import telran.problem.dao.ProblemRepository;
import telran.problem.dto.accounting.ProfileDto;

import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
@Configuration
public class KafkaConsumer {
    final ProblemRepository problemRepository;
    final ProblemCustomRepository problemCustomRepository;
    @Setter
    ProfileDto profile;

    @Bean
    @Transactional
    protected Consumer<ProfileDto> receiveProfile() {
        return data -> {
            this.profile = data;
        };
    }
}
