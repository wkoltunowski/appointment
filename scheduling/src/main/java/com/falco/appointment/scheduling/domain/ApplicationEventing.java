package com.falco.appointment.scheduling.domain;

import com.falco.appointment.scheduling.domain.schedule.ScheduleAddedEvent;

public interface ApplicationEventing {
    void publishEvent(ScheduleAddedEvent scheduleAddedEvent);
}
