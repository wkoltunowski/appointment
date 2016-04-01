package com.example.appointment;

import com.example.appointment.application.FindFreeAppointmentsService;
import com.example.appointment.application.Slot;
import com.example.appointment.domain.schedule.ScheduleRepository;
import com.example.appointment.domain.appointment.Appointment;
import com.example.appointment.domain.appointment.FreeAppointments;
import com.example.appointment.domain.schedule.Schedule;
import com.example.appointment.domain.schedule.ScheduleId;
import com.example.appointment.domain.schedule.SearchTags;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.appointment.application.Slot.slotFor;

public class FindFreeSlotsService {
    private final FindFreeAppointmentsService freeService;
    private final ScheduleRepository scheduleRepository;

    public FindFreeSlotsService(ScheduleRepository scheduleRepository, FindFreeAppointmentsService freeService) {
        this.scheduleRepository = scheduleRepository;
        this.freeService = freeService;
    }

    public List<Slot> findFreeSlots(SearchFreeSlotsCriteria criteria) {
        FreeAppointments firstFree = this.freeService.findFirstFree(criteria.getStartingFrom(), buildSearchTags(criteria));
        return firstFree.getAppointments().stream().map(this::toSlot).collect(Collectors.toList());
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


    private Slot toSlot(Appointment appointment) {
        ScheduleId scheduleId = appointment.scheduleId();

        Schedule schedule = scheduleRepository.findById(scheduleId);
        return slotFor(appointment.range(), schedule.searchTags().getDoctor(), schedule.searchTags().getService());
    }
}
