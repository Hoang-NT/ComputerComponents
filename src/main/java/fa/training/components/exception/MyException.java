package fa.training.components.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MyException extends RuntimeException{
    private String code;
    private String message;
}
