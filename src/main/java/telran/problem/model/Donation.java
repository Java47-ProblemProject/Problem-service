package telran.problem.model;

import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

public class Donation {
    protected String userName;
    protected Integer amount;
    protected LocalDateTime date;
}
