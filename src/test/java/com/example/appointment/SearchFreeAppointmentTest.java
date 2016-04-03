package com.example.appointment;

import com.example.appointment.application.DefineScheduleService;
import com.example.appointment.application.FindFreeSlotsService;
import com.example.appointment.domain.SearchFreeSlotsCriteria;
import com.example.appointment.domain.freeslot.Appointment;
import com.example.appointment.domain.freeslot.Appointments;
import com.example.appointment.domain.schedule.ScheduleHours;
import com.example.appointment.domain.schedule.ScheduleId;
import com.example.appointment.domain.schedule.SearchTags;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.example.appointment.DateTestUtils.tommorrow;
import static com.example.appointment.DateTestUtils.tommorrowAt;
import static com.example.appointment.domain.schedule.ScheduleHours.ofHours;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SearchFreeAppointmentTest {

    private FindFreeSlotsService freeSlots;
    private DefineScheduleService defineScheduleService;


    @BeforeMethod
    public void setUp() throws Exception {
        Application app = new Application();
        freeSlots = app.findFreeSlots(1);
        defineScheduleService = app.defineDoctorSchedule();
    }

    @Test
    public void shouldFindAnySlot() throws Exception {
        ScheduleId smithSchedule = addSchedule(ofHours("08:00-15:00"), schedule().forService("consultation").forDoctor("dr. Smith, John"));
        assertThat(
                freeSlots.findFreeSlots(startingFrom(tommorrowAt(8, 0))),
                is(Appointments.of(Appointment.appointmentFor(tommorrow("08:00-08:15"), smithSchedule))
                ));
    }


    @Test
    public void shouldFindSlotByDoctor() throws Exception {
        ScheduleId howardSchedule = addSchedule(ofHours("08:00-15:00"), schedule().forService("consultation").forDoctor("dr. Howard, Michael"));
        ScheduleId smithSchedule = addSchedule(ofHours("08:00-15:00"), schedule().forService("consultation").forDoctor("dr. Smith, John"));
        assertThat(
                freeSlots.findFreeSlots(startingFrom(tommorrowAt(8, 0)).forDoctor("dr. Howard, Michael")),
                is(Appointments.of(Appointment.appointmentFor(tommorrow("08:00-08:15"), howardSchedule))));
    }

    @Test
    public void shouldFindSlotByService() throws Exception {
        ScheduleId smithSchedule = addSchedule(ofHours("08:00-15:00"), schedule().forService("surgery").forDoctor("dr. Smith, John"));
        ScheduleId howardSchedule = addSchedule(ofHours("08:00-15:00"), schedule().forService("consultation").forDoctor("dr. Howard, Michael"));

        assertThat(freeSlots.findFreeSlots(startingFrom(tommorrowAt(8, 0)).forService("consultation")),
                is(Appointments.of(Appointment.appointmentFor(tommorrow("08:00-08:15"), howardSchedule))));
    }

    @Test
    public void shouldFindSlotByLocation() throws Exception {
        ScheduleId smithSchedule = addSchedule(ofHours("08:00-15:00"), schedule().forService("consultation").forLocation("Warsaw").forDoctor("dr. Smith, John"));
        ScheduleId howardSchedule = addSchedule(ofHours("08:00-15:00"), schedule().forService("consultation").forLocation("Lublin").forDoctor("dr. Howard, Michael"));
        assertThat(
                freeSlots.findFreeSlots(startingFrom(tommorrowAt(8, 0)).forService("consultation").forLocation("Warsaw")),
                is(Appointments.of(Appointment.appointmentFor(tommorrow("08:00-08:15"), smithSchedule))));
    }

    private SearchFreeSlotsCriteria startingFrom(LocalDateTime startingAt) {
        return new SearchFreeSlotsCriteria(startingAt);
    }

    private SearchTags schedule() {
        return new SearchTags();
    }


    private ScheduleId addSchedule(ScheduleHours scheduleHours, SearchTags searchTags) {
        return defineScheduleService.addSchedule(scheduleHours, Duration.ofMinutes(15), searchTags);
    }
}
