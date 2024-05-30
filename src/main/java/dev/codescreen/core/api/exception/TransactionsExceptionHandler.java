package dev.codescreen.core.api.exception;

import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.modelling.command.AggregateNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@ControllerAdvice
public class TransactionsExceptionHandler {

    @ExceptionHandler(value = {UserIdNotFoundException.class})
    public ResponseEntity<Object> handleUserIdNotFoundException (UserIdNotFoundException userIdNotFoundException) {
        TransactionsException transactionsException = new TransactionsException(
                userIdNotFoundException.getMessage(),
                userIdNotFoundException.getCode()
        );
        return new ResponseEntity<>(transactionsException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ServerInternalErrorException.class})
    public ResponseEntity<Object> handleServerInternalErrorException (ServerInternalErrorException serverInternalErrorException) {
        TransactionsException transactionsException = new TransactionsException(
                serverInternalErrorException.getMessage(),
                serverInternalErrorException.getCode()
        );
        return new ResponseEntity<>(transactionsException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {NotValidAmountException.class})
    public ResponseEntity<Object> handleNotValidAmountException (NotValidAmountException notValidAmountException) {
        TransactionsException transactionsException = new TransactionsException(
                notValidAmountException.getMessage(),
                notValidAmountException.getCode()
        );
        return new ResponseEntity<>(transactionsException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {RequestFieldsMissingException.class})
    public ResponseEntity<Object> handleRequestFieldsMissingException (RequestFieldsMissingException requestFieldsMissingException) {
        TransactionsException transactionsException = new TransactionsException(
                requestFieldsMissingException.getMessage(),
                requestFieldsMissingException.getCode()
        );
        return new ResponseEntity<>(transactionsException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {WrongTransactionTypeException.class})
    public ResponseEntity<Object> handleWrongEndpointException (WrongTransactionTypeException wrongTransactionTypeException) {
        TransactionsException transactionsException = new TransactionsException(
                wrongTransactionTypeException.getMessage(),
                wrongTransactionTypeException.getCode()
        );
        return new ResponseEntity<>(transactionsException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidCurrencyException.class})
    public ResponseEntity<Object> handleInvalidCurrencyException (InvalidCurrencyException invalidCurrencyException) {
        TransactionsException transactionsException = new TransactionsException(
                invalidCurrencyException.getMessage(),
                invalidCurrencyException.getCode()
        );
        return new ResponseEntity<>(transactionsException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidMessageIdException.class})
    public ResponseEntity<Object> handleMessageIdNotMatchingException (InvalidMessageIdException invalidMessageIdException) {
        TransactionsException transactionsException = new TransactionsException(
                invalidMessageIdException.getMessage(),
                invalidMessageIdException.getCode()
        );
        return new ResponseEntity<>(transactionsException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException (MethodArgumentNotValidException methodArgumentNotValidException) {
        String errors = methodArgumentNotValidException.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        String response = "Validation failed. " + errors;
        TransactionsException transactionsException = new TransactionsException(
                response,
                "400"
        );
        return new ResponseEntity<>(transactionsException, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler({CommandExecutionException.class, AggregateNotFoundException.class})
    public ResponseEntity<Object> handleCommandExecutionException (CommandExecutionException commandExecutionException) {
        String errors = commandExecutionException.getMessage();
        if (errors.equals("The aggregate was not found in the event store")) {
            errors = "UserId not registered, register and try again";
        }

        String response = "Validation failed. " + errors;
        TransactionsException transactionsException = new TransactionsException(
                response,
                "400"
        );
        return new ResponseEntity<>(transactionsException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<Object> handleHttpMessageNotReadableException (HttpMessageNotReadableException httpMessageNotReadableException) {
        String error = httpMessageNotReadableException.getMessage();
        String response = "Invalid arguments in the request header, " + error;
        TransactionsException transactionsException = new TransactionsException(
                response,
                "400"
        );
        return new ResponseEntity<>(transactionsException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException (HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {
        String response = httpRequestMethodNotSupportedException.getMessage();
        TransactionsException transactionsException = new TransactionsException(
                response,
                "405"
        );
        return new ResponseEntity<>(transactionsException, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<Object> handleNoResourceFoundException (NoResourceFoundException noResourceFoundException) {
        String response = noResourceFoundException.getMessage();
        TransactionsException transactionsException = new TransactionsException(
                response,
                "404"
        );
        return new ResponseEntity<>(transactionsException, HttpStatus.NOT_FOUND);
    }
}
