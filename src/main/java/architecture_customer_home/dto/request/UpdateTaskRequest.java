package architecture_customer_home.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import architecture_customer_home.enums.Priority;
import architecture_customer_home.enums.TaskStatus;

public record UpdateTaskRequest(
        String description,
        Priority priority,
        TaskStatus status,
        LocalDateTime dueDate,
        String assignedTo
) {
}
