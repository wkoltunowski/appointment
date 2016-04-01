package com.example.appointment.domain.schedule;

public interface ScheduleRepository {
    Schedule findById(ScheduleId scheduleId);

    void save(Schedule schedule);
}
