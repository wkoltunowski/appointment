package com.falco.appointment.scheduling.domain.freescheduleranges;

import com.falco.appointment.scheduling.domain.schedule.ScheduleId;
import com.google.common.collect.Range;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleRange that = (ScheduleRange) o;

        if (!scheduleId.equals(that.scheduleId)) return false;
        return range.equals(that.range);

    }

    @Override
    public int hashCode() {
        int result = scheduleId.hashCode();
        result = 31 * result + range.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ReservationCandidate{" +
                "range=" + range +
                ", scheduleId=" + scheduleId +
                '}';
    }


    public static ScheduleRange scheduleRange(Range<LocalDateTime> range, ScheduleId scheduleId) {
        ScheduleRange scheduleRange = new ScheduleRange();
        scheduleRange.scheduleId = scheduleId;
        scheduleRange.range = range;
        return scheduleRange;
    }

    public static ScheduleRange scheduleRange(LocalDateTime start, LocalDateTime end, ScheduleId scheduleId) {
        ScheduleRange scheduleRange = new ScheduleRange();
        scheduleRange.range = Range.closedOpen(start, end);
        scheduleRange.scheduleId = scheduleId;
        return scheduleRange;
    }

    public LocalDateTime start() {
        return this.range.lowerEndpoint();
    }

    public LocalDateTime end() {
        return this.range.upperEndpoint();
    }


    public Duration duration() {
        return Duration.between(range.lowerEndpoint(), range.upperEndpoint());
    }
}
