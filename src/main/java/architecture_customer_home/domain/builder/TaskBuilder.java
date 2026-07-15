package architecture_customer_home.domain.builder;

import architecture_customer_home.enums.Priority;
import architecture_customer_home.enums.TaskStatus;
import architecture_customer_home.exception.BusinessException;
import architecture_customer_home.model.Tasks;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * PATRÓN BUILDER
 * 
 * El patrón Builder es útil cuando:
 * - Un objeto tiene muchos parámetros opcionales
 * - Queremos crear el objeto paso a paso
 * - Queremos validar cada paso del proceso
 * l }{f}
 * Ventajas:
 * - Código legible: new TaskBuilder().withDescription("...").withPriority(HIGH).build()
 * - Seguridad de tipos: No hay constructores con muchos parámetros
 * - Inmutabilidad: Podemos hacer el builder inmutable
 * - Validación: Podemos validar en el build()
 */
public class TaskBuilder {
    private String description;
    private Priority priority = Priority.MEDIUM;
    private TaskStatus status = TaskStatus.PENDING;
    private LocalDate dueDate;
    private String assignedTo;

    public TaskBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder withPriority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public TaskBuilder withStatus(TaskStatus status) {
        this.status = status;
        return this;
    }

    public TaskBuilder withDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TaskBuilder withAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
        return this;
    }

    public Tasks build() {
        // Validación de reglas de negocio aquí
        if (description == null || description.isBlank()) {
            throw new BusinessException("La descripción es requerida");
        }
        if (priority == null) {
            throw new BusinessException("La prioridad es requerida");
        }

        LocalDate now = LocalDate.now();
        DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        if(status == TaskStatus.COMPLETED) {
            if (dueDate != null && dueDate.isAfter(now)) {
                String msg = String.format("No se puede marcar como COMPLETADA: la fecha de vencimiento (%s) aún no ha llegado.",
                        dueDate.format(F));
                throw new BusinessException(msg);
            }

            if (assignedTo == null) {
                throw new BusinessException("La tarea debe estar asignada para marcarla como completada.");
            }
        } else if (status == TaskStatus.PENDING) {
            if (dueDate != null && dueDate.isBefore(now)) {
                String msg = String.format("No se puede marcar como PENDIENTE: la fecha de vencimiento (%s) ya expiró.",
                        dueDate.format(F));
                throw new BusinessException(msg);
            }
        }
        validateStatusTransition();
        Tasks task = new Tasks();
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus(status);
        task.setDueDate(dueDate);
        task.setAssignedTo(assignedTo);
        
        return task;
    }

    private void validateStatusTransition() {
        if(status==TaskStatus.COMPLETED){
            throw new BusinessException("La tarea debe pasar por un proceso antes de cerrarse.");
        }
    }
}
