package com.falco.appointment.scheduling;

import com.falco.appointment.Factory;
import com.falco.appointment.scheduling.application.AppointmentTakenException;
import com.falco.appointment.scheduling.application.DefineNewScheduleService;
import com.falco.appointment.scheduling.application.FindFreeRangesService;
import com.falco.appointment.scheduling.application.ReserveScheduleRangeService;
import com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.falco.appointment.scheduling.domain.schedule.ScheduleId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.falco.testsupport.DateTestUtils.todayAt;
import static com.falco.testsupport.DateTestUtils.todayBetween;
import static com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleRange.scheduleRange;
import static com.falco.appointment.scheduling.domain.schedule.Validity.validFromTo;
import static com.falco.appointment.scheduling.domain.schedule.WorkingHours.ofHours;
import static java.time.Duration.ofMinutes;
import static java.time.LocalDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class ReserveScheduleRangeServiceTest {

    private FindFreeRangesService findFreeSlots;
    private ReserveScheduleRangeService reserveScheduleRangeService;
    private DefineNewScheduleService defineNewScheduleService;

    @Test
    public void shouldFindAppointmentWhenFirstReserved() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));
        reserveScheduleRangeService.reserve(findFirstFree(todayAt("08:00")));
        assertThat(findFirstFree(todayAt("08:00")), is(scheduleRange(todayBetween("08:15-08:30"), scheduleId)));
    }

    @Test
    public void shouldFindEmptyForFullSchedule() throws Exception {
        defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15), validFromTo(now(), now()));

        reserveScheduleRangeService.reserve(findFirstFree(todayAt("08:00")));
        reserveScheduleRangeService.reserve(findFirstFree(todayAt("08:15")));

        assertThat(findFree(todayAt(8, 0)), hasSize(0));
    }

    @Test(expectedExceptions = AppointmentTakenException.class)
    public void shouldNotReserveSameAppointmentTwice() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));

        ScheduleRange visitAt8am = findFirstFree(todayAt("08:00"));
        assertThat(visitAt8am, is(scheduleRange(todayBetween("08:00-08:15"), scheduleId)));

        reserveScheduleRangeService.reserve(visitAt8am);
        reserveScheduleRangeService.reserve(visitAt8am);
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
        reserveScheduleRangeService = factory.reservationService();
    }


}
