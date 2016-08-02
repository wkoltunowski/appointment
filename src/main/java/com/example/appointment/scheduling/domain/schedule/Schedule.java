package com.example.appointment.scheduling.domain.schedule;

import com.example.appointment.scheduling.domain.SearchTags;
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
    private final Optional<Duration> duration;
    private Validity validity = Validity.infinite();
    private WorkingHours workingHours;
    private SearchTags searchTags;


    public Schedule(WorkingHours workingHours, Validity validity, Optional<Duration> duration, SearchTags searchTags) {
        this.scheduleId = ScheduleId.newId();
        this.workingHours = workingHours;
        this.validity = validity;
        this.searchTags = searchTags;
        this.duration = duration;
    }

    public Schedule(WorkingHours workingHours, Validity validity) {
        this(workingHours, validity, Optional.empty(), SearchTags.empty());
    }


    public ScheduleId scheduleId() {
        return scheduleId;
    }


    public SearchTags searchTags() {
        return this.searchTags;
    }


    public Optional<Duration> duration() {
        return duration;
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
