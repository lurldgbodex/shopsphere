package shopsphere_shared.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import shopsphere_shared.dto.CustomResponse;
import shopsphere_shared.dto.ValidationException;
import shopsphere_shared.exceptions.BadRequestException;
import shopsphere_shared.exceptions.ConflictException;
import shopsphere_shared.exceptions.NotFoundException;
import shopsphere_shared.exceptions.UnauthorizedException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationException invalidArgumentHandler(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return new ValidationException(errors);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public CustomResponse handleConflictException(ConflictException ex) {
        return setResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public CustomResponse handleNotFoundException(NotFoundException ex) {
        return setResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public CustomResponse handleBadRequestException(BadRequestException ex) {
        return setResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public CustomResponse handleUnauthorizedException(UnauthorizedException ex) {
        return setResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public CustomResponse handleHttpMediaNotSupported(HttpMediaTypeNotSupportedException ex) {
        return setResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CustomResponse handleHttpNotReadable(HttpMessageNotReadableException ex) {
        return setResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public CustomResponse handleRequestNotSupported(HttpRequestMethodNotSupportedException ex) {
        return setResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public CustomResponse handleNoHandlerFound(NoHandlerFoundException ex) {
        return setResponse(ex.getMessage());
    }

    CustomResponse setResponse(String message) {
        return new CustomResponse("failure", message);
    }
}
