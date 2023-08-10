package telran.problem.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;
import java.util.*;

@Getter
@EqualsAndHashCode(of = "id")
@Document(collection = "problems")
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
    protected Double currentAward;
    protected Reactions reactions;
    protected Set<Donation> donationHistory;
    protected Set<String> comments;
    protected Set<String> solutions;
    protected Set<String> subscribers;

    public Problem() {
        this.rating = 0;
        this.currentAward = 0.0;
        this.reactions = new Reactions(0,0);
        this.communityNames = new HashSet<>();
        this.donationHistory = new HashSet<>();
        this.comments = new HashSet<>();
        this.solutions = new HashSet<>();
        this.subscribers = new HashSet<>();
        this.dateCreated = LocalDateTime.now();
    }

    public void setCommunityNames(Set<String> communityNames) {
        this.communityNames = communityNames;
    }
    public void setSubscribers(Set<String> subscribers){
        this.subscribers = subscribers;
    }
}
