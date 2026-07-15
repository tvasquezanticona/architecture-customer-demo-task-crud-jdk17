package architecture_customer_home.dto.request;

import architecture_customer_home.enums.TaskStatus;
import architecture_customer_home.config.LocalDateDeserializer;
import java.time.LocalDate;
import architecture_customer_home.enums.Priority;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record UpdateTaskRequest(
        String description,
        Priority priority,
        TaskStatus status,
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate dueDate,
        String assignedTo,
        Boolean completed
) {
}