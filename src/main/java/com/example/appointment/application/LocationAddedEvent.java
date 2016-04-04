package com.example.appointment.application;

import com.example.appointment.LocationId;
import com.example.appointment.domain.schedule.ScheduleId;

public class LocationAddedEvent {
    private final LocationId locationId;
    private final ScheduleId scheduleId;

    public LocationAddedEvent(ScheduleId scheduleId, LocationId location) {
        this.scheduleId = scheduleId;
        this.locationId = location;
    }

    public LocationId locationId() {
        return locationId;
    }

    public ScheduleId scheduleId() {
        return scheduleId;
    }
}
