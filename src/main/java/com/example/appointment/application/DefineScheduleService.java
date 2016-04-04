package com.example.appointment.application;

import com.example.appointment.ApplicationEventing;
import com.example.appointment.DoctorId;
import com.example.appointment.LocationId;
import com.example.appointment.ServiceId;
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
        return addSchedule(workingHours, duration, Validity.infinite());
    }

    public ScheduleId addSchedule(Validity validity, Duration duration, WorkingHours workingHours) {
        return addSchedule(workingHours, duration, validity);
    }

    public ScheduleId addSchedule(WorkingHours workingHours, Duration duration, DoctorId doctorId) {
        ScheduleId scheduleId = ScheduleId.newId();
        Schedule schedule = new Schedule(scheduleId);
        schedule.updateWorkingHours(workingHours);
        schedule.updateDuration(duration);
        schedule.updateDoctorId(doctorId);
        scheduleRepository.save(schedule);
        applicationEventing.publishEvent(new ScheduleAddedEvent(scheduleId));
        return scheduleId;

    }

    private ScheduleId addSchedule(WorkingHours workingHours, Duration duration, Validity validity) {
        ScheduleId scheduleId = ScheduleId.newId();
        Schedule schedule = new Schedule(scheduleId);
        schedule.updateWorkingHours(workingHours);
        schedule.updateDuration(duration);
        schedule.updateValidity(validity);
        scheduleRepository.save(schedule);
        applicationEventing.publishEvent(new ScheduleAddedEvent(scheduleId));
        return scheduleId;
    }

    public void addService(ScheduleId scheduleId, ServiceId service) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        schedule.addService(service);
        scheduleRepository.save(schedule);

        applicationEventing.publishEvent(new ServiceAddedEvent(scheduleId, service));
    }

    public void addLocation(ScheduleId scheduleId, LocationId location) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        schedule.addLocation(location);
        scheduleRepository.save(schedule);

        applicationEventing.publishEvent(new LocationAddedEvent(scheduleId, location));
    }
}
