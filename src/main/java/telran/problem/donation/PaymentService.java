package telran.problem.donation;

public interface PaymentService {
    PaymentResult processPayment(String cardNumber, String cardHolder, String expirationDate, String cvv, double amount);
}
