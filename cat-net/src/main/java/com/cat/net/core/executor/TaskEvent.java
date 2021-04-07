package com.cat.net.core.executor;

public class TaskEvent {
  private Runnable task;

  public Runnable getTask() {
    return task;
  }

  public void setTask(Runnable task) {
    this.task = task;
  }
}

