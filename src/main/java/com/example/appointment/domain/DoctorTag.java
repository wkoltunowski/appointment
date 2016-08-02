package com.example.appointment.domain;

import com.example.appointment.scheduling.domain.TagValue;

public class DoctorTag {
    public static TagValue of(DoctorId value) {
        return new TagValue("DOCTOR", value.asString());
    }
}
