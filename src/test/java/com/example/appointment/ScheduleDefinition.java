package com.example.appointment;

import com.example.appointment.visitreservation.domain.*;
import com.example.appointment.scheduling.domain.SearchTags;
import com.example.appointment.scheduling.domain.TagValue;
import com.example.appointment.scheduling.domain.schedule.*;

import java.time.Duration;
import java.time.LocalDate;

public class ScheduleDefinition {

    private String workingHours;
    private String duration;
    private LocalDate validTo;
    private SearchTags searchTags = SearchTags.empty();

    public ScheduleDefinition(ScheduleDefinition scheduleDefinition) {
        this.workingHours = scheduleDefinition.workingHours;
        this.duration = scheduleDefinition.duration;
        this.validTo = scheduleDefinition.validTo;
        this.searchTags = scheduleDefinition.searchTags;
    }

    public ScheduleDefinition() {
    }

    public ScheduleDefinition forDoctor(DoctorId doctor) {
        return forService(DoctorTag.of(doctor));
    }

    public ScheduleDefinition atLocation(LocationId location) {
        return withTag(LocationTag.of(location));
    }

    public ScheduleDefinition forWorkingHours(String workingHours) {
        ScheduleDefinition scheduleDefinition = new ScheduleDefinition(this);
        scheduleDefinition.workingHours = workingHours;
        return scheduleDefinition;
    }

    public ScheduleDefinition forService(TagValue tagValue) {
        return withTag(tagValue);
    }

    public ScheduleDefinition withTag(TagValue tagValue) {
        ScheduleDefinition scheduleDefinition = new ScheduleDefinition(this);
        scheduleDefinition.searchTags = searchTags.withTagAdded(tagValue);
        return scheduleDefinition;
    }

    public ScheduleDefinition withDefaultDuration(String duration) {

        ScheduleDefinition scheduleDefinition = new ScheduleDefinition(this);
        scheduleDefinition.duration = duration;
        return scheduleDefinition;
    }

    public ScheduleDefinition validTill(LocalDate endDate) {

        ScheduleDefinition scheduleDefinition = new ScheduleDefinition(this);
        scheduleDefinition.validTo = endDate;
        return scheduleDefinition;
    }

    public Validity validity() {
        Validity validity = Validity.infinite();
        if (validTo != null) {
            validity = Validity.validTill(validTo);
        }
        return validity;
    }

    public WorkingHours workingHours() {
        return WorkingHours.ofHours(workingHours);
    }

    public Duration duration() {
        return Duration.parse(this.duration);
    }

    public SearchTags searchTags() {
        return searchTags;
    }
}
