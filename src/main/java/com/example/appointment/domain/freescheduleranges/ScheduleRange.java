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

    public Range<LocalDateTime> range() {
        return range;
    }

    public ScheduleId scheduleId() {
        return scheduleId;
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
        return "ReservationCandidate{" +
                "range=" + range +
                ", scheduleId=" + scheduleId +
                '}';
    }


    public static ScheduleRange scheduleRange(Range<LocalDateTime> today, ScheduleId scheduleId) {
        return scheduleRange(today.lowerEndpoint(), Duration.between(today.lowerEndpoint(), today.upperEndpoint()), scheduleId);
    }

    public static ScheduleRange scheduleRange(LocalDateTime start, Duration duration, ScheduleId scheduleId) {
        ScheduleRange scheduleRange = new ScheduleRange();
        scheduleRange.range = Range.closedOpen(start, start.plus(duration));
        scheduleRange.scheduleId = scheduleId;
        return scheduleRange;
    }

    public LocalDateTime start() {
        return range.lowerEndpoint();
    }


}
