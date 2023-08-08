package telran.problem.dto;

import lombok.*;

import java.util.Set;


@Getter



public class ProblemDto {
    protected String author;
    protected String title;
    protected String details;
    @Singular
    protected Set<String> communityNames;
}
