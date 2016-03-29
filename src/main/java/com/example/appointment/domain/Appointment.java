package com.example.appointment.domain;

import com.google.common.collect.Range;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.google.common.collect.Ranges.closedOpen;

public class Appointment {

    private final ScheduleId scheduleId;
    private final Range<LocalDateTime> period;

    public Appointment(ScheduleId scheduleId, Range<LocalDateTime> period) {
        this.scheduleId = scheduleId;
        this.period = period;
    }

    public LocalDateTime start() {
        return period.lowerEndpoint();
    }

    public ScheduleId scheduleId() {
        return scheduleId;
    }

    public Range<LocalDateTime> range() {
        return period;
    }

    @Override
    public String toString() {
        return String.format("Appointment{range=%s, scheduleId=%s}", period, scheduleId);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public static Appointment appointmentFor(LocalDateTime dateTime, Duration duration, ScheduleId scheduleId) {
        return new Appointment(scheduleId, closedOpen(dateTime, dateTime.plus(duration)));
    }

    public static Appointment appointmentFor(LocalDateTime start, LocalDateTime end, ScheduleId scheduleId) {
        return appointmentFor(closedOpen(start, end), scheduleId);
    }

    public static Appointment appointmentFor(Range<LocalDateTime> range, ScheduleId scheduleId) {
        return new Appointment(scheduleId, range);
    }
}
