package kr.ac.knu.comit.payment.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentDetailResponse {
    private final String orderId;
    private final String status;
    private final LocalDateTime approvedAt;
    private final boolean historyIncluded;
}
