package com.atena.task;

import com.atena.user.User;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "task")
@SequenceGenerator(name = "seq_task", sequenceName = "seq_task", allocationSize = 1)
public class Task extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_task")
  @Column(name = "task_id", nullable = false)
  private Integer taskId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "is_concluded")
  private boolean isConcluded;

  @Column(name = "is_in_trash_bin")
  private boolean isInTrashBin;

  @Column(name = "sent_to_trash_bin_at")
  private LocalDateTime sentToTrashBinAt;

  @Column(name = "concluded_at")
  private LocalDateTime concludedAt;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "expires_in")
  private LocalDateTime expiresIn;

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "id_user", nullable = false)
  private User user;

  // TODO: Move this to a separate class TaskJsonConverter
  public static Task fromJson(Map<String, Object> jsonMap) {

    final Task task = new Task();
    task.taskId = Integer.valueOf(String.valueOf(jsonMap.get("id")));
    task.title = (String) jsonMap.get("title");
    task.description = (String) jsonMap.get("description");
    task.isConcluded = jsonMap.get("isConcluded").equals("true");
    task.isInTrashBin = jsonMap.get("isInTrashBin").equals("true");
    task.createdAt = LocalDateTime.parse((String) jsonMap.get("createdAt"));
    task.expiresIn = LocalDateTime.parse((String) jsonMap.get("expiresIn"));
    // task.user = new User();
    // task.user.id = Long.parseLong(String.valueOf(jsonMap.get("userId")));

    return task;
  }

  public Integer taskId() {
    return taskId;
  }

  public Task setTaskId(Integer taskId) {
    this.taskId = taskId;
    return this;
  }

  public String title() {
    return title;
  }

  public Task setTitle(String title) {
    this.title = title;
    return this;
  }

  public String description() {
    return description;
  }

  public Task setDescription(String description) {
    this.description = description;
    return this;
  }

  public boolean isConcluded() {
    return isConcluded;
  }

  public Task setConcluded(boolean concluded) {
    isConcluded = concluded;
    return this;
  }

  public boolean isInTrashBin() {
    return isInTrashBin;
  }

  public Task setInTrashBin(boolean inTrashBin) {
    isInTrashBin = inTrashBin;
    return this;
  }

  public LocalDateTime sentToTrashBinAt() {
    return sentToTrashBinAt;
  }

  public Task setSentToTrashBinAt(LocalDateTime sentToTrashBinAt) {
    this.sentToTrashBinAt = sentToTrashBinAt;
    return this;
  }

  public LocalDateTime concludedAt() {
    return concludedAt;
  }

  public Task setConcludedAt(LocalDateTime concludedAt) {
    this.concludedAt = concludedAt;
    return this;
  }

  public LocalDateTime createdAt() {
    return createdAt;
  }

  public Task setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public LocalDateTime expiresIn() {
    return expiresIn;
  }

  public Task setExpiresIn(LocalDateTime expiresIn) {
    this.expiresIn = expiresIn;
    return this;
  }

  public User user() {
    return user;
  }

  public Task setUser(User user) {
    this.user = user;
    return this;
  }

  @Override
  public String toString() {
    return "Task{user=%s, title='%s', description='%s', isConcluded=%s, isInTrashBin=%s, sentToTrashBinAt=%s, concludedAt=%s, createdAt=%s, expiresIn=%s, id=%d}"
        .formatted(
            user,
            title,
            description,
            isConcluded,
            isInTrashBin,
            sentToTrashBinAt,
            concludedAt,
            createdAt,
            expiresIn,
            taskId);
  }
}
