package kr.ac.knu.comit.payment.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentConfirmResponse {
    private final String status;
    private final LocalDateTime approvedAt;
    private final String orderId;
    private final Integer amount;
}
