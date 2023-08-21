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
import telran.problem.dto.exceptions.ProblemNotFoundException;
import telran.problem.model.Problem;

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

    @Bean
    @Transactional
    protected Consumer<ProfileDto> receiveUpdatedProfile() {
        return data -> {
            this.profile = data;
        };
    }

    @Bean
    @Transactional
    protected Consumer<String> receiveComment() {
        return data -> {
            String[] checkedData = data.split(",");
            String problemId = checkedData[0];
            String typeId = checkedData[1];
            Problem problem = problemRepository.findById(problemId).orElseThrow(ProblemNotFoundException::new);
            problem.addComment(typeId);
            problemRepository.save(problem);
        };
    }

    @Bean
    @Transactional
    protected Consumer<String> receiveNewName() {
        return data ->{
            String authorId = data.split(",")[0];
            String newName = data.split(",")[1];
            problemCustomRepository.changeAuthorName(authorId, newName);
        };
    }

    @Bean
    @Transactional
    protected Consumer<String> receiveAuthorToRemove() {
        return data -> {
            problemCustomRepository.deleteProblemsByAuthorId(data);
        };
    }
}
