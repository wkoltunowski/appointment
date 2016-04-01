package com.example.appointment;

import com.example.appointment.application.FindFreeAppointmentsService;
import com.example.appointment.domain.Appointment;
import com.example.appointment.domain.Factory;
import com.example.appointment.domain.FreeAppointments;
import com.example.appointment.domain.ScheduleId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.appointment.DateTestUtils.tommorrow;
import static com.example.appointment.DateTestUtils.tommorrowAt;
import static com.example.appointment.Slot.slotFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class VisitReservationTest {
    private DoctorScheduleDefinitionService defineDoctorSchedule;
    private Factory factory;

    @BeforeMethod
    public void setUp() throws Exception {
        factory = new Factory();
        defineDoctorSchedule = new DoctorScheduleDefinitionService(factory.scheduleDefinitionService());
    }

    @Test
    public void shouldFindAnySlot() throws Exception {
        givenSchedule("dr. Smith, John", "08:00-15:00", tags().forService("consultation"));
        assertThat(
                findFreeSlots(startingFrom(tommorrowAt(8, 0))),
                contains(
                        slotFor(tommorrow("08:00-08:15"), "dr. Smith, John", "consultation")
                ));
    }


    @Test
    public void shouldFindSlotByDoctor() throws Exception {
        givenSchedule("dr. Howard, Michael", "08:00-15:00", tags().forService("consultation"));
        givenSchedule("dr. Smith, John", "08:00-15:00", tags().forService("consultation"));
        assertThat(
                findFreeSlots(startingFrom(tommorrowAt(8, 0)).forDoctor("dr. Howard, Michael")),
                contains(slotFor(tommorrow("08:00-08:15"), "dr. Howard, Michael", "consultation")));
    }

    @Test
    public void shouldFindSlotByService() throws Exception {
        givenSchedule("dr. Smith, John", "08:00-15:00", tags().forService("surgery"));
        givenSchedule("dr. Howard, Michael", "08:00-15:00", tags().forService("consultation"));
        assertThat(
                findFreeSlots(startingFrom(tommorrowAt(8, 0)).forService("consultation")),
                contains(slotFor(tommorrow("08:00-08:15"), "dr. Howard, Michael", "consultation")));
    }

    @Test
    public void shouldFindSlotByLocation() throws Exception {
        givenSchedule("dr. Smith, John", "08:00-15:00", tags().forService("consultation").forLocation("Warsaw"));
        givenSchedule("dr. Howard, Michael", "08:00-15:00", tags().forService("consultation").forLocation("Lublin"));
        assertThat(
                findFreeSlots(startingFrom(tommorrowAt(8, 0)).forService("consultation").forLocation("Warsaw")),
                contains(slotFor(tommorrow("08:00-08:15"), "dr. Smith, John", "consultation")));
    }

    private SearchTags tags() {
        return new SearchTags();
    }

    private SearchFreeSlotsCriteria startingFrom(LocalDateTime startingAt) {
        return new SearchFreeSlotsCriteria(startingAt);
    }


    private List<Slot> findFreeSlots(SearchFreeSlotsCriteria searchFreeSlotsCriteria) {
        FindFreeAppointmentsService freeService = factory.findFreeService(1);
        FreeAppointments firstFree = freeService.findFirstFree(searchFreeSlotsCriteria.getStartingFrom(), findScheduleIds(searchFreeSlotsCriteria));

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

    private void givenSchedule(String doctor, String openHours, SearchTags searchTags) {
        String[] split = openHours.split("-");
        LocalTime from = DateTestUtils.parseTime(split[0]);
        LocalTime to = DateTestUtils.parseTime(split[1]);
        defineDoctorSchedule.addDoctorSchedule(doctor, searchTags, from, to, Duration.ofMinutes(15));
    }
}
