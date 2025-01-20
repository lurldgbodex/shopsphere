package shopsphere_logging.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopsphere_logging.dto.request.PaymentRequest;
import shopsphere_logging.dto.response.PaymentResponse;
import shopsphere_logging.service.PaymentService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(
            @RequestBody @Valid PaymentRequest request,
            @RequestHeader  HttpHeaders headers) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.processPayment(request, headers));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentByID(
            @PathVariable UUID paymentId,
            @RequestHeader HttpHeaders headers) {

        return ResponseEntity.ok(paymentService
                .getPaymentById(paymentId, headers));
    }
}
