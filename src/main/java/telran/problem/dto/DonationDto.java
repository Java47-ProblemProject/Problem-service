package telran.problem.dto;

import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class DonationDto {
    protected String userName;
    protected Double amount;
    protected LocalDateTime dateDonated;

}


