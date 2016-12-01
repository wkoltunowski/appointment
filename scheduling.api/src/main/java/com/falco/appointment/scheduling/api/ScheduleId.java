package com.falco.appointment.scheduling.api;

import java.util.UUID;

public class ScheduleId {

    private static final ScheduleId EMPTY = new ScheduleId(new UUID(0, 0).toString());
    private final UUID id;

    public ScheduleId() {
        id = UUID.randomUUID();
    }

    public ScheduleId(String id) {
        this.id = UUID.fromString(id);
    }

//    @Override
//    public boolean equals(Object o) {
//        return EqualsBuilder.reflectionEquals(this, o);
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleId that = (ScheduleId) o;

        return id.equals(that.id);

    }

//    @Override
//    public int hashCode() {
//        return HashCodeBuilder.reflectionHashCode(this);
//    }


    @Override
    public int hashCode() {
        return id.hashCode();
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

    public String asString() {
        return id.toString();
    }

    public static ScheduleId empty() {
        return EMPTY;
    }
}
