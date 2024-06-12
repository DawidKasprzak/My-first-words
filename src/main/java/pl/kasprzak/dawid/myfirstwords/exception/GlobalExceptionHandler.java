package pl.kasprzak.dawid.myfirstwords.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ParentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleParentNotFoundException(ParentNotFoundException exception) {
        return exception.getMessage();
    }


    @ExceptionHandler(ChildNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleChildNotFoundException(ChildNotFoundException exception) {
        return exception.getMessage();
    }


    @ExceptionHandler(WordNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleWordNotFoundException(WordNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(MilestoneNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleMilestoneNotFoundException(MilestoneNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(DateValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleDateValidationException(DateValidationException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(InvalidDateOrderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidDateOrderException(InvalidDateOrderException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException exception) {
        return exception.getMessage();
    }
}
