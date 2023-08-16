package telran.problem.dto.accounting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StatsDto {
    protected Integer solvedProblems;
    protected Integer checkedSolutions;
    protected Integer formulatedProblems;
    protected Integer rating;
}
