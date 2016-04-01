package com.example.appointment;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Maps.newHashMap;

public class SearchTags {
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

    public String getService() {
        return get(":SERVICE");
    }

    public SearchTags forLocation(String location) {
        return addTag(":LOCATION", location);
    }

    public String getLocation() {
        return get(":LOCATION");
    }

    private String get(String key) {
        return Optional.ofNullable(map.get(key)).orElse("");
    }

    public SearchTags forDoctor(String doctor) {
        return addTag(":DOCTOR", doctor);
    }

    public String getDoctor() {
        return get(":DOCTOR");
    }
}
