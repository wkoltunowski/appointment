package com.example.appointment.domain.schedule;

import java.time.Duration;
import java.util.Optional;

public class ScheduleConnections {
    private Optional<Duration> duration;
    private Optional<ServiceId> serviceId;
    private Optional<DoctorId> doctorId;
    private Optional<LocationId> locationId;

    private ScheduleConnections() {
        duration = Optional.empty();
        serviceId = Optional.empty();
        doctorId = Optional.empty();
        locationId = Optional.empty();
    }

    private ScheduleConnections(ScheduleConnections origin) {
        this.duration = origin.duration;
        this.serviceId = origin.serviceId;
        this.locationId = origin.locationId;
        this.doctorId = origin.doctorId;
    }

    public Optional<DoctorId> doctorId() {
        return doctorId;
    }

    public Optional<Duration> duration() {
        return duration;
    }

    public Optional<ServiceId> serviceId() {
        return serviceId;
    }

    public Optional<LocationId> locationId() {
        return locationId;
    }

    public ScheduleConnections withService(ServiceId service) {
        ScheduleConnections newDef = new ScheduleConnections(this);
        newDef.serviceId = Optional.of(service);
        return newDef;
    }

    public ScheduleConnections withLocation(LocationId locationId) {
        ScheduleConnections newDef = new ScheduleConnections(this);
        newDef.locationId = Optional.of(locationId);
        return newDef;
    }

    public ScheduleConnections withDoctorId(DoctorId doctorId) {
        ScheduleConnections newDef = new ScheduleConnections(this);
        newDef.doctorId = Optional.of(doctorId);
        return newDef;
    }

    public ScheduleConnections withDuration(Duration duration) {
        ScheduleConnections newDef = new ScheduleConnections(this);
        newDef.duration = Optional.of(duration);
        return newDef;
    }

    public static ScheduleConnections empty() {
        return new ScheduleConnections();
    }
}
