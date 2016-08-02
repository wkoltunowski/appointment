package com.example.appointment.visitreservation.domain;

import com.example.appointment.scheduling.domain.TagValue;

public class LocationTag {
    public static TagValue locationIs(LocationId value) {
        return new TagValue("LOCATION", value.asString());
    }
}
