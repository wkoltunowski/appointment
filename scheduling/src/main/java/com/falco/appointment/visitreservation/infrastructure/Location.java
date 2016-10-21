package com.falco.appointment.visitreservation.infrastructure;

import com.falco.appointment.visitreservation.domain.LocationId;

public class Location {
    private String locationId;
    private String name;

    public static Location of(LocationId id, String name) {
        Location location = new Location();
        location.locationId = id.asString();
        location.name = name;
        return location;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getName() {
        return name;
    }
}
