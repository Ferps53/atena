package com.atena.core.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.atena.task.Task;
import com.atena.task.TaskMapper;
import com.atena.task.dto.NewTaskDTO;
import com.atena.task.dto.TaskDTO;
import com.atena.user.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TaskMapperTest {

  private final TaskMapper taskMapper;

  @Inject
  TaskMapperTest(TaskMapper taskMapper) {
    this.taskMapper = taskMapper;
  }

  @Test
  void testDtoConversion() {

    // given
    final var user = new User();
    user.setIdUser(1);

    final LocalDateTime now = LocalDateTime.now();
    final Task task =
        new Task()
            .setTaskId(1)
            .setCreatedAt(now)
            .setConcluded(false)
            .setDescription("Test description")
            .setTitle("Test title")
            .setUser(user)
            .setInTrashBin(false);

    final TaskDTO expected =
        new TaskDTO(1, "Test title", "Test description", false, false, now, null, user.getIdUser());

    // when

    final TaskDTO actual = taskMapper.toTaskDTO(task);

    // assert
    assertEquals(expected, actual);
  }

  @Test
  void testNewTaskDTOConversion() {

    // given
    final LocalDateTime expiresIn = LocalDateTime.now();
    final Task expected =
        new Task()
            .setTitle("Test title")
            .setDescription("Test description")
            .setExpiresIn(expiresIn);
    final NewTaskDTO newTaskDTO = new NewTaskDTO("Test title", "Test description", expiresIn);
    // when

    final Task actual = taskMapper.toTask(newTaskDTO);

    assertEquals(expected, actual);
  }
}
