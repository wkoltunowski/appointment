package com.example.appointment;

import com.example.appointment.domain.schedule.DoctorId;

public interface DoctorFinder {
    String findDoctorName(DoctorId doctorId);

    DoctorId findDoctorId(String doctorName);
}
