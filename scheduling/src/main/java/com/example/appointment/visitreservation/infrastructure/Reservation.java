package com.example.appointment.visitreservation.infrastructure;

public class Reservation {
    private String serviceId;
    private String patientId;
    private String date;
    private String doctorId;
    private String scheduleId;
    private String duration;

    public Reservation(Reservation reservation) {
        this.serviceId = reservation.serviceId;
        this.patientId = reservation.patientId;
        this.date = reservation.date;
        this.doctorId = reservation.doctorId;
        this.scheduleId = reservation.scheduleId;
        this.duration = reservation.duration;
    }

    public Reservation() {

    }


    public Reservation withPatientId(String patientId) {
        Reservation reservation = new Reservation(this);
        reservation.patientId = patientId;
        return reservation;
    }

    public Reservation withServiceId(String serviceId) {
        Reservation reservation = new Reservation(this);
        reservation.serviceId = serviceId;
        return reservation;
    }

    public Reservation withDate(String date) {
        Reservation reservation = new Reservation(this);
        reservation.date = date;
        return reservation;
    }

    public Reservation withDoctorId(String doctorId) {
        Reservation reservation = new Reservation(this);
        reservation.doctorId = doctorId;
        return reservation;
    }

    public Reservation withScheduleId(String scheduleId) {
        Reservation reservation = new Reservation(this);
        reservation.scheduleId = scheduleId;
        return reservation;
    }

    public Reservation withDuration(String duration) {
        Reservation reservation = new Reservation(this);
        reservation.duration = duration;
        return reservation;
    }

    public String getDate() {
        return date;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getDuration() {
        return duration;
    }

    public String getScheduleId() {
        return scheduleId;
    }
}
