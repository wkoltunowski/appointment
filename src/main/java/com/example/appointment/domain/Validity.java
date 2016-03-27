package com.example.appointment.domain;

import static java.util.Optional.*;

import java.time.LocalDate;
import java.util.Optional;

public class Validity {

  private final Optional<LocalDate> validFrom;
  private final Optional<LocalDate> validTo;

  public Validity(Optional<LocalDate> validFrom, Optional<LocalDate> validTo) {
    this.validFrom = validFrom;
    this.validTo = validTo;
  }

  public static Validity infinite() {
    return new Validity(empty(), empty());
  }

  public static Validity fromTo(LocalDate now, LocalDate now1) {
    return new Validity(Optional.of(now), Optional.of(now1));
  }


  public boolean validFor(LocalDate date) {
    return !validFrom.orElse(LocalDate.MIN).isAfter(date) && !validTo.orElse(LocalDate.MAX).isBefore(date);
  }
}
