package com.falco.appointment.scheduling.domain.schedule;

import com.falco.appointment.scheduling.api.ScheduleId;

public interface ScheduleRepository {
    Schedule findById(ScheduleId scheduleId);

    void save(Schedule schedule);
}
