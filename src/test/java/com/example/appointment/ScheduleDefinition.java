package com.example.appointment;

import com.example.appointment.domain.schedule.*;
import com.example.appointment.domain.DoctorId;
import com.example.appointment.domain.ServiceId;

import java.time.Duration;
import java.time.LocalDate;

public class ScheduleDefinition {
    private String locationName;
    private DoctorId doctor;
    private ServiceId service;
    private String workingHours;
    private String duration;
    private LocalDate validTo;

    public ScheduleDefinition(ScheduleDefinition scheduleDefinition) {
        this.locationName = scheduleDefinition.locationName;
        this.doctor = scheduleDefinition.doctor;
        this.service = scheduleDefinition.service;
        this.workingHours = scheduleDefinition.workingHours;
        this.duration = scheduleDefinition.duration;
        this.validTo = scheduleDefinition.validTo;
    }

    public ScheduleDefinition() {
    }

    public ScheduleDefinition forDoctor(DoctorId doctor) {
        ScheduleDefinition scheduleDefinition = new ScheduleDefinition(this);
        scheduleDefinition.doctor = doctor;
        return scheduleDefinition;
    }

    public ScheduleDefinition atLocation(String locationName) {
        ScheduleDefinition scheduleDefinition = new ScheduleDefinition(this);
        scheduleDefinition.locationName = locationName;
        return scheduleDefinition;
    }

    public ScheduleDefinition forWorkingHours(String workingHours) {
        ScheduleDefinition scheduleDefinition = new ScheduleDefinition(this);
        scheduleDefinition.workingHours = workingHours;
        return scheduleDefinition;
    }

    public ScheduleDefinition forService(ServiceId serviceName) {
        ScheduleDefinition scheduleDefinition = new ScheduleDefinition(this);
        scheduleDefinition.service = serviceName;
        return scheduleDefinition;
    }

    public ScheduleDefinition withDefaultDuration(String duration) {

        ScheduleDefinition scheduleDefinition = new ScheduleDefinition(this);
        scheduleDefinition.duration = duration;
        return scheduleDefinition;
    }

    public ScheduleDefinition validTill(LocalDate endDate) {

        ScheduleDefinition scheduleDefinition = new ScheduleDefinition(this);
        scheduleDefinition.validTo = endDate;
        return scheduleDefinition;
    }

    public Validity validity() {
        Validity validity = Validity.infinite();
        if (validTo != null) {
            validity = Validity.validTill(validTo);
        }
        return validity;
    }

    public WorkingHours workingHours() {
        return WorkingHours.ofHours(workingHours);
    }

    public ScheduleConnections scheduleConnections() {
        return ScheduleConnections.empty()
                .withDoctorId(doctor)
                .withService(service);
    }

    public Duration duration() {
        return Duration.parse(this.duration);
    }
}
