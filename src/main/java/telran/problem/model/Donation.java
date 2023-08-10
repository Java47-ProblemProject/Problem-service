package telran.problem.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter


public class Donation {
    @Id
    protected String userName;
    @Setter
    protected Double amount;
    protected LocalDateTime dateDonated;

    public Donation() {
        this.userName = userName;
        //this.amount = amount;
        this.dateDonated = LocalDateTime.now();
    }
}
