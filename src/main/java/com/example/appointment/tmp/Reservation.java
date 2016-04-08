package com.example.appointment.tmp;

public class Reservation {
    private final ReservationId id;
    private String service;
    private String patientName;
    private String doctorName;

    public Reservation() {
        id = ReservationId.randomId();
    }

    public static Reservation of(String patientName, String doctorName, String service) {
        Reservation reservation = new Reservation();
        reservation.patientName = patientName;
        reservation.doctorName = doctorName;
        reservation.service = service;

        return reservation;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "doctorName='" + doctorName + '\'' +
                ", service='" + service + '\'' +
                ", patientName='" + patientName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reservation that = (Reservation) o;

        if (service != null ? !service.equals(that.service) : that.service != null) return false;
        if (patientName != null ? !patientName.equals(that.patientName) : that.patientName != null) return false;
        return doctorName != null ? doctorName.equals(that.doctorName) : that.doctorName == null;

    }

    @Override
    public int hashCode() {
        int result = service != null ? service.hashCode() : 0;
        result = 31 * result + (patientName != null ? patientName.hashCode() : 0);
        result = 31 * result + (doctorName != null ? doctorName.hashCode() : 0);
        return result;
    }

    public ReservationId id() {
        return id;
    }
}
