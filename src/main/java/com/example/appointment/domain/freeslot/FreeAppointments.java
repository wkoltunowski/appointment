package com.example.appointment.domain.freeslot;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static java.util.Arrays.asList;

public class FreeAppointments {

    private final TreeSet<ScheduleRange> scheduleRanges;
    public static final Comparator<ScheduleRange> START_THEN_SCHEDULE_ID_COMPARATOR =
            Comparator.comparing(ScheduleRange::start)
                    .thenComparing(fa -> Duration.between(fa.range().lowerEndpoint(), fa.range().upperEndpoint()))
                    .thenComparing(Comparator.comparing(v -> v.scheduleId().toString()));

    public TreeSet<ScheduleRange> getScheduleRanges() {
        return scheduleRanges;
    }

    public FreeAppointments(Collection<ScheduleRange> scheduleRanges) {
        this.scheduleRanges = new TreeSet<>(START_THEN_SCHEDULE_ID_COMPARATOR);
        this.scheduleRanges.addAll(scheduleRanges);
    }

    public static FreeAppointments of(List<ScheduleRange> scheduleRanges) {
        return new FreeAppointments(scheduleRanges);
    }

    public static FreeAppointments of(ScheduleRange... scheduleRanges) {
        return of(asList(scheduleRanges));
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
        return ToStringBuilder.reflectionToString(this);
    }


}
