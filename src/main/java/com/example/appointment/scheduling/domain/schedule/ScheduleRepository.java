package com.example.appointment.scheduling.domain.schedule;

public interface ScheduleRepository {
    Schedule findById(ScheduleId scheduleId);

    void save(Schedule schedule);
}
