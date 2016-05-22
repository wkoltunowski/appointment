package com.example.appointment.domain.freescheduleranges;

import com.example.appointment.domain.schedule.ScheduleId;
import com.google.common.collect.Range;

import java.time.Duration;
import java.time.LocalDateTime;

public class ScheduleRange {
    private ScheduleId scheduleId;
    private LocalDateTime start;
    private Duration duration;
    private Range<LocalDateTime> range;

    public Range<LocalDateTime> range() {
        if (range == null) {
            range = Range.closedOpen(start, start.plus(duration));
        }
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
        if (!start.equals(that.start)) return false;
        return duration.equals(that.duration);

    }

    @Override
    public int hashCode() {
        int result = scheduleId.hashCode();
        result = 31 * result + start.hashCode();
        result = 31 * result + duration.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ReservationCandidate{" +
                "start=" + start +
                ", duration=" + duration +
                ", scheduleId=" + scheduleId +
                '}';
    }


    public static ScheduleRange of(Range<LocalDateTime> today, ScheduleId scheduleId) {
        return of(today.lowerEndpoint(), Duration.between(today.lowerEndpoint(), today.upperEndpoint()), scheduleId);
    }

    public static ScheduleRange of(LocalDateTime start, Duration duration, ScheduleId scheduleId) {
        ScheduleRange scheduleRange = new ScheduleRange();
        scheduleRange.start = start;
        scheduleRange.duration = duration;
        scheduleRange.scheduleId = scheduleId;
        return scheduleRange;
    }

    public LocalDateTime start() {
        return start;
    }

    public Duration duration() {
        return duration;
    }
}
