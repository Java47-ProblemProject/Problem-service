package telran.problem.dto.problem;

import lombok.Getter;

import java.util.Set;

@Getter
public class EditProblemDto {
    protected String title;
    protected String details;
    protected Set<String> communityNames;
}
