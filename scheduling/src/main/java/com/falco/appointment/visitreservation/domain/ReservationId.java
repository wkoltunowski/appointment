package com.falco.appointment.visitreservation.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class ReservationId {
    private final UUID id;

    public ReservationId() {
        id = UUID.randomUUID();
    }

    public ReservationId(UUID uuid) {
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
        return String.format("ReservationId{id=%s}", id);
    }

    public static ReservationId newId() {
        return new ReservationId();
    }

    public String asString() {
        return id.toString();
    }

    public static ReservationId of(String id) {
        return new ReservationId(UUID.fromString(id));
    }

}
