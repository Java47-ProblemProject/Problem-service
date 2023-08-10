package telran.problem.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor

public class Donation {

    protected String userId;
    protected Double amount;
    protected LocalDateTime dateDonated;

}
