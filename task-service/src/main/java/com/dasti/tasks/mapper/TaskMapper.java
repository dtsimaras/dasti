package com.dasti.tasks.mapper;

import com.dasti.tasks.dto.UpdateTaskRequest;
import com.dasti.tasks.entity.Task;
import com.dasti.tasks.dto.CreateTaskRequest;
import com.dasti.tasks.dto.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    Task createTaskRequestToTask(CreateTaskRequest createTaskRequest);

    TaskResponse taskToTaskResponse(Task task);

    void updateTaskRequestToTask(UpdateTaskRequest updateTaskRequest, @MappingTarget Task task);
}
