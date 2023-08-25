package telran.problem.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
public class Donation {
    @Setter
    protected String userId;
    @Setter
    protected String userName;
    @Setter
    protected Double amount;
    @Setter
    protected LocalDateTime dateDonated;

    public Donation() {
        this.dateDonated = LocalDateTime.now();
    }
}
