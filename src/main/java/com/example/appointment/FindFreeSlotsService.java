package com.example.appointment;

import com.example.appointment.application.FindFreeAppointmentsService;
import com.example.appointment.domain.Appointment;
import com.example.appointment.domain.FreeAppointments;
import com.example.appointment.domain.ScheduleId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.appointment.Slot.slotFor;

public class FindFreeSlotsService {
    private final FindFreeAppointmentsService freeService;
    private DoctorScheduleDefinitionService defineDoctorSchedule;

    public FindFreeSlotsService(DoctorScheduleDefinitionService defineDoctorSchedule, FindFreeAppointmentsService freeService) {
        this.defineDoctorSchedule = defineDoctorSchedule;
        this.freeService = freeService;
    }

    public List<Slot> findFreeSlots(SearchFreeSlotsCriteria criteria) {
        FreeAppointments firstFree = this.freeService.findFirstFree(criteria.getStartingFrom(), findScheduleIds(criteria));

        return firstFree.getAppointments().stream().map(this::toSlot).collect(Collectors.toList());
    }

    private Collection<ScheduleId> findScheduleIds(SearchFreeSlotsCriteria searchFreeSlotsCriteria) {
        Optional<String> requestedDoc = searchFreeSlotsCriteria.getDoctor();
        Optional<String> requestedService = searchFreeSlotsCriteria.getService();
        Optional<String> requestedLocation = searchFreeSlotsCriteria.getLocation();
        return defineDoctorSchedule.findDoctor(requestedDoc, requestedService, requestedLocation);
    }

    private Slot toSlot(Appointment appointment) {
        ScheduleId scheduleId = appointment.scheduleId();
        Doctor doctor = defineDoctorSchedule.findDoctor(scheduleId);
        return slotFor(appointment.range(), doctor.fullName(), doctor.service());
    }
}
