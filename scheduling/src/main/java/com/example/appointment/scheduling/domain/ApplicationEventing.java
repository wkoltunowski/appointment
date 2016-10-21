package com.example.appointment.scheduling.domain;

import com.example.appointment.scheduling.domain.schedule.ScheduleAddedEvent;

public interface ApplicationEventing {
    void publishEvent(ScheduleAddedEvent scheduleAddedEvent);
}
