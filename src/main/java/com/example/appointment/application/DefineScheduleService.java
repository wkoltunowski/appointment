package com.example.appointment.application;

import com.example.appointment.ApplicationEventing;
import com.example.appointment.LocationId;
import com.example.appointment.ServiceId;
import com.example.appointment.domain.schedule.*;

import java.time.Duration;
import java.util.Optional;

public class DefineScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventing applicationEventing;

    public DefineScheduleService(ScheduleRepository scheduleRepository, ApplicationEventing applicationEventing) {
        this.scheduleRepository = scheduleRepository;
        this.applicationEventing = applicationEventing;
    }

    public ScheduleId addSchedule(ScheduleDefinition scheduleDefinition) {
        Schedule schedule = new Schedule(scheduleDefinition.getWorkingHours());
        schedule.updateDuration(scheduleDefinition.getDuration());
        schedule.updateDoctorId(scheduleDefinition.getDoctorId());
        Optional<ServiceId> serviceIdOptional = scheduleDefinition.getServiceIdOptional();
        if (serviceIdOptional.isPresent()) {
            schedule.updateService(serviceIdOptional.get());
        }
        Optional<LocationId> locationIdOptional = scheduleDefinition.getLocationIdOptional();
        if (locationIdOptional.isPresent()) {
            schedule.updateLocation(locationIdOptional.get());
        }
        scheduleRepository.save(schedule);
        ScheduleId scheduleId = schedule.scheduleId();
        applicationEventing.publishEvent(new ScheduleAddedEvent(scheduleId));
        return scheduleId;

    }

    public ScheduleId addSchedule(WorkingHours workingHours, Duration duration) {
        return addSchedule(workingHours, duration, Validity.infinite());
    }

    public ScheduleId addSchedule(WorkingHours workingHours, Duration duration, Validity validity) {
        Schedule schedule = new Schedule(workingHours, validity);
        schedule.updateDuration(duration);
        schedule.updateValidity(validity);
        scheduleRepository.save(schedule);
        ScheduleId scheduleId = schedule.scheduleId();
        applicationEventing.publishEvent(new ScheduleAddedEvent(scheduleId));
        return scheduleId;
    }


}
