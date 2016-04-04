package com.example.appointment.domain.schedule;

import com.example.appointment.DoctorId;
import com.example.appointment.LocationId;
import com.example.appointment.ServiceId;
import com.example.appointment.domain.DaysDomain;
import com.example.appointment.domain.freeslot.FreeSlot;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class Schedule {

    private final ScheduleId scheduleId;
    private WorkingHours workingHours;
    private Duration appointmentDuration;
    private Validity validity = Validity.infinite();
    private DoctorId doctorId;
    private Set<ServiceId> services = new HashSet<>();
    private Set<LocationId> locations = new HashSet<>();

    public Schedule(ScheduleId scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void updateWorkingHours(WorkingHours workingHours) {
        this.workingHours = workingHours;
    }

    public void updateDoctorId(DoctorId doctorId) {
        this.doctorId = doctorId;
    }

    public void updateValidity(Validity validity) {
        this.validity = validity;
    }

    public void updateDuration(Duration duration) {
        this.appointmentDuration = duration;
    }

    public List<FreeSlot> buildFreeSlots(Range<LocalDate> range) {
        Range<LocalDate> rangeValidityIntersection = validity
                .range()
                .intersection(range);
        return ContiguousSet
                .create(rangeValidityIntersection, DaysDomain.daysDomain())
                .stream()
                .map(this::buildFreeSlot)
                .collect(toList());
    }

    private FreeSlot buildFreeSlot(LocalDate date) {
        SearchTags searchTags = searchTags();
        return FreeSlot.of(scheduleId, workingHours.toRange(date), searchTags);
    }

    public SearchTags searchTags() {
        SearchTags searchTags = SearchTags.empty()
                .forDoctor(doctorId.toString());
        if (!locations.isEmpty()){
            searchTags = searchTags.forLocation(locations.iterator().next().toString());
        }
        if (!services.isEmpty()){
            searchTags = searchTags.forService(services.iterator().next().toString());
        }
        return searchTags;
    }


    public ScheduleId scheduleId() {
        return scheduleId;
    }

    public Duration duration() {
        return appointmentDuration;
    }

    public void addService(ServiceId service) {
        this.services.add(service);
    }

    public void addLocation(LocationId location) {
        this.locations.add(location);
    }

    public Set<ServiceId> services() {
        return ImmutableSet.copyOf(services);
    }
}
