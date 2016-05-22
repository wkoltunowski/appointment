package com.example.appointment.domain.freescheduleranges;

import com.example.appointment.domain.schedule.ScheduleId;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class FreeScheduleSlot implements Comparable<FreeScheduleSlot> {

    private final ScheduleId scheduleId;
    private final Range<LocalDateTime> range;
    private final SearchTags searchTags;

    public FreeScheduleSlot(ScheduleId scheduleId, Range<LocalDateTime> range, SearchTags searchTags) {
        this.range = range;
        this.scheduleId = scheduleId;
        this.searchTags = searchTags;
    }

    public Collection<FreeScheduleSlot> splitFor(Range<LocalDateTime> range) {

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
    public int compareTo(FreeScheduleSlot o) {
        Comparator<FreeScheduleSlot> freeSlotComparator = Comparator
                .comparing(FreeScheduleSlot::start)
                .thenComparing(FreeScheduleSlot::end)
                .thenComparing(fs -> fs.scheduleId().toString());
        return freeSlotComparator.compare(this, o);
    }

    public boolean contains(Range<LocalDateTime> range) {
        return this.range.encloses(range);
    }

    public Range<LocalDateTime> range() {
        return range;
    }


    public Iterable<ScheduleRange> appointmentsFor(LocalDateTime startingDate, Duration duration) {
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

    public FreeScheduleSlot withRange(Range<LocalDateTime> newRange) {
        return of(scheduleId, newRange, searchTags);
    }

    public static FreeScheduleSlot of(ScheduleId scheduleId, Range<LocalDateTime> range, SearchTags searchTags) {
        return new FreeScheduleSlot(scheduleId, range, searchTags);
    }


    public boolean matches(SearchTags searchTags) {
        return this.searchTags.matches(searchTags);
    }

    public FreeScheduleSlot withSearchTags(SearchTags searchTags) {
        return of(scheduleId, range, searchTags);
    }

    private class AppointmentsIterator extends AbstractIterator<ScheduleRange> {

        private final FreeScheduleSlot fs;

        private final Duration duration;
        private LocalDateTime date;

        public AppointmentsIterator(FreeScheduleSlot fs, LocalDateTime startingDate, Duration duration) {
            this.fs = fs;
            this.duration = duration;
            this.date = calcStartingDate(startingDate.truncatedTo(ChronoUnit.MINUTES));
        }

        @Override
        protected ScheduleRange computeNext() {
            if (!date.isBefore(fs.end())) {
                return endOfData();
            }
            LocalDateTime oldDate = this.date;
            this.date = this.date.plus(duration);
            return ScheduleRange.of(oldDate, duration, fs.scheduleId());
        }

        private LocalDateTime calcStartingDate(LocalDateTime appointmentDate) {
            if (fs.start().isAfter(appointmentDate)) {
                return fs.start();
            }
            return fs.start().plus(Duration.ofSeconds(this.duration.getSeconds() * durationsBetween(fs.start(), appointmentDate)));
        }

        private long durationsBetween(LocalDateTime aBeginning, LocalDateTime anEnd) {
            long secondsBetweenStartMax = Duration.between(aBeginning, anEnd).getSeconds();
            long durationInSeconds = this.duration.getSeconds();
            long appointmentFromStart = secondsBetweenStartMax / durationInSeconds;
            int mod = (secondsBetweenStartMax % durationInSeconds) > 0 ? 1 : 0;
            return appointmentFromStart + mod;
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
