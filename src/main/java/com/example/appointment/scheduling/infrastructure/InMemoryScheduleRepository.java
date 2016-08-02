package com.example.appointment.scheduling.infrastructure;

import com.example.appointment.scheduling.domain.schedule.Schedule;
import com.example.appointment.scheduling.domain.schedule.ScheduleId;
import com.example.appointment.scheduling.domain.schedule.ScheduleRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
@Component
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
