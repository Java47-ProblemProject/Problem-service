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
    protected String details;
    protected LocalDateTime dateCreated;
    @Setter
    protected Status status;
    @Setter
    protected Double currentAward;
    protected Double rating;
    @Setter
    protected Set<String> communityNames;
    protected Interactions interactions;
    protected Set<String> comments;
    protected Set<String> solutions;
    protected String type;

    public Problem() {
        this.rating = 0.;
        this.status = Status.OPENED;
        this.currentAward = 0.;
        this.communityNames = new HashSet<>();
        this.comments = new HashSet<>();
        this.solutions = new HashSet<>();
        this.dateCreated = LocalDateTime.now();
        this.interactions = new Interactions();
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

    public void calculateRating() {
        double likeWeight = interactions.getLikeWeight();
        double dislikeWeight = interactions.getDislikeWeight();
        double commentsWeight = 0.1 * comments.size();
        double subscribersWeight = 0.2 * interactions.getTotalSubscribers();
        double solutionsWeight = 0.3 * solutions.size();
        double donationsWeight = 0.5 * (interactions.getTotalDonations() + interactions.getDonations().stream().mapToDouble(Donation::getAmount).sum());
        double newRating = likeWeight - dislikeWeight + commentsWeight + subscribersWeight + solutionsWeight + donationsWeight;
        this.rating = Double.parseDouble(String.format("%.2f", newRating));
    }

    public void checkCurrentAward() {
        setCurrentAward(this.interactions.getDonations().stream().mapToDouble(Donation::getAmount).sum());
    }
}
