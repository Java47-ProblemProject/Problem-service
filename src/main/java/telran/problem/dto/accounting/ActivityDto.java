package telran.problem.dto.accounting;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor //must be deleted after test

@Getter
public class ActivityDto {
    protected String problemId;
    protected Boolean liked;
    protected Boolean disliked;
}