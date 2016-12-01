package com.falco.appointment.scheduling.application;

import com.falco.appointment.scheduling.api.ScheduleId;
import com.falco.appointment.scheduling.domain.ApplicationEventing;
import com.falco.appointment.scheduling.api.SearchTags;
import com.falco.appointment.scheduling.domain.schedule.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class DefineNewScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventing applicationEventing;

    @Autowired
    public DefineNewScheduleService(ScheduleRepository scheduleRepository, ApplicationEventing applicationEventing) {
        this.scheduleRepository = scheduleRepository;
        this.applicationEventing = applicationEventing;
    }


    public ScheduleId addDailySchedule(Schedule schedule) {
        scheduleRepository.save(schedule);
        ScheduleId scheduleId = schedule.scheduleId();
        applicationEventing.publishEvent(new ScheduleAddedEvent(scheduleId));
        return scheduleId;
    }

    public ScheduleId addDailySchedule(WorkingHours workingHours, Duration duration, SearchTags searchTags) {
        return this.addDailySchedule(new DailySchedule(workingHours, Validity.infinite(), Optional.of(duration), searchTags));

    }

    public ScheduleId addDailySchedule(WorkingHours workingHours, Duration duration) {
        return addDailySchedule(workingHours, duration, Validity.infinite());
    }

    public ScheduleId addDailySchedule(WorkingHours workingHours, Duration duration, Validity validity) {
        return addDailySchedule(new DailySchedule(workingHours, validity, Optional.of(duration), SearchTags.empty()));
    }


}
