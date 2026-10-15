package com.dasti.tasks.service;

import com.dasti.tasks.dto.CreateTaskRequest;
import com.dasti.tasks.dto.TaskResponse;
import com.dasti.tasks.dto.UpdateTaskRequest;
import com.dasti.tasks.entity.Task;
import com.dasti.tasks.exception.TaskNotFoundException;
import com.dasti.tasks.mapper.TaskMapper;
import com.dasti.tasks.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTests {
    @Mock
    TaskMapper taskMapper;

    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    TaskService taskService;

    @Test
    public void createReturnsTaskResponseWhenValid() {
        CreateTaskRequest request = new CreateTaskRequest("work out", "full body");
        Task task = task("work out", "full body");
        Task savedTask = task("work out", "full body");
        TaskResponse response = new TaskResponse(1L, "work out", "full body");

        when(taskMapper.createTaskRequestToTask(request)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskMapper.taskToTaskResponse(savedTask)).thenReturn(response);

        TaskResponse result = taskService.create(request);

        assertThat(result).isEqualTo(response);

        verify(taskMapper).createTaskRequestToTask(request);
        verify(taskRepository).save(task);
        verify(taskMapper).taskToTaskResponse(savedTask);
    }

    @Test
    public void getReturnsTaskWhenFound() {
        Task savedTask = task("cook", "protein meal");
        TaskResponse response = new TaskResponse(1L, "cook", "protein meal");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(savedTask));
        when(taskMapper.taskToTaskResponse(savedTask)).thenReturn(response);

        TaskResponse result = taskService.get(1L);

        assertThat(result).isEqualTo(response);

        verify(taskRepository).findById(1L);
        verify(taskMapper).taskToTaskResponse(savedTask);
    }

    @Test
    public void getThrowsWhenNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.get(1L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found: 1");

        verify(taskRepository).findById(1L);
        verify(taskMapper, never()).taskToTaskResponse(any());

    }

    @Test
    public void getAllReturnsTaskResponses() {
        Task task1 =  task("work out", "full body");
        Task task2 = task("cook", "high protein meal");
        TaskResponse response1 = new TaskResponse(1L, "work out", "full body");
        TaskResponse response2 = new TaskResponse(2L, "cook", "high protein meal");

        List<Task> tasks = List.of(task1, task2);
        List<TaskResponse> expected = List.of(response1, response2);

        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.taskToTaskResponse(task1)).thenReturn(response1);
        when(taskMapper.taskToTaskResponse(task2)).thenReturn(response2);

        List<TaskResponse> result = taskService.getAll();

        assertThat(result).isEqualTo(expected);

        verify(taskRepository).findAll();
        verify(taskMapper).taskToTaskResponse(task1);
        verify(taskMapper).taskToTaskResponse(task2);
    }

    @Test
    public void updateReturnsTaskWhenFound() {
        UpdateTaskRequest request = new UpdateTaskRequest("work out", "chest day");

        Task existingTask = new Task();
        existingTask.setTitle("work out");
        existingTask.setDetails("full body");

        TaskResponse response = new TaskResponse(1L, "work out", "chest day");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);
        when(taskMapper.taskToTaskResponse(existingTask)).thenReturn(response);

        TaskResponse result = taskService.update(1L, request);

        assertThat(result).isEqualTo(response);

        verify(taskMapper).updateTaskRequestToTask(request, existingTask);
        verify(taskRepository).save(existingTask);
        verify(taskMapper).taskToTaskResponse(existingTask);
    }

    @Test
    public void updateThrowsWhenNotFound() {
        UpdateTaskRequest request = new UpdateTaskRequest("work out", "chest day");

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(1L, request))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found: 1");

        verify(taskRepository).findById(1L);
        verifyNoInteractions(taskMapper);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteDeletesTaskWhenTaskExists() {
        Task task = task("work out", "full body");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.delete(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    public void deleteThrowsWhenNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(1L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found: 1");

        verify(taskRepository, never()).deleteById(any());
    }

    private Task task(String title, String details) {
        Task task = new Task();
        task.setTitle(title);
        task.setDetails(details);
        return task;
    }
}
