package com.example.appointment;

import com.example.appointment.domain.ScheduleHours;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.appointment.DateTestUtils.tommorrow;
import static com.example.appointment.DateTestUtils.tommorrowAt;
import static com.example.appointment.Slot.slotFor;
import static com.example.appointment.domain.ScheduleHours.ofHours;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class VisitReservationTest {

    private Application app;


    @BeforeMethod
    public void setUp() throws Exception {
        app = new Application();
    }

    @Test
    public void shouldFindAnySlot() throws Exception {
        addDoctorScheduleForService("dr. Smith, John", ofHours("08:00-15:00"), "consultation");
        assertThat(
                findFreeSlots(startingFrom(tommorrowAt(8, 0))),
                contains(
                        slotFor(tommorrow("08:00-08:15"), "dr. Smith, John", "consultation")
                ));
    }


    @Test
    public void shouldFindSlotByDoctor() throws Exception {
        addDoctorScheduleForService("dr. Howard, Michael", ofHours("08:00-15:00"), "consultation");
        addDoctorScheduleForService("dr. Smith, John", ofHours("08:00-15:00"), "consultation");
        assertThat(
                findFreeSlots(byDoctor(tommorrowAt(8, 0), "dr. Howard, Michael")),
                contains(slotFor(tommorrow("08:00-08:15"), "dr. Howard, Michael", "consultation")));
    }

    @Test
    public void shouldFindSlotByService() throws Exception {
        addDoctorScheduleForService("dr. Smith, John", ofHours("08:00-15:00"), "surgery");
        addDoctorScheduleForService("dr. Howard, Michael", ofHours("08:00-15:00"), "consultation");
        assertThat(
                findFreeSlots(byService(tommorrowAt(8, 0), "consultation")),
                contains(slotFor(tommorrow("08:00-08:15"), "dr. Howard, Michael", "consultation")));
    }

    @Test
    public void shouldFindSlotByLocation() throws Exception {
        defineScheduleForServiceLocation("dr. Smith, John", "consultation", "Warsaw", ofHours("08:00-15:00"));
        defineScheduleForServiceLocation("dr. Howard, Michael", "consultation", "Lublin", ofHours("08:00-15:00"));
        assertThat(
                findFreeSlots(byServiceLocation(tommorrowAt(8, 0), "consultation", "Warsaw")),
                contains(slotFor(tommorrow("08:00-08:15"), "dr. Smith, John", "consultation")));
    }

    private SearchFreeSlotsCriteria byService(LocalDateTime startingAt, String service) {
        return startingFrom(startingAt).forService(service);
    }

    private SearchFreeSlotsCriteria byDoctor(LocalDateTime startingAt, String doctor) {
        return startingFrom(startingAt).forDoctor(doctor);
    }

    private SearchFreeSlotsCriteria byServiceLocation(LocalDateTime startingAt, String service, String location) {
        return byService(startingAt, service).forLocation(location);
    }

    private SearchFreeSlotsCriteria startingFrom(LocalDateTime startingAt) {
        return new SearchFreeSlotsCriteria(startingAt);
    }

    private void addDoctorScheduleForService(String doctor, ScheduleHours scheduleHours, String surgery) {
        addSchedule(scheduleDescription().forService(surgery).forDoctor(doctor), scheduleHours);
    }

    private void defineScheduleForServiceLocation(String doctor, String service, String location, ScheduleHours scheduleHours) {
        addSchedule(scheduleDescription().forService(service).forLocation(location).forDoctor(doctor), scheduleHours);
    }

    private SearchTags scheduleDescription() {
        return new SearchTags();
    }


    private List<Slot> findFreeSlots(SearchFreeSlotsCriteria criteria) {
        return app.findFreeSlots(1).findFreeSlots(criteria);
    }


    private void addSchedule(SearchTags searchTags, ScheduleHours scheduleHours) {
        app.defineDoctorSchedule().addDoctorSchedule(Duration.ofMinutes(15), searchTags, scheduleHours);
    }
}
