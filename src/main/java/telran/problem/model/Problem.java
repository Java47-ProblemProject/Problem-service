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
    @Setter
    protected Set<String> communityNames;
    @Setter
    protected String details;
    protected LocalDateTime dateCreated;
    @Setter
    protected Double currentAward;
    protected Reactions reactions;
    protected Set<Donation> donationHistory;
    protected Set<String> comments;
    protected Set<String> solutions;
    protected Set<String> subscribers;

    public Problem(Double currentAward) {
        this.currentAward = currentAward;
    }

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


    public void updateRating() {
        double w1 = 1.0; // Weight for likes since we don't know what's going to be Weight value.
        double w2 = 1.0; // Weight for likes since we don't know what's going to be Weight value.
        int totalLikes = reactions != null ? reactions.getTotalLikes() : 0;
        double totalDonations = donationHistory != null ?
               donationHistory.stream().mapToDouble(Donation::getAmount).sum() : 0.0;
        double newRating = w1 * totalLikes + w2 * totalDonations;
        this.rating = (int) Math.round(newRating);
    }

}
