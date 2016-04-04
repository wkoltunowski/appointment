package com.example.appointment.infrastructure;

import com.example.appointment.domain.schedule.Schedule;
import com.example.appointment.domain.schedule.ScheduleId;
import com.example.appointment.domain.schedule.ScheduleRepository;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class InMemoryScheduleRepository implements ScheduleRepository {
    private Map<ScheduleId, Schedule> schedules = newHashMap();

    @Override
    public Schedule findById(ScheduleId scheduleId) {
        return schedules.get(scheduleId);
    }

    @Override
    public void save(Schedule schedule) {
        this.schedules.put(schedule.scheduleId(), schedule);
    }
}
