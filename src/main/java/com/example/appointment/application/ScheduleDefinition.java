package com.example.appointment.application;

import com.example.appointment.DoctorId;
import com.example.appointment.LocationId;
import com.example.appointment.ServiceId;
import com.example.appointment.domain.schedule.WorkingHours;

import java.time.Duration;
import java.util.Optional;

public class ScheduleDefinition {
    private final DoctorId doctorId;
    private final WorkingHours workingHours;
    private final Duration duration;
    private Optional<ServiceId> serviceIdOptional;
    private Optional<LocationId> locationIdOptional;


    public ScheduleDefinition(DoctorId doctorId, WorkingHours workingHours, Duration duration) {
        this.doctorId = doctorId;
        this.workingHours = workingHours;
        this.duration = duration;
        this.serviceIdOptional = Optional.empty();
        this.locationIdOptional = Optional.empty();
    }

    public DoctorId getDoctorId() {
        return doctorId;
    }

    public WorkingHours getWorkingHours() {
        return workingHours;
    }

    public Duration getDuration() {
        return duration;
    }

    public Optional<ServiceId> getServiceIdOptional() {
        return serviceIdOptional;
    }

    public Optional<LocationId> getLocationIdOptional() {
        return locationIdOptional;
    }

    public ScheduleDefinition withService(ServiceId service) {
        ScheduleDefinition newDef = new ScheduleDefinition(doctorId, workingHours, duration);
        newDef.serviceIdOptional = Optional.of(service);
        return newDef;
    }

    public ScheduleDefinition withLocation(LocationId locationId) {
        ScheduleDefinition newDef = new ScheduleDefinition(doctorId, workingHours, duration);
        newDef.locationIdOptional = Optional.of(locationId);
        return newDef;
    }
}
