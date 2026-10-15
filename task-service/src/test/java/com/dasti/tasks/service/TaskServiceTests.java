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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doAnswer;

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

        Task task = new Task();
        task.setTitle("work out");
        task.setDetails("full body");

        Task savedTask = new Task();
        savedTask.setTitle("work out");
        savedTask.setDetails("full body");

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
        Task savedTask = new Task();
        savedTask.setTitle("work out");
        savedTask.setDetails("full body");

        TaskResponse response = new TaskResponse(1L, "work out", "full body");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(savedTask));
        when(taskMapper.taskToTaskResponse(savedTask)).thenReturn(response);

        TaskResponse result = taskService.get(1L);

        assertThat(result).isEqualTo(response);

        verify(taskRepository).findById(1L);
        verify(taskMapper).taskToTaskResponse(savedTask);
    }

    @Test
    public void getThrowsWhenNotFound() {
        TaskResponse response = new TaskResponse(1L, "work out", "full body");
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> taskService.get(1L));

        assertEquals("Task not found: 1", exception.getMessage());

        verify(taskRepository).findById(1L);
        verify(taskMapper, times(0)).taskToTaskResponse(any());

    }

    @Test
    public void getAllReturnsTaskResponses() {
        Task task1 = new Task();
        task1.setTitle("work out");
        task1.setDetails("full body");

        Task task2 = new Task();
        task2.setTitle("cook");
        task2.setDetails("high protein meal");

        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        TaskResponse response1 = new TaskResponse(1L, "work out", "full body");
        TaskResponse response2 = new TaskResponse(2L, "cook", "high protein meal");
        List<TaskResponse> taskResponses = new ArrayList<>();
        taskResponses.add(response1);
        taskResponses.add(response2);

        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.taskToTaskResponse(task1)).thenReturn(response1);
        when(taskMapper.taskToTaskResponse(task2)).thenReturn(response2);

        List<TaskResponse> result = taskService.getAll();

        assertThat(result).isEqualTo(taskResponses);

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

        doAnswer(invocation -> {
            UpdateTaskRequest requestArg = invocation.getArgument(0);
            Task taskArg = invocation.getArgument(1);
            taskArg.setTitle(requestArg.title());
            taskArg.setDetails(requestArg.details());
            return null;
        }).when(taskMapper).updateTaskRequestToTask(request, existingTask);

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

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> taskService.update(1L, request));

        assertEquals("Task not found: 1", exception.getMessage());

        verify(taskRepository).findById(1L);
        verify(taskMapper, times(0)).taskToTaskResponse(any());
        verify(taskMapper, times(0)).updateTaskRequestToTask(any(), any());
        verify(taskRepository, times(0)).save(any());
    }

    @Test
    public void deleteThrowsWhenNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> taskService.delete(1L));

        assertEquals("Task not found: 1", exception.getMessage());
    }
}
