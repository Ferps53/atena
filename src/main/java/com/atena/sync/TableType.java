package com.atena.sync;

import com.atena.sync.controller.TableSyncStrategy;
import com.atena.sync.controller.TaskSyncStrategy;
import com.atena.task.Task;

public enum TableType {
  TASK(Task.class, new TaskSyncStrategy(), 1);

  public final Class<?> tableClass;

  public final TableSyncStrategy tableSyncStrategy;

  public final int priority;

  TableType(Class<?> tableClass, TableSyncStrategy tableSyncStrategy, int priority) {
    this.tableClass = tableClass;
    this.tableSyncStrategy = tableSyncStrategy;
    this.priority = priority;
  }

  public static TableType getByName(String name) {

    return switch (name) {
      case "task" -> TableType.TASK;

      default -> throw new IllegalStateException("Unexpected value: " + name);
    };
  }
}
