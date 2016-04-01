package com.example.appointment;

import com.example.appointment.application.DefineScheduleService;
import com.example.appointment.domain.ScheduleHours;
import com.example.appointment.domain.ScheduleId;
import com.google.common.collect.Sets;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Maps.newHashMap;

public class DoctorScheduleDefinitionService {

    private Map<ScheduleId, Doctor> doctors = newHashMap();
    private final DefineScheduleService defineScheduleService;

    public DoctorScheduleDefinitionService(DefineScheduleService defineScheduleService) {
        this.defineScheduleService = defineScheduleService;
    }

    public void addDoctorSchedule(Duration duration, SearchTags tags, ScheduleHours scheduleHours) {
        ScheduleId scheduleId = defineScheduleService.addSchedule(scheduleHours, duration);
        doctors.put(scheduleId, Doctor.of(tags));
    }


    public Doctor findDoctor(ScheduleId scheduleId) {
        return doctors.get(scheduleId);
    }


    public Collection<ScheduleId> findDoctor(Optional<String> requestedDoc, Optional<String> requestedService, Optional<String> requestedLocation) {
        Doctor first = doctors.values().stream()
                .filter(doc -> doc.satisfies(requestedDoc, requestedService, requestedLocation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No Doctor eligible for doctor:'%s', service:'%s'", requestedDoc, requestedService)));
        return Sets.newHashSet(doctors.entrySet().stream().filter(es -> es.getValue().equals(first)).findFirst().get().getKey());

    }
}
