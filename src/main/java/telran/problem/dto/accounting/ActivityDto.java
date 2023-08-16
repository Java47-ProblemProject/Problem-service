package telran.problem.dto.accounting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor //must be deleted after test

@Getter
public class ActivityDto {
    @Setter
    protected Boolean liked;
    @Setter
    protected Boolean disliked;
}