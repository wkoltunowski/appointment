package com.example.appointment.domain.schedule;

import com.example.appointment.domain.DaysDomain;
import com.example.appointment.domain.freeslot.FreeSlot;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Range;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Schedule {

    private final ScheduleId scheduleId;
    private final WorkingHours workingHours;
    private final Duration appointmentDuration;
    private final Validity validity;
    private final SearchTags searchTags;

    public Schedule(ScheduleId scheduleId, WorkingHours workingHours, Validity validity, SearchTags searchTags, Duration duration) {
        this.workingHours = workingHours;
        this.validity = validity;
        this.scheduleId = scheduleId;
        this.searchTags = searchTags;
        this.appointmentDuration = duration;
    }


    public List<FreeSlot> buildFreeSlots(Range<LocalDate> range) {
        Range<LocalDate> rangeValidityIntersection = validity
                .range()
                .intersection(range);
        return ContiguousSet
                .create(rangeValidityIntersection, DaysDomain.daysDomain())
                .stream()
                .map(this::buildFreeSlot)
                .collect(toList());
    }

    private FreeSlot buildFreeSlot(LocalDate date) {
        return FreeSlot.of(scheduleId, workingHours.toRange(date), this.searchTags);
    }


    public ScheduleId scheduleId() {
        return scheduleId;
    }

    public Duration duration() {
        return appointmentDuration;
    }
}
