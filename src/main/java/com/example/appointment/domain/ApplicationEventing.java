package com.example.appointment.domain;

import com.example.appointment.domain.schedule.ScheduleAddedEvent;

public interface ApplicationEventing {
    void publishEvent(ScheduleAddedEvent scheduleAddedEvent);
}
