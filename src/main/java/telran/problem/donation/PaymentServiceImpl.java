package telran.problem.donation;

import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Override
    public PaymentResult processPayment(String cardNumber, String cardHolder, String expirationDate, String cvv, double amount) {


        return new PaymentResult(true, "Payment successful");
    }
}
