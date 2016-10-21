package com.falco.appointment.scheduling.domain;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagValue tagValue = (TagValue) o;

        if (!key.equals(tagValue.key)) return false;
        return value != null ? value.equals(tagValue.value) : tagValue.value == null;

    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Tag{%s=>%s}", key, value);
    }
}
