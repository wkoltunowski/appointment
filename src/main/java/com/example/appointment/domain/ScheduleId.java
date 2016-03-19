package com.example.appointment.domain;

import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ScheduleId {

  private final UUID id;

  public ScheduleId() {
    id = UUID.randomUUID();
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);

  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
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
