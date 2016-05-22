package com.example.appointment.domain.freescheduleranges;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Duration;
import java.util.*;

import static java.util.Arrays.asList;

public class FreeScheduleRanges {

    private final Collection<ScheduleRange> scheduleRanges;
    public static final Comparator<ScheduleRange> START_THEN_SCHEDULE_ID_COMPARATOR =
            Comparator.comparing(ScheduleRange::start)
                    .thenComparing(fa -> Duration.between(fa.range().lowerEndpoint(), fa.range().upperEndpoint()))
                    .thenComparing(Comparator.comparing(v -> v.scheduleId().toString()));

    public TreeSet<ScheduleRange> getScheduleRanges() {
        TreeSet<ScheduleRange> scheduleRanges1 = new TreeSet<>(START_THEN_SCHEDULE_ID_COMPARATOR);
        scheduleRanges1.addAll(scheduleRanges);
        return scheduleRanges1;
    }

    public FreeScheduleRanges(Collection<ScheduleRange> scheduleRanges) {
        this.scheduleRanges = scheduleRanges;
    }

    public static FreeScheduleRanges of(List<ScheduleRange> scheduleRanges) {
        return new FreeScheduleRanges(scheduleRanges);
    }

    public static FreeScheduleRanges of(ScheduleRange... scheduleRanges) {
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


    public Optional<ScheduleRange> first() {
        return scheduleRanges.stream().findFirst();
    }
}
