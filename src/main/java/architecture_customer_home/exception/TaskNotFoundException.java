package architecture_customer_home.exception;

import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends ApiException {

    public TaskNotFoundException(Integer id) {
        super(ErrorCode.TASK_NOT_FOUND, HttpStatus.NOT_FOUND, "Task not found with id: " + id);
    }
}