package architecture_customer_home.service;

import architecture_customer_home.dto.TaskDto;
import architecture_customer_home.dto.request.CreateTaskRequest;

import java.util.List;

public interface TaskService {

    List<TaskDto> findAll();

    TaskDto findById(Integer id);

    TaskDto create(CreateTaskRequest request);

    TaskDto update(Integer id, CreateTaskRequest request);

    void delete(Integer id);
}
