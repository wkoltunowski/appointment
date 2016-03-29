package com.example.appointment.domain;

import com.google.common.collect.Range;

import java.time.LocalDate;

public class Validity {

    private final Range<LocalDate> range;

    Validity(Range<LocalDate> range) {
        this.range = range;
    }

    public static Validity infinite() {
        return new Validity(Range.all());
    }

    public static Validity fromTo(LocalDate from, LocalDate to) {
        return new Validity(Range.closed(from, to));
    }

    public boolean validFor(LocalDate date) {
        return range.contains(date);
    }
}
