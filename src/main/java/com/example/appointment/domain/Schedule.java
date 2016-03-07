package com.example.appointment.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.appointment.Validity;

public class Schedule {

  private final ScheduleId scheduleId;
  private final LocalTime end;
  private final LocalTime start;
  private final Validity validity;
  private final int maxReservationsInAdvance;

  public Schedule(LocalTime from, LocalTime to, ScheduleId scheduleId) {
    this(from, to, Validity.infinite(), scheduleId);
  }

  public Schedule(LocalTime startTime, LocalTime endTime, Validity validity, ScheduleId scheduleId) {
    this.start = startTime;
    this.end = endTime;
    this.validity = validity;
    this.scheduleId = scheduleId;

    maxReservationsInAdvance = 90;
  }

  public LocalTime getStart() {
    return start;
  }

  public LocalTime getEnd() {
    return end;
  }

  public ScheduleId getScheduleId() {
    return scheduleId;
  }

  public int maxReservationInAdvance() {
    return maxReservationsInAdvance;
  }

  public Validity validity() {
    return validity;
  }

  public boolean validFor(LocalDate date) {
    return validity.validFor(date);
  }
}
