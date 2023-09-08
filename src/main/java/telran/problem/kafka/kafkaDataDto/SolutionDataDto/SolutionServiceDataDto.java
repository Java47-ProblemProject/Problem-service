package telran.problem.kafka.kafkaDataDto.SolutionDataDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SolutionServiceDataDto {
    private String profileId;
    private String problemId;
    private String solutionId;
    private SolutionMethodName methodName;
}
