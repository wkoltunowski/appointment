package com.falco.appointment.scheduling.domain.schedule;

import com.falco.appointment.scheduling.domain.SearchTags;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class DailySchedule implements Schedule {

    private final ScheduleId scheduleId;
    private final Optional<Duration> duration;
    private final Validity validity;
    private final WorkingHours workingHours;
    private final SearchTags searchTags;


    public DailySchedule(WorkingHours workingHours, Validity validity, Optional<Duration> duration, SearchTags searchTags) {
        this.scheduleId = ScheduleId.newId();
        this.workingHours = workingHours;
        this.validity = validity;
        this.searchTags = searchTags;
        this.duration = duration;
    }

    @Override
    public ScheduleId scheduleId() {
        return scheduleId;
    }


    @Override
    public SearchTags searchTags() {
        return this.searchTags;
    }


    @Override
    public Optional<Duration> duration() {
        return duration;
    }

    @Override
    public List<Range<LocalDateTime>> dates(Range<LocalDate> range) {
//        return withWhile(range);
        return withContSet(range);
    }

    private List<Range<LocalDateTime>> withWhile(Range<LocalDate> range) {
        ImmutableList.Builder<Range<LocalDateTime>> builder = ImmutableList.builder();
        LocalDate day = range.lowerEndpoint();

        while (day.isBefore(range.upperEndpoint())) {
            builder.add(rangeForDay(day));
            day = day.plusDays(1);
        }
        return builder.build();
    }

    private List<Range<LocalDateTime>> withContSet(Range<LocalDate> range) {
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
}
