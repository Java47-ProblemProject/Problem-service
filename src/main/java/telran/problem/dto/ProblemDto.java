package telran.problem.dto;

import lombok.Getter;
import telran.problem.model.Interactions;
import telran.problem.model.Status;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class ProblemDto {
    protected String id;
    protected String author;
    protected String authorId;
    protected String title;
    protected String details;
    protected LocalDateTime dateCreated;
    protected Status status;
    protected Double currentAward;
    protected Double rating;
    protected Set<String> communityNames;
    protected Interactions interactions;
    protected Set<String> comments;
    protected Set<String> solutions;
    protected String type;
}
