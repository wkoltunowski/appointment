package com.example.appointment.tmp;

import java.time.LocalDate;

public class ScheduleDefinition {
    private String locationName;
    private String doctorName;
    private String serviceName;
    private String workingHours;
    private String duration;
    private LocalDate validTo;

    public ScheduleDefinition(ScheduleDefinition scheduleDefinition) {
        this.locationName = scheduleDefinition.locationName;
        this.doctorName = scheduleDefinition.doctorName;
        this.serviceName = scheduleDefinition.serviceName;
        this.workingHours = scheduleDefinition.workingHours;
        this.duration = scheduleDefinition.duration;
        this.validTo = scheduleDefinition.validTo;
    }

    public ScheduleDefinition() {

    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDuration() {
        return duration;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public ScheduleDefinition forDoctor(String doctorName) {
        ScheduleDefinition scheduleDefinition = new ScheduleDefinition(this);
        scheduleDefinition.doctorName = doctorName;
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

    public ScheduleDefinition forService(String serviceName) {
        ScheduleDefinition scheduleDefinition = new ScheduleDefinition(this);
        scheduleDefinition.serviceName = serviceName;
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
}
