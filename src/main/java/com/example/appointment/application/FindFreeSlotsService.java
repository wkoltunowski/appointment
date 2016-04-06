package com.example.appointment.application;

import com.example.appointment.domain.SearchFreeSlotsCriteria;
import com.example.appointment.domain.freeslot.Appointments;
import com.example.appointment.domain.freeslot.SearchTags;

import java.util.Optional;

public class FindFreeSlotsService {
    private final FindFreeAppointmentsService freeService;

    public FindFreeSlotsService(FindFreeAppointmentsService freeService) {
        this.freeService = freeService;
    }


    public Appointments findFreeSlots(SearchFreeSlotsCriteria criteria) {
        return this.freeService.findFirstFree(criteria.getStartingFrom(), buildSearchTags(criteria));
    }

    private SearchTags buildSearchTags(SearchFreeSlotsCriteria criteria) {
        SearchTags searchTags = SearchTags.empty();

        Optional<String> requestedDoc = criteria.getDoctor();
        if (requestedDoc.isPresent()) {
            searchTags = searchTags.forDoctor(requestedDoc.get());
        }

        Optional<String> requestedService = criteria.getService();
        if (requestedService.isPresent()) {
            searchTags = searchTags.forService(requestedService.get());
        }
        Optional<String> requestedLocation = criteria.getLocation();
        if (requestedLocation.isPresent()) {
            searchTags = searchTags.forLocation(requestedLocation.get());
        }
        return searchTags;
    }


}
