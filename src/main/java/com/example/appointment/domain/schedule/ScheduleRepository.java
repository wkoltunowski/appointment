package com.example.appointment.domain.schedule;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class ScheduleRepository {
    private Map<ScheduleId, Schedule> schedules = newHashMap();

    public Schedule findById(ScheduleId scheduleId) {
        return schedules.get(scheduleId);
    }

    public void save(Schedule schedule) {
        this.schedules.put(schedule.id(), schedule);
    }
}
