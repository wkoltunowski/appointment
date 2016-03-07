package com.example.appointment.domain;

import java.util.UUID;

public class ScheduleId {

  private final UUID id;

  public ScheduleId() {
    id = UUID.randomUUID();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    ScheduleId that = (ScheduleId) o;

    return id.equals(that.id);

  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return "ScheduleId{" +
        "id=" + id +
        '}';
  }

  public static ScheduleId newId() {
    return new ScheduleId();
  }
}
