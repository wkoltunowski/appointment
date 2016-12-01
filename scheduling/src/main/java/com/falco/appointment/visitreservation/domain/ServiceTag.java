package com.falco.appointment.visitreservation.domain;

import com.falco.appointment.scheduling.api.TagValue;

public class ServiceTag {
    public static TagValue serviceIs(ServiceId value) {
        return new TagValue("SERVICE", value.asString());
    }

    public static String key() {
        return "SERVICE";
    }
}
