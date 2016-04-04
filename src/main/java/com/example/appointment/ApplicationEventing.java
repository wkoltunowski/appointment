package com.example.appointment;

import com.example.appointment.application.ScheduleAddedEvent;

public interface ApplicationEventing {
    void publishEvent(ScheduleAddedEvent scheduleAddedEvent);
}
