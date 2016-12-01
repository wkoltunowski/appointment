package com.falco.appointment.scheduling.domain.schedule;

import com.falco.appointment.scheduling.api.ScheduleId;

public class ScheduleAddedEvent {
    private final ScheduleId scheduleId;

    public ScheduleAddedEvent(ScheduleId scheduleId) {
        this.scheduleId = scheduleId;
    }

    public ScheduleId scheduleId() {
        return scheduleId;
    }
}
