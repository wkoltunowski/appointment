package com.example.appointment.visitreservation.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class LocationId {
    private final UUID id;

    public LocationId() {
        id = UUID.randomUUID();
    }

    public LocationId(UUID uuid) {
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
        return "LocationId{" +
                "id=" + id +
                '}';
    }

    public static LocationId newId() {
        return new LocationId();
    }

    public String asString() {
        return id.toString();
    }

    public static LocationId of(String id) {
        return new LocationId(UUID.fromString(id));
    }
}
