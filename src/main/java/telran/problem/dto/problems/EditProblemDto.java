package telran.problem.dto.problems;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter


public class EditProblemDto {
    protected String title;
    protected String details;
    protected Set<String> communityNames;

}
