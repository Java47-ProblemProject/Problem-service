package telran.problem.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Donation {
    @Setter
    protected String profileId;
    @Setter
    protected String profileName;
    @Setter
    protected Double amount;
    @Setter
    protected LocalDateTime dateDonated;

    public Donation(String profileId, String profileName, Double amount) {
        this.profileId = profileId;
        this.profileName = profileName;
        this.amount = amount;
        this.dateDonated = LocalDateTime.now();
    }
}
