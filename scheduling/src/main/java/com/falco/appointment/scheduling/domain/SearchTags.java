package com.falco.appointment.scheduling.domain;

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

    public SearchTags withTagAdded(TagValue tagValue) {
        SearchTags searchTags = new SearchTags(map);
        searchTags.map.put(tagValue.getKey(), tagValue.getValue());
        return searchTags;
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(map.get(key));
    }

    private String getOrEmpty(String key) {
        Optional<String> get = get(key);
        return get.orElse("");
    }

    public boolean matches(SearchTags other) {
        boolean result = true;
        for (String otherKey : other.map.keySet()) {
            String otherValue = other.getOrEmpty(otherKey);
            String thisValue = getOrEmpty(otherKey);
            result = result && otherValue.equals(thisValue);
        }
        return result;
    }

    public static SearchTags empty() {
        return EMPTY;
    }

    public static SearchTags ofTags(TagValue... tags) {
        return EMPTY.withTags(tags);
    }

    @Override
    public String toString() {
        return "SearchTags{" +
                "map=" + map +
                '}';
    }

    public SearchTags withTags(TagValue... tags) {
        SearchTags result = this;
        for (TagValue tag : tags) {
            result = result.withTagAdded(tag);
        }
        return result;
    }
}
