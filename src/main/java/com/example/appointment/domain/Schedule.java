package com.example.appointment.domain;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Schedule {

    private final ScheduleId scheduleId;
    private final ScheduleHours scheduleHours;
    private final Validity validity;
    private final Period maxReservationsInAdvance;

    public Schedule(ScheduleHours scheduleHours, Validity validity, ScheduleId scheduleId) {
        this.scheduleHours = scheduleHours;
        this.validity = validity;
        this.scheduleId = scheduleId;
        this.maxReservationsInAdvance = Period.ofDays(90);
    }

    public Schedule(ScheduleId scheduleId, ScheduleHours scheduleHours) {
        this(scheduleHours, Validity.infinite(), scheduleId);
    }


    private Range<LocalDateTime> createRange(LocalDate date) {
        return scheduleHours.toRange(date);

    }

    public FreeSlot buildFreeSlot(LocalDate date) {
        return FreeSlot.of(scheduleId, createRange(date));
    }

    public List<FreeSlot> buildFreeSlots(LocalDate startingFrom) {
        return buildSlots(Range.closed(startingFrom, startingFrom.plus(maxReservationsInAdvance)));
    }

    private List<FreeSlot> buildSlots(Range<LocalDate> range) {
        Range<LocalDate> rangeValidityIntersection = validity.range().intersection(range);
        return ContiguousSet.create(rangeValidityIntersection, DaysDomain.daysDomain())
                .stream()
                .map(this::buildFreeSlot)
                .collect(toList());
    }
}
