package com.example.appointment.application;

import com.example.appointment.ApplicationEventing;
import com.example.appointment.domain.schedule.*;

import java.time.Duration;

public class DefineScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventing applicationEventing;

    public DefineScheduleService(ScheduleRepository scheduleRepository, ApplicationEventing applicationEventing) {
        this.scheduleRepository = scheduleRepository;
        this.applicationEventing = applicationEventing;
    }

    public ScheduleId addSchedule(WorkingHours workingHours, Duration duration) {
        return addSchedule(workingHours, duration, SearchTags.empty(), Validity.infinite());
    }

    public ScheduleId addSchedule(Validity validity, Duration duration, WorkingHours workingHours) {
        return addSchedule(workingHours, duration, SearchTags.empty(), validity);
    }

    public ScheduleId addSchedule(WorkingHours workingHours, Duration duration, SearchTags tags) {
        return addSchedule(workingHours, duration, tags, Validity.infinite());
    }

    private ScheduleId addSchedule(WorkingHours workingHours, Duration duration, SearchTags tags, Validity validity) {
        ScheduleId scheduleId = ScheduleId.newId();
        scheduleRepository.save(new Schedule(scheduleId, workingHours, validity, tags, duration));
        applicationEventing.publishEvent(new ScheduleAddedEvent(scheduleId));
        return scheduleId;
    }

}
