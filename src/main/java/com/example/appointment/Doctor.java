package com.example.appointment;

import java.util.Optional;

public class Doctor {
    private final String fullName;
    private final SearchTags tags;

    public Doctor(String fullName, SearchTags tags) {
        this.fullName = fullName;
        this.tags = tags;

    }

    public String fullName() {
        return fullName;
    }

    public static Doctor of(String doctor, SearchTags tags) {
        return new Doctor(doctor, tags);
    }

    public String service() {
        return tags.getService();
    }

    public boolean satisfies(Optional<String> requestedDoc, Optional<String> requestedService, Optional<String> requestedLocation) {
        return
                tags.getService().equals(requestedService.orElse(tags.getService()))
                        && tags.getLocation().equals(requestedLocation.orElse(tags.getLocation()))
                        && fullName.equals(requestedDoc.orElse(fullName))
                ;
    }
}
