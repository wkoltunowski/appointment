package com.example.appointment;

import com.example.appointment.scheduling.application.AppointmentTakenException;
import com.example.appointment.scheduling.application.DefineNewScheduleService;
import com.example.appointment.scheduling.application.FindFreeRangesService;
import com.example.appointment.scheduling.application.ReserveScheduleRangeService;
import com.example.appointment.scheduling.domain.schedule.ScheduleId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.example.appointment.DateTestUtils.todayAt;
import static com.example.appointment.DateTestUtils.todayBetween;
import static com.example.appointment.scheduling.domain.freescheduleranges.ScheduleRange.scheduleRange;
import static com.example.appointment.scheduling.domain.schedule.Validity.validFromTo;
import static com.example.appointment.scheduling.domain.schedule.WorkingHours.ofHours;
import static java.time.Duration.ofMinutes;
import static java.time.LocalDate.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.testng.AssertJUnit.assertEquals;

public class ReserveAppointmentTest {

    private FindFreeRangesService findFreeSlots;
    private ReserveScheduleRangeService reserveScheduleRangeService;
    private DefineNewScheduleService defineNewScheduleService;
    private Factory factory;



    @Test
    public void shouldFindAppointmentWhenFirstReserved() throws Exception {
        buildSearchServiceForMaxResults(1);
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));
        reserveScheduleRangeService.reserve(scheduleRange(todayBetween("08:00-08:15"), scheduleId));

        assertThat(findFreeSlots.findFirstFree(todayAt(8, 0)), contains(scheduleRange(todayBetween("08:15-08:30"), scheduleId)));
    }

    @Test
    public void shouldFindEmptyForFullSchedule() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(
                ofHours("08:00-08:30"), ofMinutes(15), validFromTo(now(), now())
        );

        reserveScheduleRangeService.reserve(scheduleRange(todayBetween("08:00-08:15"), scheduleId));
        reserveScheduleRangeService.reserve(scheduleRange(todayBetween("08:15-08:30"), scheduleId));

        assertThat(findFreeSlots.findFirstFree(todayAt(8, 0)), hasSize(0));
    }

    @Test(expectedExceptions = AppointmentTakenException.class)
    public void shouldNotReserveSameAppointmentTwice() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));
        reserveScheduleRangeService.reserve(scheduleRange(todayBetween("08:00-08:15"), scheduleId));
        reserveScheduleRangeService.reserve(scheduleRange(todayBetween("08:00-08:15"), scheduleId));
    }

    @BeforeMethod
    public void setUp() throws Exception {
        factory = new Factory();
        buildSearchServiceForMaxResults(10);
    }


    private void buildSearchServiceForMaxResults(int firstFreeCount) {
        findFreeSlots = factory.findFreeService(firstFreeCount);
        defineNewScheduleService = factory.scheduleDefinitionService();
        reserveScheduleRangeService = factory.reservationService();
    }


}
