package telran.problem.dto.kafkaData.commentDataDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentServiceDataDto {
    private String profileId;
    private String problemId;
    private String commentsId;
    private CommentMethodName methodName;
}
