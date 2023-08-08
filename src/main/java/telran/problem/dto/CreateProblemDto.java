package telran.problem.dto;

import lombok.*;



import java.time.LocalDateTime;
import java.util.*;

@ToString
@Getter
public class CreateProblemDto {

    protected String id;
    protected String author;
    // protected Integer rating;
    @Setter
    protected String title;
    protected Set<String> communityNames;
    @Setter
    protected String details;
    protected LocalDateTime dateCreated;
    protected Integer currentAward;
//    protected Reactions reactions;
//    protected Set<Donation> donationHistory;
    protected Set<String> comments;
    protected Set<String> solutions;
    protected Set<String> subscribers;


}