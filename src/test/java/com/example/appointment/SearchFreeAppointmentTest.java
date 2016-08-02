package com.example.appointment;

import com.example.appointment.scheduling.domain.TagValue;
import com.example.appointment.visitreservation.domain.*;
import com.example.appointment.scheduling.application.DefineNewScheduleService;
import com.example.appointment.scheduling.application.FindFreeScheduleRangesService;
import com.example.appointment.scheduling.domain.SearchTags;
import com.example.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.scheduling.domain.freescheduleranges.SearchCriteria;
import com.example.appointment.scheduling.domain.schedule.ScheduleId;
import com.example.appointment.scheduling.domain.schedule.WorkingHours;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.example.appointment.DateTestUtils.tommorrow;
import static com.example.appointment.DateTestUtils.tommorrowAt;
import static com.example.appointment.scheduling.domain.schedule.WorkingHours.ofHours;
import static com.example.appointment.visitreservation.domain.DoctorTag.doctorIs;
import static com.example.appointment.visitreservation.domain.LocationTag.locationIs;
import static com.example.appointment.visitreservation.domain.ServiceTag.serviceIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class SearchFreeAppointmentTest {

    private FindFreeScheduleRangesService freeSlots;
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

        assertFoundAppointments(startingFrom(tommorrowAt(8, 0)).withTagValue(doctorIs(howardMichael)), ScheduleRange.scheduleRange(tommorrow("08:00-08:15"), howardSchedule));
    }

    @Test
    public void shouldFindSlotByService() throws Exception {

        DoctorId doctorId = drSmithJohn();
        givenSchedule(ofHours("08:00-15:00"), newSchedule(doctorId).withTagAdded(serviceIs(surgery())));

        TagValue consultation = serviceIs(consultation());
        ScheduleId howardSchedule = givenSchedule(ofHours("08:00-15:00"), newSchedule(drHowardMichael()).withTagAdded(consultation));

        assertFoundAppointments(
                startingFrom(tommorrowAt(8, 0)).withTag(consultation),
                ScheduleRange.scheduleRange(tommorrow("08:00-08:15"), howardSchedule));
    }

    @Test
    public void shouldFindSlotByLocation() throws Exception {

        TagValue warsaw = locationIs(warsaw());
        ScheduleId smithSchedule = givenSchedule(ofHours("08:00-15:00"), newSchedule(drSmithJohn()).withTagAdded(warsaw));

        ScheduleId howardSchedule = givenSchedule(ofHours("08:00-15:00"), newSchedule(drHowardMichael()).withTagAdded(locationIs(lublin())));

        assertFoundAppointments(
                startingFrom(tommorrowAt(8, 0)).withTagValue(warsaw),
                ScheduleRange.scheduleRange(tommorrow("08:00-08:15"), smithSchedule));
    }

    private SearchTags newSchedule(DoctorId doctorId) {
        return SearchTags.empty().withTagAdded(doctorIs(doctorId));
    }

    private ScheduleId givenSchedule(WorkingHours workingHours, SearchTags searchTags) {
        return defineNewScheduleService.addDailySchedule(workingHours, Duration.ofMinutes(15), searchTags);

    }

    private void assertFoundAppointments(SearchCriteria crit, ScheduleRange scheduleRange) {
        assertThat(freeSlots.findFirstFree(crit), contains(scheduleRange));
    }

    private DoctorId drSmithJohn() {
        return addDoctor("dr. Smith, John");
    }

    private DoctorId drHowardMichael() {
        return addDoctor("dr. Howard, Michael");
    }


    private SearchCriteria startingFrom(LocalDateTime startingAt) {
        return new SearchCriteria(startingAt);
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
