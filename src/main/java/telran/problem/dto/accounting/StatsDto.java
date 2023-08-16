package telran.problem.dto.accounting;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor //must be deleted after test

@Getter
public class StatsDto {
    protected Integer solvedProblems;
    protected Integer checkedSolutions;
    protected Integer formulatedProblems;
    protected Integer rating;
}