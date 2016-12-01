package com.falco.appointment.visitreservation.domain;

import com.falco.appointment.scheduling.api.TagValue;

public class LocationTag {
    public static TagValue locationIs(LocationId value) {
        return new TagValue("LOCATION", value.asString());
    }
}
