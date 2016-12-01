package com.falco.appointment.scheduling.infrastructure.restapi;

public class FreeRange {
    private String scheduleId;
    private String start;
    private String duration;

    private String serviceId;
    private String doctorId;


    public FreeRange(FreeRange freeRange) {
        this.scheduleId = freeRange.scheduleId;
        this.start = freeRange.start;
        this.duration = freeRange.duration;
        this.serviceId = freeRange.serviceId;
        this.doctorId = freeRange.doctorId;
    }

    public FreeRange() {

    }

    public FreeRange withScheduleId(String scheduleId) {
        FreeRange freeRange = new FreeRange(this);
        freeRange.scheduleId = scheduleId;
        return freeRange;
    }

    public FreeRange withStart(String start) {
        FreeRange freeRange = new FreeRange(this);
        freeRange.start = start;
        return freeRange;
    }

    public FreeRange withDuration(String duration) {
        FreeRange freeRange = new FreeRange(this);
        freeRange.duration = duration;
        return freeRange;
    }

    public FreeRange withServiceId(String serviceId) {
        FreeRange freeRange = new FreeRange(this);
        freeRange.serviceId = serviceId;
        return freeRange;
    }

    public FreeRange withDoctorId(String doctorId) {
        FreeRange freeRange = new FreeRange(this);
        freeRange.doctorId = doctorId;
        return freeRange;
    }

    public String getDuration() {
        return duration;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getStart() {
        return start;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getDoctorId() {
        return doctorId;
    }
}
