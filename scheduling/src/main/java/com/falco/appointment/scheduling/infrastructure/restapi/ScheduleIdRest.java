package com.falco.appointment.scheduling.infrastructure.restapi;

public class ScheduleIdRest {
    private final String scheduleId;

    public ScheduleIdRest(String id) {
        this.scheduleId = id;
    }

    public String getScheduleId() {
        return scheduleId;
    }
}
