package com.example.appointment.scheduling.domain;

public class TagValue {
    private final String key;
    private final String value;

    public TagValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
