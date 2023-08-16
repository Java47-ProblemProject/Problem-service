package telran.problem.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(of = "id")
@Document(collection = "problems")
@AllArgsConstructor
public class Problem {
    @Id
    protected String id;
    @Setter
    protected String author;
    @Setter
    protected String authorId;
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
    protected List<Donation> donationHistory;
    protected Set<String> comments;
    protected Set<String> solutions;
    protected Set<String> subscribers;

    protected String type;


    public Problem() {
        this.rating = 0;
        this.currentAward = 0.0;
        this.reactions = new Reactions(0,0);
        this.communityNames = new HashSet<>();
        this.donationHistory = new ArrayList<>();
        this.comments = new HashSet<>();
        this.solutions = new HashSet<>();
        this.subscribers = new HashSet<>();
        this.dateCreated = LocalDateTime.now();
        this.type = "problem";
    }
    public void addSolution(String solutionId){this.solutions.add(solutionId);}
    public void addComment(String commentId){
        this.comments.add(commentId);
    }
    public void addSubs(String profileId){this.subscribers.add(profileId);}
    public void removeSubs(String profileId) {this.subscribers.remove(profileId);}

    public void updateRating() {
        double w1 = 1.0; // Weight for likes since we don't know what's going to be Weight value.
        double w2 = 1.0; // Weight for likes since we don't know what's going to be Weight value.
        int totalLikes = reactions != null ? reactions.getTotalLikes() : 0;
        double totalDonations = donationHistory != null ?
               donationHistory.stream().mapToDouble(Donation::getAmount).sum() : 0.0;
        double newRating = w1 * totalLikes + w2 * totalDonations;
        this.rating = (int) Math.round(newRating);
    }
    public void checkCurrentAward() {
        double totalDonations = !this.donationHistory.isEmpty() ?
                donationHistory.stream().mapToDouble(Donation::getAmount).sum() : 0.0;
        if (totalDonations != currentAward) {
            setCurrentAward(totalDonations);
        }
    }
    public void addDonationHistory(Donation donation){
        this.donationHistory.add(donation);
    }


}
