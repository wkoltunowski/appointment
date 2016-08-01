package com.example.appointment.domain.freescheduleranges;

import com.example.appointment.domain.LocationId;
import com.example.appointment.domain.ServiceId;
import com.example.appointment.domain.DoctorId;
import com.example.appointment.domain.SearchTags;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Maps.newHashMap;

public class SearchCriteria {

    private Map<String, String> map;
    private final LocalDateTime startingFrom;

    public SearchCriteria(LocalDateTime startingFrom) {
        this(startingFrom, Collections.emptyMap());
    }

    public SearchCriteria(LocalDateTime startingFrom, Map<String, String> map) {
        this.startingFrom = startingFrom;
        this.map = newHashMap(map);
    }

    public SearchCriteria forService(ServiceId service) {
        return addTag("SERVICE", service.asString());
    }

    private Optional<String> getService() {
        return get("SERVICE");
    }

    private SearchCriteria addTag(String key, String value) {
        SearchCriteria searchTags = new SearchCriteria(startingFrom, map);
        searchTags.map.put(key, value);
        return searchTags;
    }

    private Optional<String> get(String key) {
        return Optional.ofNullable(map.get(key));
    }


    public SearchCriteria forDoctor(final DoctorId doctor) {
        return addTag("DOCTOR", doctor.asString());
    }

    private Optional<String> getDoctor() {
        return get("DOCTOR");
    }


    public LocalDateTime getStartingFrom() {
        return startingFrom;
    }

    public SearchCriteria forLocation(final LocationId location) {
        return addTag("LOCATION", location.asString());
    }

    private Optional<String> getLocation() {
        return get("LOCATION");
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
