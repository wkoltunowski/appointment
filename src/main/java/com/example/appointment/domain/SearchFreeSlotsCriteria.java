package com.example.appointment.domain;

import com.example.appointment.DoctorId;
import com.example.appointment.ServiceId;
import com.example.appointment.domain.freeslot.SearchTags;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Maps.newHashMap;

public class SearchFreeSlotsCriteria {

    private Map<String, String> map;
    private final LocalDateTime startingFrom;

    public SearchFreeSlotsCriteria(LocalDateTime startingFrom) {
        this(startingFrom, Collections.emptyMap());
    }

    public SearchFreeSlotsCriteria(LocalDateTime startingFrom, Map<String, String> map) {
        this.startingFrom = startingFrom;
        this.map = newHashMap(map);
    }

    public SearchFreeSlotsCriteria forService(ServiceId service) {
        return addTag(":SERVICE", service.toString());
    }

    public Optional<String> getService() {
        return get(":SERVICE");
    }

    private SearchFreeSlotsCriteria addTag(String key, String value) {
        SearchFreeSlotsCriteria searchTags = new SearchFreeSlotsCriteria(startingFrom, map);
        searchTags.map.put(key, value);
        return searchTags;
    }

    private Optional<String> get(String key) {
        return Optional.ofNullable(map.get(key));
    }


    public SearchFreeSlotsCriteria forDoctor(final DoctorId doctor) {
        return addTag(":DOCTOR", doctor.toString());
    }

    public Optional<String> getDoctor() {
        return get(":DOCTOR");
    }


    public LocalDateTime getStartingFrom() {
        return startingFrom;
    }

    public SearchFreeSlotsCriteria forLocation(String location) {
        return addTag(":LOCATION", location);
    }

    public Optional<String> getLocation() {
        return get(":LOCATION");
    }

    public SearchTags searchTags() {
        SearchTags searchTags = SearchTags.empty();

        Optional<String> requestedDoc = this.getDoctor();
        if (requestedDoc.isPresent()) {
            searchTags = searchTags.forDoctor(requestedDoc.get());
        }

        Optional<String> requestedService = this.getService();
        if (requestedService.isPresent()) {
            searchTags = searchTags.forService(requestedService.get());
        }
        Optional<String> requestedLocation = this.getLocation();
        if (requestedLocation.isPresent()) {
            searchTags = searchTags.forLocation(requestedLocation.get());
        }
        return searchTags;
    }
}
