package fa.training.components.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseObject {
    private String code;
    private String message;
    private Object data;

    public ResponseObject(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseObject(Object data) {
        this.code = "200";
        this.message = "Success";
        this.data = data;
    }
}
