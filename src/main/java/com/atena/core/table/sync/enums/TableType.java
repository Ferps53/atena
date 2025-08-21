package com.atena.core.table.sync.enums;

import com.atena.core.table.sync.controller.strategies.TableSyncStrategy;
import com.atena.core.table.sync.controller.strategies.TaskSyncStrategy;
import com.atena.features.task.model.Task;

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
