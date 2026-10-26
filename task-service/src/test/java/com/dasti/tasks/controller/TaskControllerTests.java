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

    @Test
    void createReturnsBadRequestWhenTitleIsBlank() {

    }

    /*
     * TaskController HTTP behavior to test
     *
     * CREATE
     * [x] POST /api/v1/tasks with a valid request returns 201 Created.
     * [x] The response contains the task returned by TaskService.
     * [x] TaskService.create() receives the deserialized request.
     * [ ] A blank title returns 400 Bad Request.
     * [ ] A missing title returns 400 Bad Request.
     * [ ] TaskService is not called when the create request is invalid.
     *
     * GET ONE
     * [ ] GET /api/v1/tasks/{id} returns 200 OK and the expected task.
     * [ ] TaskService.get() receives the path ID.
     * [ ] A missing task returns 404 Not Found with the exception message.
     *
     * GET ALL
     * [ ] GET /api/v1/tasks returns 200 OK and the expected JSON array.
     * [ ] An empty service result returns 200 OK and an empty JSON array.
     *
     * UPDATE
     * [ ] PUT /api/v1/tasks/{id} with a valid request returns 200 OK.
     * [ ] The response contains the updated task.
     * [ ] TaskService.update() receives both the path ID and request body.
     * [ ] A blank or missing title returns 400 Bad Request.
     * [ ] TaskService is not called when the update request is invalid.
     * [ ] Updating a missing task returns 404 Not Found.
     *
     * DELETE
     * [ ] DELETE /api/v1/tasks/{id} returns 204 No Content.
     * [ ] The successful delete response has no body.
     * [ ] TaskService.delete() receives the path ID.
     * [ ] Deleting a missing task returns 404 Not Found.
     *
     * Keep these as controller tests:
     * - Assert HTTP status, JSON, validation, request mapping, and delegation.
     * - Do not retest repository behavior or TaskService business logic here.
     * - Add and run one test at a time.
     */
}
