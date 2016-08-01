package com.example.appointment.domain.schedule;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class ServiceId {
    private final UUID id;

    public ServiceId() {
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
        return "ServiceId{" +
                "id=" + id +
                '}';
    }

    public static ServiceId newId() {
        return new ServiceId();
    }
}
