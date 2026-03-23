package kr.ac.knu.comit.payment.controller.api;

import jakarta.validation.Valid;
import kr.ac.knu.comit.docs.annotation.ApiContract;
import kr.ac.knu.comit.docs.annotation.ApiDoc;
import kr.ac.knu.comit.docs.annotation.Example;
import kr.ac.knu.comit.docs.annotation.FieldDesc;
import kr.ac.knu.comit.payment.dto.PaymentConfirmRequest;
import kr.ac.knu.comit.payment.dto.PaymentConfirmResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@ApiContract
public interface PaymentControllerApi {

    @ApiDoc(
            summary = "결제 승인",
            descriptions = {
                    @FieldDesc(name = "orderId", value = "주문 ID"),
                    @FieldDesc(name = "amount", value = "결제 금액 (원 단위)"),
                    @FieldDesc(name = "paymentKey", value = "토스 결제 고유 키"),
                    @FieldDesc(name = "status", value = "결제 상태"),
                    @FieldDesc(name = "approvedAt", value = "결제 승인 시각")
            },
            example = @Example(
                    request = """
                            {
                              "orderId": "ORDER-001",
                              "amount": 15000,
                              "paymentKey": "pay_123456789"
                            }
                            """,
                    response = """
                            {
                              "status": "DONE",
                              "approvedAt": "2024-01-01T12:00:00",
                              "orderId": "ORDER-001",
                              "amount": 15000
                            }
                            """
            )
    )
    @PostMapping("/confirm")
    ResponseEntity<PaymentConfirmResponse> confirmPayment(@Valid @RequestBody PaymentConfirmRequest request);
}
