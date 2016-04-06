package com.example.appointment.domain.schedule;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Range;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class Schedule {

    private final ScheduleId scheduleId;
    private Validity validity = Validity.infinite();
    private WorkingHours workingHours;
    private ScheduleConnections scheduleDefinition;


    public Schedule(WorkingHours workingHours, Validity validity, ScheduleConnections scheduleDefinition) {
        this.scheduleId = ScheduleId.newId();
        this.workingHours = workingHours;
        this.validity = validity;
        this.scheduleDefinition = scheduleDefinition;
    }

    public Schedule(WorkingHours workingHours, Validity validity) {
        this(workingHours, validity, ScheduleConnections.empty());
    }

    public Schedule(WorkingHours workingHours) {
        this(workingHours, Validity.infinite(), ScheduleConnections.empty());
    }

    public Schedule(WorkingHours workingHours, ScheduleConnections scheduleDefinition) {
        this(workingHours, Validity.infinite(), scheduleDefinition);
    }

    public ScheduleId scheduleId() {
        return scheduleId;
    }


    public ScheduleConnections scheduleDefinition() {
        return this.scheduleDefinition;
    }


    public Optional<Duration> duration() {
        return scheduleDefinition.duration();
    }

    public List<Range<LocalDateTime>> dates(Range<LocalDate> range) {
        Range<LocalDate> rangeValidityIntersection = validity.range().intersection(range);
        return ContiguousSet
                .create(rangeValidityIntersection, DaysDomain.daysDomain())
                .stream()
                .map(this::rangeForDay)
                .collect(toList());
    }

    private Range<LocalDateTime> rangeForDay(LocalDate date) {
        return workingHours.toRange(date);
    }


    public static Schedule of(WorkingHours workingHours) {
        return new Schedule(workingHours, Validity.infinite());
    }
}
