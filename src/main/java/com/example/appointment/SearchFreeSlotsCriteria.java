package com.example.appointment;

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

    public SearchFreeSlotsCriteria forService(String service) {
        return addTag(":SERVICE", service);
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


    public SearchFreeSlotsCriteria forDoctor(final String doctor) {
        return addTag(":DOCTOR", doctor);
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
}
