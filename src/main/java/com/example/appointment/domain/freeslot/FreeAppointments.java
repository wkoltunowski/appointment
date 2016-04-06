package com.example.appointment.domain.freeslot;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static java.util.Arrays.asList;

public class FreeAppointments {

    private final TreeSet<FreeAppointment> freeAppointments;
    public static final Comparator<FreeAppointment> START_THEN_SCHEDULE_ID_COMPARATOR =
            Comparator.comparing(FreeAppointment::start)
                    .thenComparing(Comparator.comparing(v -> v.scheduleId().toString()));

    public TreeSet<FreeAppointment> getFreeAppointments() {
        return freeAppointments;
    }

    public FreeAppointments(Collection<FreeAppointment> freeAppointments) {
        this.freeAppointments = new TreeSet<>(START_THEN_SCHEDULE_ID_COMPARATOR);
        this.freeAppointments.addAll(freeAppointments);
    }

    public static FreeAppointments of(List<FreeAppointment> freeAppointments) {
        return new FreeAppointments(freeAppointments);
    }

    public static FreeAppointments of(FreeAppointment... freeAppointments) {
        return of(asList(freeAppointments));
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
