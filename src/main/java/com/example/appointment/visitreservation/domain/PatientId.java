package com.example.appointment.visitreservation.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class PatientId {
    private final UUID id;

    public PatientId() {
        id = UUID.randomUUID();
    }

    public PatientId(UUID uuid) {
        this.id = uuid;
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
        return "ReservationId {" +
                "id=" + id +
                '}';
    }

    public static PatientId newId() {
        return new PatientId();
    }


    public static PatientId randomId() {
        return newId();
    }

    public static PatientId of(String id) {
        return new PatientId(UUID.fromString(id));
    }

    public String asString() {
        return id.toString();
    }
}
