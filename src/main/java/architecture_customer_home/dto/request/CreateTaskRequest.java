package architecture_customer_home.dto.request;

import architecture_customer_home.enums.TaskStatus;
import architecture_customer_home.config.LocalDateDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import architecture_customer_home.enums.Priority;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record CreateTaskRequest(
        @NotBlank(message = "La descripción es requerida")
        String description,

        @NotNull(message = "La prioridad es requerida")
        Priority priority,

        @NotNull(message = "El estado es requerido")
        @JsonProperty("taskStatus")
        TaskStatus status,

        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate dueDate,
        String assignedTo,
        Boolean completed
) {
}
