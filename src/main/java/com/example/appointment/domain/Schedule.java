package com.example.appointment.domain;

import com.example.appointment.domain.freeslots.DaysDomain;
import com.example.appointment.domain.freeslots.FreeSlot;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Schedule {

    private final ScheduleId scheduleId;
    private final LocalTime start;
    private final LocalTime end;
    private final Validity validity;
    private final Period maxReservationsInAdvance;

    public Schedule(LocalTime startTime, LocalTime endTime, Validity validity, ScheduleId scheduleId) {
        this.start = startTime;
        this.end = endTime;
        this.validity = validity;
        this.scheduleId = scheduleId;
        this.maxReservationsInAdvance = Period.ofDays(90);
    }

    public Schedule(LocalTime from, LocalTime to, ScheduleId scheduleId) {
        this(from, to, Validity.infinite(), scheduleId);
    }

    public boolean validFor(LocalDate date) {
        return validity.validFor(date);
    }

    private Range<LocalDateTime> createRange(LocalDate date) {
        if (!start.isAfter(end)) {
            return Range.closedOpen(date.atTime(start), date.atTime(end));
        }
        return Range.closedOpen(date.atTime(start), date.plusDays(1).atTime(end));
    }

    public FreeSlot buildFreeSlot(LocalDate date) {
        return FreeSlot.of(scheduleId, createRange(date));
    }

    public List<FreeSlot> buildFreeSlots(LocalDate startingFrom) {
        return buildSlots(Range.closed(startingFrom, startingFrom.plus(maxReservationsInAdvance)));
    }

    private List<FreeSlot> buildSlots(Range<LocalDate> range) {
        return ContiguousSet.create(range, DaysDomain.daysDomain())
                .stream()
                .filter(this::validFor)
                .map(this::buildFreeSlot)
                .collect(toList());
    }
}
