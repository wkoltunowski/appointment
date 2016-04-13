package com.example.appointment.domain.freescheduleranges;

import com.example.appointment.domain.schedule.ScheduleId;
import com.google.common.collect.Range;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

public class ScheduleRange {
    private ScheduleId scheduleId;
    private Range<LocalDateTime> range;

    public static ScheduleRange of(Range<LocalDateTime> today, ScheduleId scheduleId) {
        ScheduleRange scheduleRange = new ScheduleRange();
        scheduleRange.range = today;
        scheduleRange.scheduleId = scheduleId;
        return scheduleRange;
    }

    public Range<LocalDateTime> range() {
        return range;
    }

    public ScheduleId scheduleId() {
        return scheduleId;
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public String toString() {
        return "ReservationCandidate{" +
                "range=" + range +
                ", scheduleId=" + scheduleId +
                '}';
    }

    public static ScheduleRange of(LocalDateTime start, Duration duration, ScheduleId scheduleId) {
        return of(Range.closedOpen(start, start.plus(duration)), scheduleId);
    }

    public LocalDateTime start() {
        return range.lowerEndpoint();
    }

    public Duration duration() {
        return Duration.between(range.lowerEndpoint(), range.upperEndpoint());
    }
}
