package com.example.appointment.domain.freeslots;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Iterator;

import com.example.appointment.domain.ScheduleId;
import com.example.appointment.domain.Visit;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Range;

public class FreeSlot implements Comparable<FreeSlot> {

  private final ScheduleId scheduleId;
  private final Range<LocalDateTime> range;

  public FreeSlot(ScheduleId scheduleId, Range<LocalDateTime> range) {
    this.range = range;
    this.scheduleId = scheduleId;
  }

  @Override
  public int compareTo(FreeSlot o) {
    Comparator<FreeSlot> freeSlotComparator = Comparator
        .comparing(FreeSlot::getStart)
        .thenComparing(FreeSlot::getEnd)
        .thenComparing(fs -> fs.getScheduleId().toString());
    return freeSlotComparator.compare(this, o);
  }

  public boolean contains(Range<LocalDateTime> closed) {
    return range.encloses(closed);
  }

  public Iterable<Visit> visits(FreeSlot fs, Duration duration) {
    return () -> new VisitsIterator(fs, duration);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  public LocalDateTime getStart() {
    return range.lowerEndpoint();
  }

  public LocalDateTime getEnd() {
    return range.upperEndpoint();
  }

  public ScheduleId getScheduleId() {
    return scheduleId;
  }

  public FreeSlot withRange(Range<LocalDateTime> range) {
    return of(scheduleId, range);
  }

  public boolean isEmpty() {
    return range.isEmpty();
  }

  public static FreeSlot of(ScheduleId scheduleId, Range<LocalDateTime> range) {
    return new FreeSlot(scheduleId, range);
  }

  @Override
  public String toString() {
    return String.format("FreeSlot{range=%s, scheduleId=%s}", range, scheduleId);
  }

  private class VisitsIterator implements Iterator<Visit> {

    private final FreeSlot fs;
    private final Duration duration;
    private LocalDateTime date;

    public VisitsIterator(FreeSlot fs, Duration duration) {
      this.fs = fs;
      this.duration = duration;
      this.date = fs.getStart();
    }

    @Override
    public boolean hasNext() {
      return !date.plus(duration).isAfter(fs.getEnd());
    }

    @Override
    public Visit next() {
      LocalDateTime oldDate = this.date;
      this.date = this.date.plus(duration);
      return Visit.visitFor(oldDate, duration, fs.getScheduleId());
    }
  }
}
