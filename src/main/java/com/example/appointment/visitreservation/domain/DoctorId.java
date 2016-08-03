package com.example.appointment.visitreservation.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class DoctorId {
    private final UUID id;

    private DoctorId(UUID id) {
        this.id = id;
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
        return "DoctorId{" +
                "id=" + id +
                '}';
    }

    public static DoctorId newId() {
        return new DoctorId(UUID.randomUUID());
    }

    public String asString() {
        return id.toString();
    }

    public static DoctorId of(String id) {
        return new DoctorId(UUID.fromString(id));
    }
}
