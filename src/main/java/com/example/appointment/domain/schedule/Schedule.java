package com.example.appointment.domain.schedule;

import com.example.appointment.DoctorId;
import com.example.appointment.LocationId;
import com.example.appointment.ServiceId;
import com.example.appointment.domain.DaysDomain;
import com.example.appointment.domain.freeslot.FreeSlot;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Range;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class Schedule {

    private final ScheduleId scheduleId;
    private final WorkingHours workingHours;

    private Duration appointmentDuration;

    private Validity validity = Validity.infinite();

    private Optional<DoctorId> doctorId;
    private Optional<LocationId> locationId;
    private Optional<ServiceId> serviceId;

    public Schedule(WorkingHours workingHours, Validity validity) {
        this.scheduleId = ScheduleId.newId();
        this.locationId = Optional.empty();
        this.serviceId = Optional.empty();
        this.validity = validity;
        this.workingHours = workingHours;
    }

    public Schedule(WorkingHours workingHours) {
        this(workingHours, Validity.infinite());
    }

    public ScheduleId scheduleId() {
        return scheduleId;
    }

    public void updateValidity(Validity validity) {
        this.validity = validity;
    }

    public void updateDoctorId(DoctorId doctorId) {
        this.doctorId = Optional.of(doctorId);
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
        SearchTags searchTags = SearchTags.empty();

        if (doctorId.isPresent()) {
            searchTags = searchTags.forDoctor(doctorId.get().toString());
        }
        if (locationId.isPresent()) {
            searchTags = searchTags.forLocation(locationId.get().toString());
        }
        if (serviceId.isPresent()) {
            searchTags = searchTags.forService(serviceId.get().toString());
        }
        return searchTags;
    }

    public Duration duration() {
        return appointmentDuration;
    }

    public void updateService(ServiceId service) {
        this.serviceId = Optional.of(service);
    }

    public void updateLocation(LocationId location) {
        this.locationId = Optional.of(location);
    }

    public static Schedule of(WorkingHours workingHours) {
        return new Schedule(workingHours, Validity.infinite());
    }


}
