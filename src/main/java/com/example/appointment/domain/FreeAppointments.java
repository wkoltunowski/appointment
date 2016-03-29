package com.example.appointment.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

import static java.util.Arrays.asList;

public class FreeAppointments {

    private static final FreeAppointments EMPTY = new FreeAppointments(Collections.emptyList());
    private final TreeSet<Appointment> appointments;

    public TreeSet<Appointment> getAppointments() {
        return appointments;
    }

    public FreeAppointments(Collection<Appointment> appointments) {
        Comparator<Appointment> dateScheduleIdComparator = Comparator.comparing(Appointment::getDateTime)
                .thenComparing(Comparator.comparing(v -> v.scheduleId().toString()));

        this.appointments = new TreeSet<>(dateScheduleIdComparator);
        this.appointments.addAll(appointments);
    }

    public static FreeAppointments of(List<Appointment> appointments) {
        return new FreeAppointments(appointments);
    }

    public static FreeAppointments of(Appointment... appointments) {
        return new FreeAppointments(asList(appointments));
    }

    public static FreeAppointments empty() {
        return EMPTY;
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

    public FreeAppointments add(Appointment appointment) {
        ArrayList<Appointment> newAppointments = new ArrayList<>(this.appointments);
        newAppointments.add(appointment);
        return new FreeAppointments(newAppointments);
    }
}
