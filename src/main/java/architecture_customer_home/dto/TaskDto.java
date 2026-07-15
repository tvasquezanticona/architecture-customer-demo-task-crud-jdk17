package architecture_customer_home.dto;

import architecture_customer_home.enums.Priority;
import architecture_customer_home.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import architecture_customer_home.config.LocalDateDeserializer;

import java.time.LocalDate;

public record TaskDto(
        Integer id,
        String description,

        boolean completed, Priority priority,
        TaskStatus taskStatus,
        @JsonDeserialize(using = LocalDateDeserializer.class)
        @JsonFormat(pattern = "yyyy/MM/dd")
        LocalDate dueDate,
        String assignedTo
) {
}
