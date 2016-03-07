package com.example.appointment.domain;

import java.time.Duration;
import java.time.LocalDateTime;

public class Visit {

  private final LocalDateTime dateTime;
  private final Duration duration;
  private final ScheduleId scheduleId;

  public Visit(LocalDateTime dateTime, Duration duration, ScheduleId scheduleId) {
    this.dateTime = dateTime;
    this.scheduleId = scheduleId;
    this.duration = duration;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  @Override
  public String toString() {
    return String.format("Visit{dateTime=%s, duration=%s, scheduleId=%s}", dateTime, duration, scheduleId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Visit visit = (Visit) o;

    return dateTime.equals(visit.dateTime) && scheduleId.equals(visit.scheduleId);

  }

  @Override
  public int hashCode() {
    int result = dateTime.hashCode();
    result = 31 * result + scheduleId.hashCode();
    return result;
  }

  public static Visit visitFor(LocalDateTime dateTime, Duration duration, ScheduleId scheduleId) {
    return new Visit(dateTime, duration, scheduleId);
  }

  public static Visit visitFor(LocalDateTime start, LocalDateTime end, ScheduleId scheduleId) {
    return new Visit(start, Duration.between(start, end), scheduleId);
  }

  public Duration getDuration() {
    return duration;
  }

  public ScheduleId scheduleId() {
    return scheduleId;
  }
}
