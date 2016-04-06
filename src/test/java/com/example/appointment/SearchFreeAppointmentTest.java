package com.example.appointment;

import com.example.appointment.application.DefineScheduleService;
import com.example.appointment.application.FindFreeSlotsService;
import com.example.appointment.application.ScheduleDefinition;
import com.example.appointment.domain.SearchFreeSlotsCriteria;
import com.example.appointment.domain.freeslot.Appointment;
import com.example.appointment.domain.freeslot.Appointments;
import com.example.appointment.domain.schedule.ScheduleId;
import com.example.appointment.domain.schedule.WorkingHours;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.example.appointment.DateTestUtils.tommorrow;
import static com.example.appointment.DateTestUtils.tommorrowAt;
import static com.example.appointment.domain.schedule.WorkingHours.ofHours;
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
        ScheduleId smithSchedule = givenSchedule(newSchedule(ofHours("08:00-15:00"), drSmithJohn()));

        assertFoundAppointments(startingFrom(tommorrowAt(8, 0)), Appointment.appointmentFor(tommorrow("08:00-08:15"), smithSchedule));
    }

    @Test
    public void shouldFindSlotByDoctor() throws Exception {
        DoctorId howardMichael = drHowardMichael();
        ScheduleId howardSchedule = givenSchedule(newSchedule(ofHours("08:00-15:00"), howardMichael));
        ScheduleId smithSchedule = givenSchedule(newSchedule(ofHours("08:00-15:00"), drSmithJohn()));

        assertFoundAppointments(startingFrom(tommorrowAt(8, 0)).forDoctor(howardMichael), Appointment.appointmentFor(tommorrow("08:00-08:15"), howardSchedule));
    }

    @Test
    public void shouldFindSlotByService() throws Exception {

        DoctorId doctorId = drSmithJohn();
        givenSchedule(newSchedule(ofHours("08:00-15:00"), doctorId).withService(surgery()));

        ServiceId consultation = consultation();
        ScheduleId howardSchedule = givenSchedule(newSchedule(ofHours("08:00-15:00"), drHowardMichael()).withService(consultation));

        assertFoundAppointments(
                startingFrom(tommorrowAt(8, 0)).forService(consultation),
                Appointment.appointmentFor(tommorrow("08:00-08:15"), howardSchedule));
    }

    @Test
    public void shouldFindSlotByLocation() throws Exception {
        WorkingHours workingHours = ofHours("08:00-15:00");
        LocationId warsaw = warsaw();
        ScheduleId smithSchedule = givenSchedule(newSchedule(workingHours, drSmithJohn()).withLocation(warsaw));
        ScheduleId howardSchedule = givenSchedule(newSchedule(ofHours("08:00-15:00"), drHowardMichael()).withLocation(lublin()));

        assertFoundAppointments(
                startingFrom(tommorrowAt(8, 0)).forLocation(warsaw.toString()),
                Appointment.appointmentFor(tommorrow("08:00-08:15"), smithSchedule));
    }

    private ScheduleDefinition newSchedule(WorkingHours workingHours, DoctorId doctorId) {
        return new ScheduleDefinition(doctorId, workingHours, Duration.ofMinutes(15));
    }

    private ScheduleId givenSchedule(ScheduleDefinition scheduleDefinition) {
        return defineScheduleService.addSchedule(scheduleDefinition);

    }

    private void assertFoundAppointments(SearchFreeSlotsCriteria crit, Appointment appointment) {
        assertThat(freeSlots.findFreeSlots(crit), is(Appointments.of(appointment)));
    }

    private DoctorId drSmithJohn() {
        return addDoctor("dr. Smith, John");
    }

    private DoctorId drHowardMichael() {
        return addDoctor("dr. Howard, Michael");
    }


    private SearchFreeSlotsCriteria startingFrom(LocalDateTime startingAt) {
        return new SearchFreeSlotsCriteria(startingAt);
    }

    private LocationId lublin() {
        return LocationId.newId();
    }

    private LocationId warsaw() {
        return LocationId.newId();
    }

    private ServiceId consultation() {
        return ServiceId.newId();
    }

    private ServiceId surgery() {
        return ServiceId.newId();
    }

    private DoctorId addDoctor(String description) {
        return DoctorId.newId();
    }


}
