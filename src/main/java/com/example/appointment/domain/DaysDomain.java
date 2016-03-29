package com.example.appointment.domain;

import com.google.common.collect.DiscreteDomain;

import java.time.LocalDate;
import java.time.Period;

public class DaysDomain extends DiscreteDomain<LocalDate> {

    private static final DaysDomain daysDomain = new DaysDomain();

    public static DiscreteDomain<LocalDate> daysDomain() {
        return daysDomain;
    }

    @Override
    public LocalDate next(LocalDate value) {
        return value.plusDays(1);
    }

    @Override
    public LocalDate previous(LocalDate value) {
        return value.minusDays(1);
    }

    @Override
    public long distance(LocalDate start, LocalDate end) {
        return daysBetween(start, end);
    }

    private int daysBetween(LocalDate start, LocalDate end) {
        return Period.between(start, end).getDays();
    }
}
