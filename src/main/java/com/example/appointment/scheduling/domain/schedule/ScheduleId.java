package com.example.appointment.scheduling.domain.schedule;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

public class ScheduleId {

    private final UUID id;

    public ScheduleId() {
        id = UUID.randomUUID();
    }

    public ScheduleId(String id) {
        this.id = UUID.fromString(id);
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
        return "ScheduleId{" +
                "id=" + id +
                '}';
    }

    public static ScheduleId newId() {
        return new ScheduleId();
    }

    public UUID id() {
        return id;
    }

    public static ScheduleId of(String id) {
        return new ScheduleId(id);
    }
}
