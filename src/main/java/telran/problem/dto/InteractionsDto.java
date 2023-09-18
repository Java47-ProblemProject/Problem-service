package telran.problem.dto;

import lombok.Getter;
import telran.problem.model.Donation;
import telran.problem.model.ProfileDetails;

import java.util.List;
import java.util.Set;

@Getter
public class InteractionsDto {
    protected Integer totalLikes;
    protected Integer totalDislikes;
    protected Double likeWeight;
    protected Double dislikeWeight;
    protected Integer totalDonations;
    protected Integer totalSubscribers;
    protected List<Donation> donations;
    protected Set<ProfileDetails> likes;
    protected Set<ProfileDetails> dislikes;
    protected Set<ProfileDetails> subscriptions;
}
