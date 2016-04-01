package com.example.appointment;

import java.util.Optional;

public class Doctor {
    private final SearchTags tags;

    public Doctor(SearchTags tags) {
        this.tags = tags;

    }

    public String fullName() {
        return tags.getDoctor();
    }

    public static Doctor of(SearchTags tags) {
        return new Doctor(tags);
    }

    public String service() {
        return tags.getService();
    }

    public boolean satisfies(Optional<String> requestedDoc, Optional<String> requestedService, Optional<String> requestedLocation) {
        return
                tags.getService().equals(requestedService.orElse(tags.getService()))
                        && tags.getLocation().equals(requestedLocation.orElse(tags.getLocation()))
                        && tags.getDoctor().equals(requestedDoc.orElse(tags.getDoctor()))
                ;
    }
}
