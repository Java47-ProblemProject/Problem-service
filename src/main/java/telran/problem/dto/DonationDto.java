package telran.problem.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class DonationDto {
    protected String userId;
    protected Double amount;
    protected LocalDateTime dateDonated;
    public void setAmount(Double amount){
        this.amount = amount;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }
    public void setDateDonated(LocalDateTime dateDonated) {
        this.dateDonated = LocalDateTime.now();
    }



}


