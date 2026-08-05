package architecture_customer_home.service.impl;

import architecture_customer_home.domain.builder.TaskBuilder;
import architecture_customer_home.dto.TaskDto;
import architecture_customer_home.dto.request.CreateTaskRequest;
import architecture_customer_home.dto.request.UpdateTaskRequest;
import architecture_customer_home.enums.Priority;
import architecture_customer_home.enums.TaskStatus;
import architecture_customer_home.exception.BusinessException;
import architecture_customer_home.exception.ErrorCode;
import architecture_customer_home.exception.InvalidTaskStateException;
import architecture_customer_home.exception.TaskNotFoundException;
import architecture_customer_home.model.Tasks;
import architecture_customer_home.repository.TaskRepository;
import architecture_customer_home.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
                .withCompleted(request.completed())
                .build();
//        if(request.status().equals(TaskStatus.PENDING)){
//            tasks.setStatus(TaskStatus.PENDING);
//        }

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
    if ((status == TaskStatus.COMPLETED || status == TaskStatus.PENDING || status == TaskStatus.IN_PROGRESS ) && assignedTo.isBlank()){
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
                " como COMPLETADA: la fecha limite aun no ha llegado" + dueDate);

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

    private void validateStateTransition(TaskStatus current, TaskStatus target) {
        if (current == TaskStatus.COMPLETED && target != TaskStatus.COMPLETED ){
            throw new InvalidTaskStateException(ErrorCode.TASK_INVALID_STATE_TRANSITION,"Una tarea COMPLETADA no se puede Reabrir." + target);
        }
    }

    @Override
    public TaskDto update(Integer id, UpdateTaskRequest request) {

            Tasks tasks = findModelEntityById(id);

            validateBusinessRule(request.description(),request.priority(),
                request.status(),request.dueDate(),
                request.assignedTo());

            validateStateTransition(tasks.getStatus(), request.status());

                    tasks.setDescription(request.description() );
                    tasks.setPriority(request.priority());
                    tasks.setStatus(request.status());
                    tasks.setDueDate(request.dueDate());
                    tasks.setAssignedTo(request.assignedTo());
                    tasks.isCompleted();

            Tasks saved = taskRepository.save(tasks);
            log.warn("Tarea Creada [id = {}, status={}]",id,tasks.getStatus());


            return toDto(saved);

        }

    @Override
    public void delete(Integer id) {

        Tasks tasks = findModelEntityById(id);

        if (tasks.getStatus()==TaskStatus.IN_PROGRESS){
            log.warn("Intento de eliminar tarea no permitida [id={}, status={}]", + id, tasks.getStatus());
            throw new InvalidTaskStateException(ErrorCode.TASK_DELETE_NOT_ALLOWED,"Tarea en estado COMPLETADO");
        }

        taskRepository.delete(tasks);

    }


}


