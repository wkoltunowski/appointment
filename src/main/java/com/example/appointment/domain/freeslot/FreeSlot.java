package com.example.appointment.domain.freeslot;

import com.example.appointment.domain.schedule.ScheduleId;
import com.example.appointment.domain.schedule.SearchTags;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;

public class FreeSlot implements Comparable<FreeSlot> {

    private final ScheduleId scheduleId;
    private final Range<LocalDateTime> range;
    private final SearchTags searchTags;

    public FreeSlot(ScheduleId scheduleId, Range<LocalDateTime> range, SearchTags searchTags) {
        this.range = range;
        this.scheduleId = scheduleId;
        this.searchTags = searchTags;
    }

    public Collection<FreeSlot> splitFor(Range<LocalDateTime> range) {

        RangeSet<LocalDateTime> rangeSet = TreeRangeSet.create();
        rangeSet.add(this.range);
        rangeSet.remove(range);

        return rangeSet
                .asRanges()
                .stream()
                .map(this::withRange)
                .collect(Collectors.toList());

    }

    @Override
    public int compareTo(FreeSlot o) {
        Comparator<FreeSlot> freeSlotComparator = Comparator
                .comparing(FreeSlot::start)
                .thenComparing(FreeSlot::end)
                .thenComparing(fs -> fs.scheduleId().toString());
        return freeSlotComparator.compare(this, o);
    }

    public boolean contains(Range<LocalDateTime> range) {
        return this.range.encloses(range);
    }


    public Iterable<Appointment> appointmentsFor(LocalDateTime startingDate, Duration duration) {
        return () -> new AppointmentsIterator(this, startingDate, duration);
    }

    public LocalDateTime start() {
        return range.lowerEndpoint();
    }

    public LocalDateTime end() {
        return range.upperEndpoint();
    }

    public ScheduleId scheduleId() {
        return scheduleId;
    }

    public FreeSlot withRange(Range<LocalDateTime> newRange) {
        return of(scheduleId, newRange, searchTags);
    }

    public static FreeSlot of(ScheduleId scheduleId, Range<LocalDateTime> range, SearchTags searchTags) {
        return new FreeSlot(scheduleId, range, searchTags);
    }

    public SearchTags searchTags() {
        return searchTags;
    }

    public boolean matches(SearchTags searchTags) {
        return this.searchTags.matches(searchTags);
    }

    public FreeSlot withSearchTags(SearchTags searchTags) {
        return of(scheduleId, range, searchTags);
    }

    private class AppointmentsIterator implements Iterator<Appointment> {

        private final FreeSlot fs;

        private final Duration duration;
        private LocalDateTime date;

        public AppointmentsIterator(FreeSlot fs, LocalDateTime startingDate, Duration duration) {
            this.fs = fs;
            this.duration = duration;
            this.date = calcStartingDate(startingDate);
        }

        private LocalDateTime calcStartingDate(LocalDateTime appointmentDate) {
            long appointmentsFromSlotStart = calculateAppointmentNo(appointmentDate);
            return fs.start().plus(this.duration.multipliedBy(appointmentsFromSlotStart));
        }

        private long calculateAppointmentNo(LocalDateTime startingDate) {
            LocalDateTime max = ObjectUtils.max(startingDate, fs.start());
            long secondsBetweenStartMax = Duration.between(fs.start(), max).getSeconds();
            long durationInSeconds = this.duration.getSeconds();
            long appointmentFromStart = secondsBetweenStartMax / durationInSeconds;
            int mod = (secondsBetweenStartMax % durationInSeconds) > 0 ? 1 : 0;
            return appointmentFromStart + mod;
        }

        @Override
        public boolean hasNext() {
            return !date.plus(duration).isAfter(fs.end());
        }

        @Override
        public Appointment next() {
            LocalDateTime oldDate = this.date;
            this.date = this.date.plus(duration);
            return Appointment.appointmentFor(oldDate, duration, fs.scheduleId());
        }

    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format("FreeSlot{range=%s, scheduleId=%s}", range, scheduleId);
    }
}
