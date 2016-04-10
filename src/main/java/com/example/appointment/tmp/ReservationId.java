package com.example.appointment.tmp;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class ReservationId {
    private final UUID id;

    public ReservationId() {
        id = UUID.randomUUID();
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

    public static ReservationId newId() {
        return new ReservationId();
    }



    public static ReservationId randomId() {
        return newId();
    }
}