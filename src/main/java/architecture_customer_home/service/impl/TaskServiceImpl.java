package architecture_customer_home.service.impl;

import architecture_customer_home.domain.builder.TaskBuilder;
import architecture_customer_home.dto.TaskDto;
import architecture_customer_home.dto.request.CreateTaskRequest;
import architecture_customer_home.dto.request.UpdateTaskRequest;
import architecture_customer_home.enums.Priority;
import architecture_customer_home.enums.TaskStatus;
import architecture_customer_home.exception.BusinessException;
import architecture_customer_home.exception.ErrorCode;
import architecture_customer_home.exception.TaskNotFoundException;
import architecture_customer_home.model.Tasks;
import architecture_customer_home.repository.TaskRepository;
import architecture_customer_home.service.TaskService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final Logger log = (Logger) LoggerFactory.getLogger(TaskServiceImpl.class);
    private final TaskRepository taskRepository;


    @Override
    public List<TaskDto> findAll() {
        List<TaskDto> getTaskDto = taskRepository.findAll()
                .stream()
                .map(this::toDto).toList();
        log.debug("findAll -> {} tareas encontradas", getTaskDto.size() );
        return getTaskDto;
    }

    private TaskDto toDto(Tasks tasks) {
        return new TaskDto(
                tasks.getId(),
                tasks.getDescription(),
                tasks.isCompleted(),
                tasks.getPriority(),
                tasks.getStatus(),
                tasks.getDueDate(),
                tasks.getAssignedTo()
        );
    }

    @Override
    public TaskDto findById(Integer id) {
        return toDto(findModelEntityById(id));
    }

    private Tasks findModelEntityById(Integer id) {
    return taskRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Tarea no encontrada [{id}]",id);
                return new TaskNotFoundException(id);
            });
    }


    @Override
    public TaskDto create(CreateTaskRequest request) {
        validateBusinessRule(request.description(),request.priority(),
                            request.status(),request.dueDate(),
                            request.assignedTo());

        Tasks tasks = new TaskBuilder()
                .withDescription(request.description())
                .withPriority(request.priority())
                .withStatus(request.status())
                .withDueDate(request.dueDate())
                .withAssignedTo(request.assignedTo())
                .build();
        Tasks saved = taskRepository.save(tasks);
        log.info("Tarea Creada [id = {}, status={}]");


        return toDto(saved);
    }

    private void validateBusinessRule(String description,Priority priority, TaskStatus status, LocalDate dueDate, String assignedTo) {
            validateDescriptionRequired(description);
            validatePriorityRequired(priority);
            validateDueDateForStatus(status,dueDate);
            validateAssigneeForStatus(status,assignedTo);

    }

    private void validateAssigneeForStatus(TaskStatus status, String assignedTo) {
    if ((status == TaskStatus.COMPLETED || status == TaskStatus.PENDING ) && assignedTo.isBlank()){
        throw new BusinessException(ErrorCode.TASK_ASSIGNEE_REQUIRED,"La tarea requiere un Responsable Asignado");
    }
    }

    //COMPLETED: La fecha limite no puede estar en el futuro(Si ya lo completaste
    //no puede ser que todavia no venza.
    //PENDING: la fecha limite no puede haber expirado ya. si no debe estar abierta..
    private void validateDueDateForStatus(TaskStatus status, LocalDate dueDate) {
    if(dueDate==null){
        return;
    }
        LocalDate today= LocalDate.now();

    if(status==TaskStatus.COMPLETED&& dueDate.isAfter(today)){
        throw new BusinessException(ErrorCode.TASK_DUE_DATE_INVALID,"No se puede marcar" +
                "como COMPLETADA: la fecha limite aun no ha llegado");

    }
    if(status==TaskStatus.PENDING&&dueDate.isBefore(today)){
            throw new BusinessException(ErrorCode.TASK_DUE_DATE_INVALID,"No se puede marcar como PENDIENTE" +
                    "La fecha limite ya expiro");
    }


    }

    private void validatePriorityRequired(Priority priority) {
        if(priority==null){
            throw new BusinessException(ErrorCode.PRIORITY_REQUIRED,"Prioridad es Requerido.");
        }
    }

    private void validateDescriptionRequired(String description) {
        if (description==null || description.isBlank()){
            throw new BusinessException(ErrorCode.TASK_DESCRIPTION_REQUIRED,"Descripcion es Requerido.");
        }
    }


    @Override
    public TaskDto update(Integer id, UpdateTaskRequest request) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }
}
