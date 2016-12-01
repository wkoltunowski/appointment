package com.falco.appointment.visitreservation.domain;

import com.falco.appointment.scheduling.api.TagValue;

public class DoctorTag {
    public static TagValue doctorIs(DoctorId value) {
        return new TagValue(key(), value.asString());
    }

    public static String key() {
        return "DOCTOR";
    }
}
