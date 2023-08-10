package telran.problem.dto;

import lombok.*;
import telran.problem.model.Donation;
import telran.problem.model.Reactions;

import java.time.LocalDateTime;
import java.util.Set;


@Getter

public class ProblemDto {
    protected String id;
    protected String author;
    protected Integer rating;

    protected String title;

    protected Set<String> communityNames;

    protected String details;
    protected LocalDateTime dateCreated;
    protected Double currentAward;
    protected Reactions reactions;

    protected Set<Donation> donationHistory;

    protected Set<String> comments;

    protected Set<String> solutions;

    protected Set<String> subscribers;

}
