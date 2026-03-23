package kr.ac.knu.comit.payment.service;

import java.time.LocalDateTime;
import kr.ac.knu.comit.payment.dto.PaymentConfirmRequest;
import kr.ac.knu.comit.payment.dto.PaymentConfirmResponse;
import kr.ac.knu.comit.payment.dto.PaymentDetailResponse;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public PaymentConfirmResponse confirm(PaymentConfirmRequest request) {
        return new PaymentConfirmResponse(
                "DONE",
                LocalDateTime.of(2024, 1, 1, 12, 0),
                request.getOrderId(),
                request.getAmount()
        );
    }

    public PaymentDetailResponse getPayment(String orderId, boolean includeHistory) {
        return new PaymentDetailResponse(
                orderId,
                "DONE",
                LocalDateTime.of(2024, 1, 1, 12, 0),
                includeHistory
        );
    }
}
