package com.atena.task;

import com.atena.task.dto.NewTaskDTO;
import com.atena.task.dto.TaskDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;

@RequestScoped
@Path("task")
public class TaskEndpoint {

  private final TaskController taskController;

  @Inject
  public TaskEndpoint(TaskController taskController) {
    this.taskController = taskController;
  }

  @GET
  public Response getTasks() {
    final List<TaskDTO> taskList = taskController.getTasksNotInTrashBin();
    return Response.ok(taskList).build();
  }

  @GET
  @Path("{id}")
  public Response getTaskById(@PathParam("id") Long taskId) {
    final TaskDTO task = taskController.getTaskById(taskId);
    return Response.ok(task).build();
  }

  @PATCH
  @Path("{id}")
  public Response patchTask(@PathParam("id") Long taskId, NewTaskDTO newTaskDTO) {
    final TaskDTO taskDTO = taskController.patchTask(taskId, newTaskDTO);
    return Response.ok(taskDTO).build();
  }

  @PUT
  @Path("mark-as-concluded/{id}")
  public Response markTaskAsConcluded(@PathParam("id") Long taskId) {
    final TaskDTO taskDTO = taskController.markTaskAsCompleted(taskId);
    return Response.ok(taskDTO).build();
  }

  @PUT
  @Path("send-to-trash-bin/{id}")
  public Response sendToTrashBin(@PathParam("id") Long taskId) {
    final TaskDTO taskDTO = taskController.sendTaskToTrashBin(taskId);
    return Response.ok(taskDTO).build();
  }

  @POST
  public Response createTask(NewTaskDTO newTaskDTO) {
    final TaskDTO newTask = taskController.createTask(newTaskDTO);
    return Response.ok(newTask).build();
  }

  @DELETE
  @Path("{id}")
  public Response deleteTask(@PathParam("id") Long taskId) {
    taskController.deleteTask(taskId);
    return Response.ok().build();
  }
}
