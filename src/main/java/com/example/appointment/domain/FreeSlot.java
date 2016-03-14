package com.example.appointment.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

public class FreeSlot implements Comparable<FreeSlot> {

  private final LocalDateTime start;
  private final LocalDateTime end;
  private final ScheduleId scheduleId;
  private final Duration duration;

  public FreeSlot(ScheduleId scheduleId, LocalDateTime start, LocalDateTime end, Duration duration) {
    this.start = start;
    this.end = end;
    this.scheduleId = scheduleId;
    this.duration = duration;
  }

  @Override
  public int compareTo(FreeSlot o) {

    Comparator<FreeSlot> freeSlotComparator = Comparator.comparing(FreeSlot::getStart)
            .thenComparing(FreeSlot::getEnd)
            .thenComparing(FreeSlot::duration)
            .thenComparing(fs -> fs.getScheduleId().toString());
//
     return freeSlotComparator.compare(this, o);
//    int dateCompare = start.compareTo(o.start);
//    return dateCompare != 0 ? dateCompare : this.scheduleId.toString().compareTo(o.scheduleId.toString());
  }

  public boolean contains(LocalDateTime dateTime, Duration duration) {
    return !this.start.isAfter(dateTime) && !this.end.isBefore(dateTime.plus(duration));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    FreeSlot freeSlot = (FreeSlot) o;

    if (!start.equals(freeSlot.start))
      return false;
    if (!end.equals(freeSlot.end))
      return false;
    if (!scheduleId.equals(freeSlot.scheduleId))
      return false;
    return duration.equals(freeSlot.duration);

  }

  @Override
  public int hashCode() {
    int result = start.hashCode();
    result = 31 * result + end.hashCode();
    result = 31 * result + scheduleId.hashCode();
    result = 31 * result + duration.hashCode();
    return result;
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

  public Duration duration() {
    return this.duration;
  }

  public FreeSlot withNewEnd(LocalDateTime newEnd) {
    return of(scheduleId, new FromTo(start, newEnd), duration);
  }

  public FreeSlot withNewStart(LocalDateTime newStart) {
    return of(scheduleId, new FromTo(newStart, end), duration);
  }

  public static FreeSlot of(ScheduleId scheduleId, FromTo fromTo, Duration duration) {
    return new FreeSlot(scheduleId, fromTo.getStart(), fromTo.getEnd(), duration);
  }

  @Override
  public String toString() {
    return String.format("FreeSlot{start=%s, end=%s, duration=%s, scheduleId=%s}", start, end, duration, scheduleId);
  }
}
