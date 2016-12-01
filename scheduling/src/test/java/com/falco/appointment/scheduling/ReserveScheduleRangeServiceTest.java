package com.falco.appointment.scheduling;

import com.falco.appointment.Factory;
import com.falco.appointment.scheduling.api.*;
import com.falco.appointment.scheduling.application.AppointmentTakenException;
import com.falco.appointment.scheduling.application.DefineNewScheduleService;
import com.falco.appointment.scheduling.api.SearchTags;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlot;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlotRepository;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.falco.appointment.scheduling.api.ScheduleRange.scheduleRange;
import static com.falco.appointment.scheduling.domain.schedule.Validity.validFromTo;
import static com.falco.appointment.scheduling.domain.schedule.Validity.validOn;
import static com.falco.appointment.scheduling.domain.schedule.WorkingHours.ofHours;
import static com.falco.testsupport.DateTestUtils.todayAt;
import static com.falco.testsupport.DateTestUtils.todayBetween;
import static java.time.Duration.ofMinutes;
import static java.time.LocalDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ReserveScheduleRangeServiceTest {

    private FindFreeRangesService findFreeSlots;
    private ReservationService reservationService;
    private DefineNewScheduleService defineNewScheduleService;
    private FreeScheduleSlotRepository repository;
    private CancellationService cancellationService;

    @Test
    public void shouldFindAppointmentWhenFirstReserved() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));
        reservationService.reserve(findFirstFree(todayAt("08:00")));
        assertThat(findFirstFree(todayAt("08:00")), is(scheduleRange(todayBetween("08:15-08:30"), scheduleId)));
    }

    @Test
    public void shouldFindEmptyForFullSchedule() throws Exception {
        defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15), validFromTo(now(), now()));

        reservationService.reserve(findFirstFree(todayAt("08:00")));
        reservationService.reserve(findFirstFree(todayAt("08:15")));

        assertThat(findFree(todayAt(8, 0)), hasSize(0));
    }

    @Test(expectedExceptions = AppointmentTakenException.class)
    public void shouldNotReserveSameAppointmentTwice() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));

        reservationService.reserve(scheduleRange(todayBetween("08:00-08:15"), scheduleId));
        reservationService.reserve(scheduleRange(todayBetween("08:00-08:15"), scheduleId));
    }

    @Test(expectedExceptions = AppointmentTakenException.class)
    public void shouldNotReserveEnclosingAppointment() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:45"), ofMinutes(15));

        reservationService.reserve(scheduleRange(todayBetween("08:15-08:45"), scheduleId));
        reservationService.reserve(scheduleRange(todayBetween("08:00-08:45"), scheduleId));
    }

    @Test(expectedExceptions = AppointmentTakenException.class)
    public void shouldNotReserveOverlappingAppointment() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:45"), ofMinutes(15));

        reservationService.reserve(scheduleRange(todayBetween("08:15-08:45"), scheduleId));
        reservationService.reserve(scheduleRange(todayBetween("08:00-08:20"), scheduleId));
    }

    @Test
    public void shouldCancel() throws Exception {
        defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));

        ScheduleRange visitAt8am = findFirstFree(todayAt("08:00"));
        reservationService.reserve(visitAt8am);
        cancellationService.cancel(visitAt8am);
        assertThat(findFirstFree(todayAt("08:00")), is(visitAt8am));
    }

    @Test
    public void shouldCancelLast() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15), validOn(now()));

        reservationService.reserve(findFirstFree(todayAt("08:00")));
        reservationService.reserve(findFirstFree(todayAt("08:15")));
        assertThat(repository.findByScheduleId(scheduleId), hasSize(0));

        cancellationService.cancel(scheduleRange(todayBetween("08:00-08:15"), scheduleId));
        cancellationService.cancel(scheduleRange(todayBetween("08:15-08:30"), scheduleId));
        assertThat(repository.findByScheduleId(scheduleId), hasItem(FreeScheduleSlot.of(scheduleId, todayBetween("08:00-08:30"), SearchTags.empty())));
    }

    @Test
    public void shouldCancelMiddle() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:45"), ofMinutes(15), validOn(now()));

        reservationService.reserve(findFirstFree(todayAt("08:15")));
        assertThat(repository.findByScheduleId(scheduleId), contains(
                FreeScheduleSlot.of(scheduleId, todayBetween("08:00-08:15"), SearchTags.empty()),
                FreeScheduleSlot.of(scheduleId, todayBetween("08:30-08:45"), SearchTags.empty())));
        cancellationService.cancel(scheduleRange(todayBetween("08:15-08:30"), scheduleId));
        assertThat(repository.findByScheduleId(scheduleId), contains(FreeScheduleSlot.of(scheduleId, todayBetween("08:00-08:45"), SearchTags.empty())));
    }

    @Test
    public void shouldCancelFullSchedule() throws Exception {
        ScheduleId sId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:45"), ofMinutes(15), validOn(now()));

        reservationService.reserve(scheduleRange(todayBetween("08:00-08:15"), sId));
        reservationService.reserve(scheduleRange(todayBetween("08:15-08:30"), sId));
        reservationService.reserve(scheduleRange(todayBetween("08:30-08:45"), sId));
        assertThat(repository.findByScheduleId(sId), hasSize(0));

        cancellationService.cancel(scheduleRange(todayBetween("08:00-08:15"), sId));
        cancellationService.cancel(scheduleRange(todayBetween("08:15-08:30"), sId));
        cancellationService.cancel(scheduleRange(todayBetween("08:30-08:45"), sId));
        assertThat(repository.findByScheduleId(sId), contains(FreeScheduleSlot.of(sId, todayBetween("08:00-08:45"), SearchTags.empty())));
    }

    @Test
    public void shouldReserveCancelled() throws Exception {
        ScheduleId sId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:45"), ofMinutes(15), validOn(now()));

        reservationService.reserve(scheduleRange(todayBetween("08:00-08:15"), sId));
        cancellationService.cancel(scheduleRange(todayBetween("08:00-08:15"), sId));
        reservationService.reserve(scheduleRange(todayBetween("08:00-08:30"), sId));
    }

    private ScheduleRange findFirstFree(LocalDateTime startingFrom) {
        return findFree(startingFrom).get(0);
    }

    private List<ScheduleRange> findFree(LocalDateTime startingFrom) {
        return findFreeSlots.findFirstFree(startingFrom);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        Factory factory = new Factory();
        findFreeSlots = factory.findFreeService(10);
        defineNewScheduleService = factory.scheduleDefinitionService();
        reservationService = factory.reservationService();
        cancellationService = factory.cancellationService();
        repository = factory.freeSlotRepository();
    }


}
