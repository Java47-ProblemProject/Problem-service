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
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(collection = "problems")
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
        this.reactions = new Reactions(0, 0);
        this.communityNames = new HashSet<>();
        this.donationHistory = new ArrayList<>();
        this.comments = new HashSet<>();
        this.solutions = new HashSet<>();
        this.subscribers = new HashSet<>();
        this.dateCreated = LocalDateTime.now();
        this.type = "PROBLEM";
    }

    public void addSolution(String solutionId) {
        this.solutions.add(solutionId);
    }

    public void addComment(String commentId) {
        this.comments.add(commentId);
    }

    public void removeComment(String commentId) {
        this.comments.remove(commentId);
    }
    public void removeSolution(String solutionId) {
        this.solutions.remove(solutionId);
    }

    public void addSubscriber(String profileId) {
        this.subscribers.add(profileId);
    }

    public void removeSubscriber(String profileId) {
        this.subscribers.remove(profileId);
    }

    public void updateRating() {
        double w1 = 1.0;
        double w2 = 1.0;
        int likes = this.reactions.getLikes();
        int disLikes = this.reactions.getDislikes();
        double totalDonations = donationHistory.stream().mapToDouble(Donation::getAmount).sum();
        int total = likes - disLikes;
        double newRating = (total > 0) ? w1 * total + w2 * totalDonations : w1 * 0 + w2 * totalDonations;
        this.rating = (int) Math.round(newRating);
    }

    public void checkCurrentAward() {
        double totalDonations = !this.donationHistory.isEmpty() ?
                donationHistory.stream().mapToDouble(Donation::getAmount).sum() : 0.0;
        if (totalDonations != currentAward) {
            setCurrentAward(totalDonations);
        }
    }

    public void addDonation(Donation donation) {
        this.donationHistory.add(donation);
    }


}
