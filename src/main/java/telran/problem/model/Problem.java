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
    protected Double rating;
    @Setter
    protected Status status;
    protected List<Donation> donationHistory;
    protected Set<String> comments;
    protected Set<String> solutions;
    protected Set<String> subscribers;
    protected String type;

    public Problem() {
        this.rating = 0.;
        this.status = Status.OPENED;
        this.currentAward = 0.;
        this.reactions = new Reactions(0, 0., 0, 0.);
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

    public void calculateRating() {
        double reactionsWeight = this.reactions.getLikesWeight() - this.reactions.getDislikesWeight();
        double commentsWeight = 0.1 * this.comments.size();
        double subscribersWeight = 0.2 * this.subscribers.size();
        double solutionsWeight = 0.3 * this.solutions.size();
        double donationsWeight = 0.5 * (this.donationHistory.size() + this.donationHistory.stream().mapToDouble(Donation::getAmount).sum());
        double newRating = reactionsWeight + subscribersWeight + donationsWeight + commentsWeight + solutionsWeight;
        this.rating = Double.parseDouble(String.format("%.2f", newRating));
    }

    public void checkCurrentAward() {
        double totalDonations = !this.donationHistory.isEmpty() ? donationHistory.stream().mapToDouble(Donation::getAmount).sum() : 0.0;
        if (totalDonations != currentAward) {
            setCurrentAward(totalDonations);
        }
    }

    public void addDonation(Donation donation) {
        this.donationHistory.add(donation);
    }


}
