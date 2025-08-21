package com.atena.task;

import com.atena.exceptions.exception.NotFoundException;
import com.atena.task.dto.TaskDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TaskRepository implements PanacheRepository<Task> {

  private static final String BASE_QUERY_FIND_TASK =
      """
            id = ?1 and
            user.id = ?2
            """;

  public List<TaskDTO> listTasksNotInTrashBin(long userId) {
    return find(
            """
                        user.id = ?1
                        Order by expiresIn ASC,
                        id ASC
                        """,
            userId)
        .project(TaskDTO.class)
        .list();
  }

  public TaskDTO getTaskDTOById(long taskId, long userId) {
    final var optionalTask =
        find(BASE_QUERY_FIND_TASK, taskId, userId).project(TaskDTO.class).firstResultOptional();

    if (optionalTask.isEmpty()) throw new NotFoundException("task.notFound");

    return optionalTask.get();
  }

  public Task getTaskById(long taskId, long userId) {
    final var optionalTask = find(BASE_QUERY_FIND_TASK, taskId, userId).firstResultOptional();

    if (optionalTask.isEmpty()) throw new NotFoundException("task.notFound");

    return optionalTask.get();
  }
}
