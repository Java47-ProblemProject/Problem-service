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
import telran.problem.dto.kafkaData.commentDataDto.CommentMethodName;
import telran.problem.dto.kafkaData.commentDataDto.CommentServiceDataDto;
import telran.problem.dto.kafkaData.SolutionDataDto.SolutionMethodName;
import telran.problem.dto.kafkaData.SolutionDataDto.SolutionServiceDataDto;
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
            if (data.getUsername().equals("DELETED_PROFILE")) {
                //profile was deleted ->
                problemCustomRepository.deleteProblemsByAuthorId(data.getEmail());
                this.profile = new ProfileDto();
            } else if (this.profile != null && data.getEmail().equals(profile.getEmail()) && !data.getUsername().equals(profile.getUsername())) {
                problemCustomRepository.changeAuthorName(data.getEmail(), data.getUsername());
                this.profile = data;
            } else this.profile = data;

        };
    }

    @Bean
    @Transactional
    protected Consumer<CommentServiceDataDto> receiveDataFromComment() {
        return data -> {
            String profileId = data.getProfileId();
            String problemId = data.getProblemId();
            CommentMethodName methodName = data.getMethodName();
            String commentId = data.getCommentsId();
            System.out.println("COMMENT DATA RECEIVED" + profileId + problemId + methodName + commentId);
            if (methodName.equals(CommentMethodName.ADD_COMMENT)) {
                Problem problem = problemRepository.findById(problemId).get();
                problem.addComment(commentId);
                problemRepository.save(problem);
            }
            if (methodName.equals(CommentMethodName.DELETE_COMMENT) || methodName.equals(CommentMethodName.DELETE_COMMENT_AND_PROBLEM)) {
                Problem problem = problemRepository.findById(problemId).get();
                problem.removeComment(commentId);
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
            System.out.println("SOLUTION DATA RECEIVED" + profileId + problemId + methodName + commentId);
            if (methodName.equals(SolutionMethodName.ADD_SOLUTION)) {
                Problem problem = problemRepository.findById(problemId).get();
                problem.addSolution(commentId);
                problemRepository.save(problem);
            }
            if (methodName.equals(SolutionMethodName.DELETE_SOLUTION) || methodName.equals(SolutionMethodName.DELETE_SOLUTION_AND_PROBLEM)) {
                Problem problem = problemRepository.findById(problemId).get();
                problem.removeSolution(commentId);
                problemRepository.save(problem);
            }
        };
    }
}
