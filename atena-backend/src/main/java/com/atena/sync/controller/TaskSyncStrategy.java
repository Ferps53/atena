package com.atena.sync.controller;

import com.atena.task.Task;
import com.atena.task.dto.TaskDTO;
import com.atena.user.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.transaction.TransactionScoped;
import java.util.concurrent.CopyOnWriteArrayList;

@TransactionScoped
public class TaskSyncStrategy implements TableSyncStrategy<Task, TaskDTO>, PanacheRepository<Task> {

  final UserRepository userRepository = new UserRepository();

  @Override
  public void deleteRow(Task entity) {
    delete(entity);
  }

  @Override
  public void insertRow(Task entity) {

    entity.setTaskId(null).setUser(userRepository.findById(entity.getUser().getIdUser()));
    persist(entity);
  }

  @Override
  public void updateRow(Task entity) {

    Parameters params =
        Parameters.with("title", entity.getTitle())
            .and("desc", entity.getDescription())
            .and("concludedAt", entity.getConcludedAt())
            .and("isConcluded", entity.getIsConcluded())
            .and("id", entity.getTaskId())
            .and("userId", entity.getUser().getIdUser())
            .and("expiresIn", entity.getExpiresIn());

    entity.setUser(userRepository.findById(entity.getUser().getIdUser()));
    update(
        "set title = :title, description = :desc, concludedAt = :concludedAt, isConcluded = :isConcluded, expiresIn = :expiresIn where id = :id and user.id = :userId",
        params);
  }

  @Override
  public CopyOnWriteArrayList<TaskDTO> getTableDto(long... userId) {

    return new CopyOnWriteArrayList<>(
        find("user.id = ?1", userId[0]).project(TaskDTO.class).list());
  }

  @Override
  public CopyOnWriteArrayList<Task> getTableOrm(long... userId) {

    return new CopyOnWriteArrayList<>(find("user.id = ?1", userId[0]).list());
  }
}
