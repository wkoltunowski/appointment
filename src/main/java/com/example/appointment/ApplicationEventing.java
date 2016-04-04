package com.example.appointment;

import com.example.appointment.application.LocationAddedEvent;
import com.example.appointment.application.ScheduleAddedEvent;
import com.example.appointment.application.ServiceAddedEvent;

public interface ApplicationEventing {
    void publishEvent(ScheduleAddedEvent scheduleAddedEvent);

    void publishEvent(ServiceAddedEvent serviceAddedEvent);

    void publishEvent(LocationAddedEvent locationAddedEvent);
}
