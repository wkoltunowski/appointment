package com.example.appointment.application;

import com.example.appointment.ApplicationEventing;
import com.example.appointment.domain.schedule.*;

import java.time.Duration;

public class DefineNewScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventing applicationEventing;

    public DefineNewScheduleService(ScheduleRepository scheduleRepository, ApplicationEventing applicationEventing) {
        this.scheduleRepository = scheduleRepository;
        this.applicationEventing = applicationEventing;
    }

    public ScheduleId addSchedule(WorkingHours workingHours, ScheduleConnections scheduleDefinition) {
        Schedule schedule = new Schedule(workingHours, scheduleDefinition);
        scheduleRepository.save(schedule);
        ScheduleId scheduleId = schedule.scheduleId();
        applicationEventing.publishEvent(new ScheduleAddedEvent(scheduleId));
        return scheduleId;

    }

    public ScheduleId addSchedule(WorkingHours workingHours, Duration duration) {
        return addSchedule(workingHours, duration, Validity.infinite());
    }

    public ScheduleId addSchedule(WorkingHours workingHours, Duration duration, Validity validity) {
        Schedule schedule = new Schedule(workingHours, validity, ScheduleConnections.empty().withDuration(duration));
        scheduleRepository.save(schedule);
        ScheduleId scheduleId = schedule.scheduleId();
        applicationEventing.publishEvent(new ScheduleAddedEvent(scheduleId));
        return scheduleId;
    }


}
