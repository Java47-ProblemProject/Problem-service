package telran.problem.dto.kafkaData.problemDataDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class ProblemServiceDataDto {
    private String authorizedProfileId;
    private String problemId;
    private String problemAuthorId;
    private Double problemRating;
    private ProblemMethodName methodName;
    private Set<String> comments;
    private Set<String> solutions;
    private Set<String> subscribers;
    private Set<String> communities;
}
