package com.example.appointment;

import com.example.appointment.domain.SearchTags;
import com.example.appointment.domain.DoctorId;
import com.example.appointment.domain.LocationId;
import com.example.appointment.domain.ServiceId;

import java.util.Optional;

public class ScheduleConnections {
    private Optional<ServiceId> serviceId;
    private Optional<DoctorId> doctorId;
    private Optional<LocationId> locationId;

    private ScheduleConnections() {
        serviceId = Optional.empty();
        doctorId = Optional.empty();
        locationId = Optional.empty();
    }

    private ScheduleConnections(ScheduleConnections origin) {
        this.serviceId = origin.serviceId;
        this.locationId = origin.locationId;
        this.doctorId = origin.doctorId;
    }

    public Optional<DoctorId> doctorId() {
        return doctorId;
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

    public static ScheduleConnections empty() {
        return new ScheduleConnections();
    }

    public SearchTags searchTagsFor() {
        SearchTags searchTags = SearchTags.empty();
        if (this.doctorId().isPresent()) {
            searchTags = searchTags.forDoctor(this.doctorId().get().asString());
        }
        if (this.locationId().isPresent()) {
            searchTags = searchTags.forLocation(this.locationId().get().asString());
        }
        if (this.serviceId().isPresent()) {
            searchTags = searchTags.forService(this.serviceId().get().asString());
        }
        return searchTags;
    }
}
