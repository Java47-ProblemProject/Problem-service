package telran.problem.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor

public class Donation {
    @Setter
    protected String userId;
    @Setter
    protected Double amount;
    @Setter
    protected LocalDateTime dateDonated;

}
