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
        ProfileDetails profileDetails = new ProfileDetails(profileId, profileRating);
        if (this.likes.contains(profileDetails)) {
            this.likes.remove(profileDetails);
            this.totalLikes--;
            this.likeWeight -= profileDetails.getProfileRating();
            return false;
        } else {
            if (this.dislikes.contains(profileDetails)) {
                this.dislikes.remove(profileDetails);
                this.totalDislikes--;
                this.dislikeWeight -= profileDetails.getProfileRating();
            }
            this.likes.add(profileDetails);
            this.totalLikes++;
            this.likeWeight += profileDetails.getProfileRating();
            return true;
        }
    }

    public boolean setDislike(String profileId, Double profileRating) {
        ProfileDetails profileDetails = new ProfileDetails(profileId, profileRating);
        if (this.dislikes.contains(profileDetails)) {
            this.dislikes.remove(profileDetails);
            this.totalDislikes--;
            this.likeWeight -= profileDetails.getProfileRating();
            return false;
        } else {
            if (this.likes.contains(profileDetails)) {
                this.likes.remove(profileDetails);
                this.totalLikes--;
                this.likeWeight -= profileDetails.getProfileRating();
            }
            this.dislikes.add(profileDetails);
            this.totalDislikes++;
            this.dislikeWeight += profileDetails.getProfileRating();
            return true;
        }
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
