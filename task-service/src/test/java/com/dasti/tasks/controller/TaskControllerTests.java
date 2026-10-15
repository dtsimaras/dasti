package com.dasti.tasks.controller;

import com.dasti.tasks.dto.CreateTaskRequest;
import com.dasti.tasks.dto.TaskResponse;
import com.dasti.tasks.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(TaskController.class)
public class TaskControllerTests {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    TaskService taskService;

    @Test
    public void createReturnsCreatedWhenSuccess() {
        CreateTaskRequest request = new CreateTaskRequest("work out", "full body");
        TaskResponse response = new TaskResponse(1L, "work out", "full body");

        when(taskService.create(request)).thenReturn(response);


    }

    @Test
    void createReturnsCreatedTaskWhenRequestIsValid() {
        CreateTaskRequest request =
                new CreateTaskRequest("work out", "full body");

        TaskResponse response =
                new TaskResponse(1L, "work out", "full body");

        when(taskService.create(request)).thenReturn(response);

        assertThat(mvc.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "work out",
                      "details": "full body"
                    }
                    """))
                .hasStatus(HttpStatus.CREATED)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .isLenientlyEqualTo("""
                    {
                      "id": 1,
                      "title": "work out",
                      "details": "full body"
                    }
                    """);

        verify(taskService).create(request);
    }
}
