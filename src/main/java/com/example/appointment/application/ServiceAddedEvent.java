package com.example.appointment.application;

import com.example.appointment.ServiceId;
import com.example.appointment.domain.schedule.ScheduleId;

public class ServiceAddedEvent {
    private final ScheduleId scheduleId;
    private final ServiceId serviceId;

    public ServiceAddedEvent(ScheduleId scheduleId, ServiceId serviceId) {
        this.scheduleId = scheduleId;
        this.serviceId = serviceId;
    }

    public ScheduleId scheduleId() {
        return scheduleId;
    }

    public ServiceId serviceId() {
        return serviceId;
    }
}
