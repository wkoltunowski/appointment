package com.example.appointment.visitreservation.domain;

import com.example.appointment.scheduling.domain.TagValue;

public class DoctorTag {
    public static TagValue doctorIs(DoctorId value) {
        return new TagValue("DOCTOR", value.asString());
    }
}
