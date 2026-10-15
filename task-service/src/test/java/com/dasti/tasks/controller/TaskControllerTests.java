package com.dasti.tasks.controller;

import com.dasti.tasks.dto.CreateTaskRequest;
import com.dasti.tasks.dto.TaskResponse;
import com.dasti.tasks.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import static org.mockito.Mockito.when;

@WebMvcTest
public class TaskControllerTests {

    @Mock
    TaskService taskService;

    @InjectMocks
    TaskController taskController;

    @Test
    public void createReturnsCreatedWhenSuccess() {
        CreateTaskRequest request = new CreateTaskRequest("work out", "full body");
        TaskResponse response = new TaskResponse(1L, "work out", "full body");

        when(taskService.create(request)).thenReturn(response);
    }
}
