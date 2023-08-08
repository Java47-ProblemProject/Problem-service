package telran.problem.model;

import lombok.*;
import org.springframework.data.annotation.Id;


import java.time.LocalDateTime;
import java.util.*;

@ToString
@Getter
public class Problem {
    @Id
    protected String id;
    protected String author;
    protected Integer rating;
    @Setter
    protected String title;
    protected Set<String> communityNames;
    @Setter
    protected String details;
    protected LocalDateTime dateCreated;
    protected Integer currentAward;
    protected Reactions reactions;
    protected Set<Donation> donationHistory;
    protected Set<String> comments;
    protected Set<String> solutions;
    protected Set<String> subscribers;

    public Problem() {
        this.communityNames = new HashSet<>();
        this.donationHistory = new HashSet<>();
        this.comments = new HashSet<>();
        this.solutions = new HashSet<>();
        this.subscribers = new HashSet<>();
        this.dateCreated = LocalDateTime.now();
    }
}
