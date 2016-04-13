package com.example.appointment.domain.freescheduleranges;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Maps.newHashMap;

public class SearchTags {
    private static final SearchTags EMPTY = new SearchTags();
    private Map<String, String> map;

    public SearchTags() {
        this(Collections.emptyMap());
    }

    public SearchTags(Map<String, String> map) {
        this.map = newHashMap(map);
    }

    private SearchTags addTag(String key, String value) {
        SearchTags searchTags = new SearchTags(map);
        searchTags.map.put(key, value);
        return searchTags;
    }

    public SearchTags forService(String service) {
        return addTag(":SERVICE", service);
    }

    public SearchTags forLocation(String location) {
        return addTag(":LOCATION", location);
    }

    public SearchTags forDoctor(String doctor) {
        return addTag(":DOCTOR", doctor);
    }


    private String get(String key) {
        return Optional.ofNullable(map.get(key)).orElse("");
    }

    public boolean matches(SearchTags other) {
        boolean result = true;
        for (String otherKey : other.map.keySet()) {
            String otherValue = other.get(otherKey);
            String thisValue = get(otherKey);
            result = result && otherValue.equals(thisValue);
        }

        return result;
    }

    public static SearchTags empty() {
        return EMPTY;
    }
}
