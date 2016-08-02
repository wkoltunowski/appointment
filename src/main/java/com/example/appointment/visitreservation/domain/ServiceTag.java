package com.example.appointment.visitreservation.domain;

import com.example.appointment.scheduling.domain.TagValue;

public class ServiceTag {
    public static TagValue of(ServiceId value) {
        return new TagValue("SERVICE", value.asString());
    }
}
