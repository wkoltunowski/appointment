package com.example.appointment;

import com.example.appointment.application.DefineNewScheduleService;
import com.example.appointment.application.FindFreeAppointmentsService;
import com.example.appointment.domain.freescheduleranges.SearchFreeSlotsCriteria;
import com.example.appointment.domain.schedule.DoctorId;
import com.example.appointment.domain.schedule.LocationId;
import com.example.appointment.domain.ServiceId;
import com.example.appointment.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.domain.freescheduleranges.FreeScheduleRanges;
import com.example.appointment.domain.schedule.ScheduleConnections;
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

    private FindFreeAppointmentsService freeSlots;
    private DefineNewScheduleService defineNewScheduleService;


    @BeforeMethod
    public void setUp() throws Exception {
        Application app = new Application();
        freeSlots = app.findFreeSlots(1);
        defineNewScheduleService = app.defineDoctorSchedule();
    }

    @Test
    public void shouldFindAnySlot() throws Exception {
        ScheduleId smithSchedule = givenSchedule(ofHours("08:00-15:00"), newSchedule(drSmithJohn()));

        assertFoundAppointments(startingFrom(tommorrowAt(8, 0)), ScheduleRange.scheduleRange(tommorrow("08:00-08:15"), smithSchedule));
    }

    @Test
    public void shouldFindSlotByDoctor() throws Exception {
        DoctorId howardMichael = drHowardMichael();
        ScheduleId howardSchedule = givenSchedule(ofHours("08:00-15:00"), newSchedule(howardMichael));
        ScheduleId smithSchedule = givenSchedule(ofHours("08:00-15:00"), newSchedule(drSmithJohn()));

        assertFoundAppointments(startingFrom(tommorrowAt(8, 0)).forDoctor(howardMichael), ScheduleRange.scheduleRange(tommorrow("08:00-08:15"), howardSchedule));
    }

    @Test
    public void shouldFindSlotByService() throws Exception {

        DoctorId doctorId = drSmithJohn();
        givenSchedule(ofHours("08:00-15:00"), newSchedule(doctorId).withService(surgery()));

        ServiceId consultation = consultation();
        ScheduleId howardSchedule = givenSchedule(ofHours("08:00-15:00"), newSchedule(drHowardMichael()).withService(consultation));

        assertFoundAppointments(
                startingFrom(tommorrowAt(8, 0)).forService(consultation),
                ScheduleRange.scheduleRange(tommorrow("08:00-08:15"), howardSchedule));
    }

    @Test
    public void shouldFindSlotByLocation() throws Exception {
        LocationId warsaw = warsaw();
        ScheduleId smithSchedule = givenSchedule(ofHours("08:00-15:00"), newSchedule(drSmithJohn()).withLocation(warsaw));
        ScheduleId howardSchedule = givenSchedule(ofHours("08:00-15:00"), newSchedule(drHowardMichael()).withLocation(lublin()));

        assertFoundAppointments(
                startingFrom(tommorrowAt(8, 0)).forLocation(warsaw.toString()),
                ScheduleRange.scheduleRange(tommorrow("08:00-08:15"), smithSchedule));
    }

    private ScheduleConnections newSchedule(DoctorId doctorId) {
        return ScheduleConnections.empty().withDuration(Duration.ofMinutes(15)).withDoctorId(doctorId);
    }

    private ScheduleId givenSchedule(WorkingHours workingHours, ScheduleConnections scheduleDefinition) {
        return defineNewScheduleService.addSchedule(workingHours, scheduleDefinition);

    }

    private void assertFoundAppointments(SearchFreeSlotsCriteria crit, ScheduleRange scheduleRange) {
        assertThat(freeSlots.findFirstFree(crit), is(FreeScheduleRanges.of(scheduleRange)));
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
