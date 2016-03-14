package com.example.appointment.domain;

import java.time.LocalDateTime;

public class FromTo {

  private final LocalDateTime start;
  private final LocalDateTime end;

  public FromTo(LocalDateTime start, LocalDateTime end) {
    this.start = start;
    this.end = end;
  }

  public LocalDateTime getStart() {
    return start;
  }

  public LocalDateTime getEnd() {
    return end;
  }
}
