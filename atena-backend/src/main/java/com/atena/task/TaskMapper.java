package com.atena.task;

import com.atena.task.dto.NewTaskDTO;
import com.atena.task.dto.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi")
public interface TaskMapper {

  @Mapping(target = "user", ignore = true)
  @Mapping(target = "sentToTrashBinAt", ignore = true)
  //  @Mapping(target = "isInTrashBin", constant = "false")
  //  @Mapping(target = "isConcluded", constant = "false")
  @Mapping(target = "taskId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "concludedAt", ignore = true)
  Task toTask(NewTaskDTO newTaskDTO);

  //  @Mapping(target = "idUser", source = "task.user.idUser")
  TaskDTO toTaskDTO(Task task);
}
