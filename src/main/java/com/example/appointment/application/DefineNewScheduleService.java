package com.example.appointment.application;

import com.example.appointment.domain.ApplicationEventing;
import com.example.appointment.domain.schedule.*;

import java.time.Duration;

public class DefineNewScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventing applicationEventing;

    public DefineNewScheduleService(ScheduleRepository scheduleRepository, ApplicationEventing applicationEventing) {
        this.scheduleRepository = scheduleRepository;
        this.applicationEventing = applicationEventing;
    }

    public ScheduleId addDailySchedule(WorkingHours workingHours, Validity validity, ScheduleConnections scheduleDefinition) {
        Schedule schedule = new Schedule(workingHours, validity, scheduleDefinition);
        scheduleRepository.save(schedule);
        ScheduleId scheduleId = schedule.scheduleId();
        applicationEventing.publishEvent(new ScheduleAddedEvent(scheduleId));
        return scheduleId;

    }

    public ScheduleId addDailySchedule(WorkingHours workingHours, ScheduleConnections scheduleDefinition) {
        return this.addDailySchedule(workingHours, Validity.infinite(), scheduleDefinition);

    }

    public ScheduleId addDailySchedule(WorkingHours workingHours, Duration duration) {
        return addDailySchedule(workingHours, duration, Validity.infinite());
    }

    public ScheduleId addDailySchedule(WorkingHours workingHours, Duration duration, Validity validity) {
        Schedule schedule = new Schedule(workingHours, validity, ScheduleConnections.empty().withDuration(duration));
        scheduleRepository.save(schedule);
        ScheduleId scheduleId = schedule.scheduleId();
        applicationEventing.publishEvent(new ScheduleAddedEvent(scheduleId));
        return scheduleId;
    }


}
