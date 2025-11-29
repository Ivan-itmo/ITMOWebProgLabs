package result;

import java.io.Serializable;

public class Error implements Serializable {
    private String message;
    public Error(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
