package architecture_customer_home.dto.response;

import java.time.LocalDateTime;
import architecture_customer_home.enums.Priority;
import architecture_customer_home.enums.TaskStatus;

public record TaskResponse(
        Integer id,
        String description,
        Priority priority,
        TaskStatus status,
        LocalDateTime dueDate,
        String assignedTo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean overdue,
        long daysUntilDue
) {
}
