package com.dasti.tasks.controller;

import com.dasti.tasks.dto.CreateTaskRequest;
import com.dasti.tasks.dto.TaskResponse;
import com.dasti.tasks.dto.UpdateTaskRequest;
import com.dasti.tasks.exception.TaskNotFoundException;
import com.dasti.tasks.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
        assertThat(mvc.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "   ",
                      "details": "full body"
                    }
                    """))
                .hasStatus(HttpStatus.BAD_REQUEST);

        verifyNoInteractions(taskService);
    }

    @Test
    void createReturnsBadRequestWhenTitleIsMissing() {
        assertThat(mvc.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "details": "full body"
                    }
                    """))
                .hasStatus(HttpStatus.BAD_REQUEST);

        verify(taskService, never()).create(any());
    }

    @Test
    void getReturnsTaskWhenTaskExists() {
        TaskResponse response =
                new TaskResponse(1L, "work out", "full body");
        when(taskService.get(1L)).thenReturn(response);

        assertThat(mvc.get()
                .uri("/api/v1/tasks/{id}", 1L))
                .hasStatus(HttpStatus.OK)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .isLenientlyEqualTo("""
                    {
                      "id": 1,
                      "title": "work out",
                      "details": "full body"
                    }
                    """);

        verify(taskService).get(1L);
    }

    @Test
    void getReturnsNotFoundWhenTaskDoesNotExist() {
        when(taskService.get(1L)).thenThrow(new TaskNotFoundException("Task not found: 1"));

        assertThat(mvc.get()
                .uri("/api/v1/tasks/{id}", 1L))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .hasBodyTextEqualTo("Task not found: 1");

        verify(taskService).get(1L);
    }

    @Test
    void getAllReturnsTasksWhenTasksExist() {
        TaskResponse response1 = new TaskResponse(1L, "work out", "full body");
        TaskResponse response2 = new TaskResponse(2L, "cook", "high protein meal");
        when(taskService.getAll()).thenReturn(List.of(response1, response2));

        assertThat(mvc.get()
                .uri("/api/v1/tasks"))
                .hasStatus(HttpStatus.OK)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .isLenientlyEqualTo("""
                    [{
                      "id": 1,
                      "title": "work out",
                      "details": "full body"
                    },
                    {
                      "id": 2,
                      "title": "cook",
                      "details": "high protein meal"
                    }]
                    """);

        verify(taskService).getAll();
    }

    @Test
    void getAllReturnsEmptyListWhenTasksDontExist() {
        when(taskService.getAll()).thenReturn(List.of());

        assertThat(mvc.get()
                .uri("/api/v1/tasks"))
                .hasStatus(HttpStatus.OK)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .isStrictlyEqualTo("[]");

        verify(taskService).getAll();
    }

    @Test
    void updateReturnsUpdatedTaskWhenRequestIsValid() {
        var request = new UpdateTaskRequest("work out", "full body");
        TaskResponse response = new TaskResponse(1L, "work out", "full body");
        when(taskService.update(1L, request)).thenReturn(response);

        assertThat(mvc.put()
                .uri("/api/v1/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "work out",
                      "details": "full body"
                    }
                    """))
                .hasStatus(HttpStatus.OK)
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .isLenientlyEqualTo("""
                    {
                      "id": 1,
                      "title": "work out",
                      "details": "full body"
                    }
                    """);

        verify(taskService).update(1L, request);
    }

    @Test
    void updateReturnsBadRequestWhenTitleIsBlank() {
        assertThat(mvc.put()
                .uri("/api/v1/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "  ",
                      "details": "full body"
                    }
                    """))
                .hasStatus(HttpStatus.BAD_REQUEST);

        verifyNoInteractions(taskService);
    }

    @Test
    void updateReturnsBadRequestWhenTitleIsMissing() {
        assertThat(mvc.put()
                .uri("/api/v1/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "details": "full body"
                    }
                    """))
                .hasStatus(HttpStatus.BAD_REQUEST);

        verifyNoInteractions(taskService);
    }

    @Test
    void updateReturnsNotFoundWhenTaskNotFound() {
        var request = new UpdateTaskRequest("work out", "full body");
        when(taskService.update(1L, request)).thenThrow(new TaskNotFoundException("Task not found: 1"));

        assertThat(mvc.put()
                .uri("/api/v1/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "work out",
                      "details": "full body"
                    }
                    """))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .hasBodyTextEqualTo("Task not found: 1");

        verify(taskService).update(1L, request);
    }

    @Test
    void deleteReturnsNoBodyWhenSuccessful() {
        assertThat(mvc.delete()
                .uri("/api/v1/tasks/{id}", 1L))
                .hasStatus(HttpStatus.NO_CONTENT)
                .body().isNullOrEmpty();

        verify(taskService).delete(1L);
    }

    @Test
    void deleteReturnsNotFoundWhenTaskDoesNotExist() {
        doThrow(new TaskNotFoundException("Task not found: 1"))
                .when(taskService).delete(1L);

        assertThat(mvc.delete()
                .uri("/api/v1/tasks/{id}", 1L))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .hasBodyTextEqualTo("Task not found: 1");

        verify(taskService).delete(1L);
    }
}
