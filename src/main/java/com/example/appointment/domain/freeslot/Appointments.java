package com.example.appointment.domain.freeslot;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static java.util.Arrays.asList;

public class Appointments {

    private final TreeSet<Appointment> appointments;
    public static final Comparator<Appointment> START_THEN_SCHEDULE_ID_COMPARATOR =
            Comparator.comparing(Appointment::start)
                    .thenComparing(Comparator.comparing(v -> v.scheduleId().toString()));

    public TreeSet<Appointment> getAppointments() {
        return appointments;
    }

    public Appointments(Collection<Appointment> appointments) {
        this.appointments = new TreeSet<>(START_THEN_SCHEDULE_ID_COMPARATOR);
        this.appointments.addAll(appointments);
    }

    public static Appointments of(List<Appointment> appointments) {
        return new Appointments(appointments);
    }

    public static Appointments of(Appointment... appointments) {
        return of(asList(appointments));
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
