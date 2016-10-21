package com.falco.appointment.scheduling.domain.schedule;

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

    public static Validity validFromTo(LocalDate from, LocalDate to) {
        return new Validity(Range.closed(from, to));
    }

    public static Validity validTill(LocalDate to) {
        return new Validity(Range.atMost(to));
    }

    public Range<LocalDate> range() {
        return range;
    }
}
