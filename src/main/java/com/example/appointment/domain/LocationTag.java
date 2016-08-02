package com.example.appointment.domain;

import com.example.appointment.scheduling.domain.TagValue;

public class LocationTag {
    public static TagValue of(LocationId value) {
        return new TagValue("LOCATION", value.asString());
    }
}
