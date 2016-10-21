package com.falco.appointment.visitreservation.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class ServiceId {
    private final UUID id;

    public ServiceId() {
        id = UUID.randomUUID();
    }

    public ServiceId(String uuidAsString) {
        this.id = UUID.fromString(uuidAsString);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public String asString() {
        return id.toString();
    }

    @Override
    public String toString() {
        return "ServiceId{" +
                "id=" + id +
                '}';
    }

    public static ServiceId newId() {
        return new ServiceId();
    }

    public static ServiceId of(String id) {
        return new ServiceId(id);
    }
}
