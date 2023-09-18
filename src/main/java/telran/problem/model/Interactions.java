package telran.problem.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Interactions {
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

    public Interactions() {
        this.totalLikes = 0;
        this.totalDislikes = 0;
        this.likeWeight = 0.;
        this.dislikeWeight = 0.;
        this.totalDonations = 0;
        this.totalSubscribers = 0;
        this.donations = new ArrayList<>();
        this.likes = new HashSet<>();
        this.dislikes = new HashSet<>();
        this.subscriptions = new HashSet<>();
    }

    public boolean setLike(String profileId, Double profileRating) {
        boolean isLiked = this.likes.stream().anyMatch(profileDetails -> profileDetails.getProfileId().equals(profileId));
        if (isLiked) {
            ProfileDetails profileToRemove = this.likes.stream()
                    .filter(profileDetails -> profileDetails.getProfileId().equals(profileId))
                    .findFirst()
                    .get();
            this.likes.remove(profileToRemove);
            this.totalLikes--;
            this.likeWeight -= profileToRemove.getProfileRating();
        } else {
            ProfileDetails newLike = new ProfileDetails(profileId, profileRating);
            this.likes.add(newLike);
            this.totalLikes++;
            this.likeWeight += profileRating;
        }
        return !isLiked;
    }

    public boolean setDislike(String profileId, Double profileRating) {
        boolean isDisliked = this.dislikes.stream().anyMatch(profileDetails -> profileDetails.getProfileId().equals(profileId));
        if (isDisliked) {
            ProfileDetails profileToRemove = this.dislikes.stream()
                    .filter(profileDetails -> profileDetails.getProfileId().equals(profileId))
                    .findFirst()
                    .get();
            this.dislikes.remove(profileToRemove);
            this.totalDislikes--;
            this.dislikeWeight -= profileToRemove.getProfileRating();
        } else {
            ProfileDetails newDislike = new ProfileDetails(profileId, profileRating);
            this.dislikes.add(newDislike);
            this.totalDislikes++;
            this.dislikeWeight += profileRating;
        }
        return !isDisliked;
    }

    public void setDonation(String profileId, String profileName, double amount) {
        Donation donation = new Donation(profileId, profileName, amount);
        this.donations.add(donation);
        this.totalDonations = this.donations.size();
    }

    public boolean setSubscription(String profileId, Double profileRating) {
        ProfileDetails profileDetails = new ProfileDetails(profileId, profileRating);
        if (this.subscriptions.contains(profileDetails)) {
            this.subscriptions.remove(profileDetails);
            this.totalSubscribers = this.subscriptions.size();
            return false;
        } else {
            this.subscriptions.add(profileDetails);
            this.totalSubscribers = this.subscriptions.size();
            return true;
        }
    }

}
