package com.example.appointment.domain.schedule;

import com.example.appointment.domain.schedule.ScheduleId;

public class ScheduleAddedEvent {
    private final ScheduleId scheduleId;

    public ScheduleAddedEvent(ScheduleId scheduleId) {
        this.scheduleId = scheduleId;
    }

    public ScheduleId scheduleId() {
        return scheduleId;
    }
}