package com.example.appointment.domain;

import static com.google.common.collect.Ranges.closedOpen;

import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Range;

public class Visit {

  private final ScheduleId scheduleId;
  private final Range<LocalDateTime> visitRange;

  public Visit(ScheduleId scheduleId, Range<LocalDateTime> visitRange) {

    this.scheduleId = scheduleId;
    this.visitRange = visitRange;
  }

  public LocalDateTime getDateTime() {
    return visitRange.lowerEndpoint();
  }

  public ScheduleId scheduleId() {
    return scheduleId;
  }

  public Range<LocalDateTime> range() {
    return visitRange;
  }

  @Override
  public String toString() {
    return String.format("Visit{visitRange=%s, scheduleId=%s}", visitRange, scheduleId);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  public static Visit visitFor(LocalDateTime dateTime, Duration duration, ScheduleId scheduleId) {
    return new Visit(scheduleId, closedOpen(dateTime, dateTime.plus(duration)));
  }

  public static Visit visitFor(LocalDateTime start, LocalDateTime end, ScheduleId scheduleId) {
    return new Visit(scheduleId, closedOpen(start, end));
  }

  public static Visit visitFor(Range<LocalDateTime> range, ScheduleId scheduleId) {
    return new Visit(scheduleId, range);
  }
}
