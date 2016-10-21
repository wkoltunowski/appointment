package com.falco.appointment.visitreservation.infrastructure;

import com.falco.appointment.visitreservation.domain.DoctorId;

public class Doctor {

    private String doctorId;
    private String lastName;
    private String firstName;

    public static Doctor of(DoctorId doctorId, String lastName, String firstName) {
        Doctor doctor = new Doctor();
        doctor.doctorId = doctorId.asString();
        doctor.lastName = lastName;
        doctor.firstName = firstName;
        return doctor;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
