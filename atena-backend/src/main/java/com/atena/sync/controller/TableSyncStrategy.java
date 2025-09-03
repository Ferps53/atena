package com.atena.sync.controller;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import java.util.concurrent.CopyOnWriteArrayList;

public interface TableSyncStrategy<T extends PanacheEntityBase, D> {

  void deleteRow(T entity);

  void insertRow(T entity);

  void updateRow(T entity);

  CopyOnWriteArrayList<D> getTableDto(long... userId);

  CopyOnWriteArrayList<T> getTableOrm(long... userId);
}
