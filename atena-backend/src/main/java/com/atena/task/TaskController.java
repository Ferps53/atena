package com.atena.task;

import com.atena.auth.controller.Session;
import com.atena.auth.controller.SessionModel;
import com.atena.exceptions.exception.NotFoundException;
import com.atena.task.dto.NewTaskDTO;
import com.atena.task.dto.TaskDTO;
import com.atena.user.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@ApplicationScoped
public class TaskController {

  private final TaskMapper taskMapper;
  private final TaskRepository taskRepository;
  private final SessionModel sessionModel;

  @Inject
  public TaskController(
      TaskMapper taskMapper, TaskRepository taskRepository, @Session SessionModel sessionModel) {
    this.taskMapper = taskMapper;
    this.taskRepository = taskRepository;
    this.sessionModel = sessionModel;
  }

  public TaskDTO createTask(NewTaskDTO newTaskDTO) {

    final int userId = sessionModel.getAuth().idUser();

    final Task task = taskMapper.toTask(newTaskDTO).setCreatedAt(LocalDateTime.now());

    User.findByIdOptional(userId)
        .ifPresentOrElse(
            user -> task.setUser((User) user),
            () -> {
              throw new NotFoundException("user.notFound", userId);
            });

    task.persist();

    return taskMapper.toTaskDTO(task);
  }

  public TaskDTO getTaskById(long taskId) {

    final int userId = sessionModel.getAuth().idUser();
    return taskRepository.getTaskDTOById(taskId, userId);
  }

  public TaskDTO patchTask(long taskId, NewTaskDTO newTaskDTO) {

    final var userId = sessionModel.getAuth().idUser();
    final Task task = taskRepository.getTaskById(taskId, userId);

    task.setTitle(newTaskDTO.title());
    task.setDescription(newTaskDTO.description());
    task.setExpiresIn(newTaskDTO.expiresIn());

    task.persist();

    return taskMapper.toTaskDTO(task);
  }

  public TaskDTO markTaskAsCompleted(long taskId) {

    final int userId = sessionModel.getAuth().idUser();
    final Task task = taskRepository.getTaskById(taskId, userId);

    task.setConcluded(!task.isConcluded());
    task.setConcludedAt(task.isConcluded() ? LocalDateTime.now() : null);

    task.persist();

    return taskMapper.toTaskDTO(task);
  }

  public TaskDTO sendTaskToTrashBin(long taskId) {

    final int userId = sessionModel.getAuth().idUser();
    final Task task = taskRepository.getTaskById(taskId, userId);

    task.setInTrashBin(!task.isInTrashBin());
    task.setSentToTrashBinAt(task.isInTrashBin() ? LocalDateTime.now() : null);

    task.persist();

    return taskMapper.toTaskDTO(task);
  }

  public List<TaskDTO> getTasksNotInTrashBin() {

    final int userId = sessionModel.getAuth().idUser();
    final Optional<User> optionalUser = User.findByIdOptional(userId);
    if (optionalUser.isEmpty()) throw new NotFoundException("user.notFound", userId);

    return taskRepository.listTasksNotInTrashBin(userId);
  }

  public void deleteTask(long taskId) {

    final int userId = sessionModel.getAuth().idUser();
    final Task task = taskRepository.getTaskById(taskId, userId);
    task.delete();
  }
}
