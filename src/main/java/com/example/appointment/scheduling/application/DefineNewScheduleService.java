package com.example.appointment.scheduling.application;

import com.example.appointment.scheduling.domain.ApplicationEventing;
import com.example.appointment.scheduling.domain.SearchTags;
import com.example.appointment.scheduling.domain.schedule.*;

import java.time.Duration;
import java.util.Optional;

public class DefineNewScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventing applicationEventing;

    public DefineNewScheduleService(ScheduleRepository scheduleRepository, ApplicationEventing applicationEventing) {
        this.scheduleRepository = scheduleRepository;
        this.applicationEventing = applicationEventing;
    }


    public ScheduleId addDailySchedule(WorkingHours workingHours, Validity validity, Duration duration, SearchTags searchTags) {
        Schedule schedule = new DailySchedule(workingHours, validity, Optional.of(duration), searchTags);
        scheduleRepository.save(schedule);
        ScheduleId scheduleId = schedule.scheduleId();
        applicationEventing.publishEvent(new ScheduleAddedEvent(scheduleId));
        return scheduleId;
    }

    public ScheduleId addDailySchedule(WorkingHours workingHours, Duration duration, SearchTags searchTags) {
        return this.addDailySchedule(workingHours, Validity.infinite(), duration, searchTags);

    }

    public ScheduleId addDailySchedule(WorkingHours workingHours, Duration duration) {
        return addDailySchedule(workingHours, duration, Validity.infinite());
    }

    public ScheduleId addDailySchedule(WorkingHours workingHours, Duration duration, Validity validity) {
        return addDailySchedule(workingHours, validity, duration, SearchTags.empty());
    }


}
