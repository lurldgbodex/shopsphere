package shopsphere_payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shopsphere_shared.dto.CustomResponse;
import shopsphere_shared.handler.CustomExceptionHandler;

@RestControllerAdvice
public class PaymentExceptionHandler extends CustomExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public CustomResponse handlePayment(PaymentException ex) {
        return new CustomResponse("failure", ex.getMessage());
    }
}
