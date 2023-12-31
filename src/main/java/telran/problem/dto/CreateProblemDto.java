package telran.problem.dto;

import lombok.*;

import java.util.Set;

@Setter
public class CreateProblemDto {
    protected String author;
    protected String title;
    protected String details;
    protected Set<String> communityNames;
}