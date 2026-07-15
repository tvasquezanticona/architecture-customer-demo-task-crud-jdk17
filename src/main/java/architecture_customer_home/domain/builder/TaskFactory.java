package architecture_customer_home.domain.builder;

import architecture_customer_home.dto.request.CreateTaskRequest;
import architecture_customer_home.dto.request.UpdateTaskRequest;
import architecture_customer_home.model.Tasks;

import java.time.LocalDate;

/**
 * PATRÓN FACTORY
 * 
 * El patrón Factory es útil cuando:
 * - Queremos encapsular la creación de objetos complejos
 * - Tenemos varias formas de crear un objeto (de DTO, de entidad, etc)
 * - Queremos aplicar lógica de negocio durante la creación
 * 
 * Ventajas:
 * - Separa la creación de la lógica de negocio
 * - Centraliza las transformaciones DTO → Entity
 * - Permite aplicar validaciones y transformaciones
 */
public class TaskFactory {

    public static Tasks createFromRequest(CreateTaskRequest request) {
        return new TaskBuilder()
                .withDescription(request.description())
                .withPriority(request.priority())
                .withDueDate(LocalDate.from(request.dueDate().atStartOfDay()))
                .withAssignedTo(request.assignedTo())
                .build();
    }

    public static Tasks updateFromRequest(Tasks existingTask, UpdateTaskRequest request) {
        if (request.description() != null) {
            existingTask.setDescription(request.description());
        }
        if (request.priority() != null) {
            existingTask.setPriority(request.priority());
        }
        if (request.status() != null) {
            existingTask.setStatus(request.status());
        }
        if (request.dueDate() != null) {
            existingTask.setDueDate(LocalDate.from(request.dueDate()));
        }
        if (request.assignedTo() != null) {
            existingTask.setAssignedTo(request.assignedTo());
        }
        
        return existingTask;
    }
}
