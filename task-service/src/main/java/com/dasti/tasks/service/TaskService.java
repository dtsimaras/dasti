package com.dasti.tasks.service;

import com.dasti.tasks.dto.UpdateTaskRequest;
import com.dasti.tasks.entity.Task;
import com.dasti.tasks.dto.CreateTaskRequest;
import com.dasti.tasks.dto.TaskResponse;
import com.dasti.tasks.exception.TaskNotFoundException;
import com.dasti.tasks.mapper.TaskMapper;
import com.dasti.tasks.repository.TaskRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    //TODO: remove starter logs, keep only useful ones

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    @Observed(name = "task.get", contextualName = "get-task")
    public TaskResponse get(Long id) throws TaskNotFoundException {
        log.debug("Looking up task id = {}", id);
        Task task = getTaskById(id);
        return taskMapper.taskToTaskResponse(task);
    }

    public TaskResponse create(CreateTaskRequest createTaskRequest) {
        log.info("Creating task with title = {}", createTaskRequest.title());
        Task task = taskMapper.createTaskRequestToTask(createTaskRequest);
        Task savedTask = taskRepository.save(task);
        return taskMapper.taskToTaskResponse(savedTask);
    }

    public List<TaskResponse> getAll() {
        log.debug("get all tasks");
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream().map(taskMapper::taskToTaskResponse).toList();
    }

    public TaskResponse update(Long id, UpdateTaskRequest updateTaskRequest) {
        log.debug("update task with id: {}", id);
        Task task = getTaskById(id);
        taskMapper.updateTaskRequestToTask(updateTaskRequest, task);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.taskToTaskResponse(updatedTask);
    }

    public void delete(Long id) {
        log.debug("delete task with id: {}", id);
        Task task = getTaskById(id);
        taskRepository.deleteById(id);
    }

    private Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found: " + id));
    }
}
