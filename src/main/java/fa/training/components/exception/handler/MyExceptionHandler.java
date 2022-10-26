package fa.training.components.exception.handler;

import fa.training.components.exception.MyException;
import fa.training.components.model.ResponseObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(MyException.class)
    public ResponseObject handleMyException(MyException e){
        return new ResponseObject(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseObject handleAuthenticationException() {
        return new ResponseObject("401", "Authentication fail");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseObject handleAccessDeniedException() {
        return new ResponseObject("403", "Access denied");
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseObject handleDateTimeParseException() {
        return new ResponseObject("402", "Cannot parse date from string. Make sure date entered in ISO format: YYYY-MM-DD");
    }

    @ExceptionHandler(Exception.class)
    public ResponseObject handleException(Exception e){
        return new ResponseObject("408", e.getMessage());
    }
}
