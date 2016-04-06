package com.example.appointment.domain.freeslot;

import com.example.appointment.domain.schedule.ScheduleId;
import com.google.common.collect.Range;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.google.common.collect.Range.closedOpen;

public class FreeAppointment {

    private final ScheduleId scheduleId;
    private final Range<LocalDateTime> range;

    public FreeAppointment(ScheduleId scheduleId, Range<LocalDateTime> range) {
        this.scheduleId = scheduleId;
        this.range = range;
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

    public Range<LocalDateTime> range() {
        return range;
    }

    @Override
    public String toString() {
        return String.format("Appointment{range=%s, scheduleId=%s}", range, scheduleId);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public static FreeAppointment appointmentFor(LocalDateTime dateTime, Duration duration, ScheduleId scheduleId) {
        return new FreeAppointment(scheduleId, closedOpen(dateTime, dateTime.plus(duration)));
    }

    public static FreeAppointment appointmentFor(LocalDateTime start, LocalDateTime end, ScheduleId scheduleId) {
        return appointmentFor(closedOpen(start, end), scheduleId);
    }

    public static FreeAppointment appointmentFor(Range<LocalDateTime> range, ScheduleId scheduleId) {
        return new FreeAppointment(scheduleId, range);
    }
}
