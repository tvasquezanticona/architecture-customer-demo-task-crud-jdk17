package architecture_customer_home.exception;

import org.springframework.http.HttpStatus;

public class InvalidTaskStateException extends ApiException {

    public InvalidTaskStateException(ErrorCode errorCode, String message) {
        super(errorCode, HttpStatus.CONFLICT, message);
    }
}