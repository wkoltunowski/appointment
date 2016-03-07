package com.example.appointment.domain;

import java.time.Duration;
import java.time.LocalDateTime;

public class FreeSlot implements Comparable<FreeSlot> {

  private final LocalDateTime start;
  private final LocalDateTime end;
  private ScheduleId scheduleId;

  public FreeSlot(ScheduleId scheduleId, LocalDateTime start, LocalDateTime end) {
    this.start = start;
    this.end = end;
    this.scheduleId = scheduleId;
  }

  public static FreeSlot of(ScheduleId scheduleId, LocalDateTime start, LocalDateTime end) {
    return new FreeSlot(scheduleId, start, end);
  }

  @Override
  public int compareTo(FreeSlot o) {

    int dateCompare = start.compareTo(o.start);
    return dateCompare != 0 ? dateCompare : ((Integer) this.hashCode()).compareTo(o.hashCode());
  }

  public boolean contains(LocalDateTime dateTime, Duration duration) {
    return !this.start.isAfter(dateTime) && !this.end.isBefore(dateTime.plus(duration));
  }

  public LocalDateTime getStart() {
    return start;
  }

  public LocalDateTime getEnd() {
    return end;
  }

  public ScheduleId getScheduleId() {
    return scheduleId;
  }
}
