package com.example.appointment.tmp;

import com.example.appointment.domain.schedule.ScheduleId;
import com.google.common.collect.Range;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

public class ReservationCandidate {
    private ScheduleId scheduleId;
    private Range<LocalDateTime> range;

    public static ReservationCandidate reservationFor(Range<LocalDateTime> today, ScheduleId scheduleId) {
        ReservationCandidate reservationCandidate = new ReservationCandidate();
        reservationCandidate.range = today;
        reservationCandidate.scheduleId = scheduleId;
        return reservationCandidate;
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
}
