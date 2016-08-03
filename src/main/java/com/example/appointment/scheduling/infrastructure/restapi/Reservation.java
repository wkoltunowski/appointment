package com.example.appointment.scheduling.infrastructure.restapi;

public class Reservation {
    private String serviceId;
    private String patientId;
    private String date;
    private String doctorId;

    public Reservation(Reservation reservation) {
        this.serviceId = reservation.serviceId;
        this.patientId = reservation.patientId;
        this.date = reservation.date;
        this.doctorId = reservation.doctorId;
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
}
