package architecture_customer_home.service.impl;

import architecture_customer_home.domain.builder.TaskBuilder;
import architecture_customer_home.dto.TaskDto;
import architecture_customer_home.dto.request.CreateTaskRequest;
import architecture_customer_home.exception.TaskNotFoundException;
import architecture_customer_home.model.Tasks;
import architecture_customer_home.repository.TaskRepository;
import architecture_customer_home.service.TaskService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Priority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public List<TaskDto> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDto findById(Integer id) {
        return toDto(findEntityById(id));
    }

    @Override
    public TaskDto create(CreateTaskRequest request) {
        Tasks task = new TaskBuilder()
                .withDescription(request.description())
                .withPriority(request.priority())
                .withStatus(request.taskStatus())
                .withDueDate(request.dueDate())
                .withAssignedTo(request.assignedTo()).build();
        return toDto(taskRepository.save(task));
    }

    @Override
    public TaskDto update(Integer id, CreateTaskRequest request) {
        Tasks task = findEntityById(id);
        task.setDescription(request.description());
        task.setPriority(request.priority());
        task.setStatus(request.taskStatus());
        task.setDueDate(request.dueDate());
        task.setAssignedTo(request.assignedTo());

        return toDto(taskRepository.save(task));
    }

    @Override
    public void delete(Integer id) {
        Tasks task = findEntityById(id);
        taskRepository.delete(task);
    }

    private Tasks findEntityById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    private TaskDto toDto(Tasks task) {
        return new TaskDto(
            task.getId(),
            task.getDescription(),
            task.isCompleted(),
            task.getPriority(),
            task.getStatus(),
            task.getDueDate(),
            task.getAssignedTo()
        );
    }
}
