package com.example.appointment;

import com.example.appointment.scheduling.application.DefineNewScheduleService;
import com.example.appointment.scheduling.application.FindFreeScheduleRangesService;
import com.example.appointment.scheduling.domain.SearchTags;
import com.example.appointment.scheduling.domain.TagValue;
import com.example.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.scheduling.domain.freescheduleranges.SearchCriteria;
import com.example.appointment.scheduling.domain.schedule.ScheduleId;
import com.example.appointment.scheduling.domain.schedule.WorkingHours;
import com.example.appointment.visitreservation.domain.DoctorId;
import com.example.appointment.visitreservation.domain.LocationId;
import com.example.appointment.visitreservation.domain.ServiceId;
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
import static org.hamcrest.Matchers.hasSize;

public class SearchFreeAppointmentTest {

    private FindFreeScheduleRangesService freeSlots;
    private DefineNewScheduleService defineNewScheduleService;
    private TagValue drHoward;
    private TagValue drSmith;
    private TagValue warsaw;
    private TagValue lublin;
    private TagValue consultation;
    private TagValue surgery;
    private Duration fifteenMinutes;


    @BeforeMethod
    public void setUp() throws Exception {
        Application app = new Application();
        freeSlots = app.findFreeSlots(1);
        defineNewScheduleService = app.defineDoctorSchedule();
        drHoward = doctorIs(DoctorId.newId());
        drSmith = doctorIs(DoctorId.newId());
        warsaw = locationIs(LocationId.newId());
        lublin = locationIs(LocationId.newId());
        consultation = serviceIs(ServiceId.newId());
        surgery = serviceIs(ServiceId.newId());
        fifteenMinutes = Duration.ofMinutes(15);
    }

    @Test
    public void shouldFindAnySlot() throws Exception {
        ScheduleId smithSchedule = givenSchedule(ofHours("08:00-15:00"), fifteenMinutes, drSmith);

        assertFoundAppointments(startingFrom(tommorrowAt(8, 0)), ScheduleRange.scheduleRange(tommorrow("08:00-08:15"), smithSchedule));
    }

    @Test
    public void shouldFindSlotByDoctor() throws Exception {
        ScheduleId howardSchedule = givenSchedule(ofHours("08:00-15:00"), fifteenMinutes, drHoward);
        ScheduleId smithSchedule = givenSchedule(ofHours("08:00-15:00"), fifteenMinutes, drSmith);

        assertFoundAppointments(startingFrom(tommorrowAt(8, 0)).withTagValue(drHoward), ScheduleRange.scheduleRange(tommorrow("08:00-08:15"), howardSchedule));
    }

    @Test
    public void shouldFindSlotByService() throws Exception {

        givenSchedule(ofHours("08:00-15:00"), fifteenMinutes, surgery, drSmith);

        ScheduleId howardSchedule = givenSchedule(ofHours("08:00-15:00"), fifteenMinutes, consultation, drHoward);

        assertFoundAppointments(
                startingFrom(tommorrowAt(8, 0)).withTag(consultation),
                ScheduleRange.scheduleRange(tommorrow("08:00-08:15"), howardSchedule));
    }

    @Test
    public void shouldFindSlotByLocation() throws Exception {

        ScheduleId smithSchedule = givenSchedule(ofHours("08:00-15:00"), fifteenMinutes, warsaw, drSmith);
        ScheduleId howardSchedule = givenSchedule(ofHours("08:00-15:00"), fifteenMinutes, lublin, drHoward);

        assertFoundAppointments(
                startingFrom(tommorrowAt(8, 0)).withTagValue(warsaw),
                ScheduleRange.scheduleRange(tommorrow("08:00-08:15"), smithSchedule));
    }

    @Test
    public void shouldNotFindTooBigAppointment() throws Exception {
        givenSchedule(ofHours("08:00-08:10"), fifteenMinutes, warsaw, drSmith);

        assertThat(freeSlots.findFirstFree(startingFrom(tommorrowAt(8, 0)).withTagValue(warsaw)), hasSize(0));
    }

    private ScheduleId givenSchedule(WorkingHours workingHours, Duration duration, TagValue... tags) {
        return defineNewScheduleService.addDailySchedule(workingHours, duration, SearchTags.ofTags(tags));
    }


    private void assertFoundAppointments(SearchCriteria crit, ScheduleRange scheduleRange) {
        assertThat(freeSlots.findFirstFree(crit), contains(scheduleRange));
    }


    private SearchCriteria startingFrom(LocalDateTime startingAt) {
        return new SearchCriteria(startingAt);
    }

}
