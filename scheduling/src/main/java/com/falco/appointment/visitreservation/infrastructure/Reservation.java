package com.falco.appointment.visitreservation.infrastructure;

public class Reservation {
    private String serviceId;
    private String patientId;
    private String date;
    private String doctorId;
    private String scheduleId;
    private String duration;
    private String state;
    private String reservationId;

    public Reservation(Reservation reservation) {
        this.serviceId = reservation.serviceId;
        this.patientId = reservation.patientId;
        this.date = reservation.date;
        this.doctorId = reservation.doctorId;
        this.scheduleId = reservation.scheduleId;
        this.duration = reservation.duration;
        this.state = reservation.state;
        this.reservationId = reservation.reservationId;
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

    public Reservation withState(String state) {
        Reservation reservation = new Reservation(this);
        reservation.state = state;
        return reservation;
    }

    public Reservation withId(String id) {
        Reservation reservation = new Reservation(this);
        reservation.reservationId = id;
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

    public String getState() {
        return state;
    }

    public String getReservationId() {
        return reservationId;
    }
}
