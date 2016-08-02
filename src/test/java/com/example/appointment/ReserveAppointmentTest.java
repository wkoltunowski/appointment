package com.example.appointment;

import com.example.appointment.scheduling.application.DefineNewScheduleService;
import com.example.appointment.scheduling.application.FindFreeScheduleRangesService;
import com.example.appointment.scheduling.application.ReserveScheduleRangeService;
import com.example.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.scheduling.domain.schedule.ScheduleId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.example.appointment.DateTestUtils.todayBetween;
import static com.example.appointment.DateTestUtils.tommorrow;
import static com.example.appointment.scheduling.domain.schedule.Validity.validFromTo;
import static com.example.appointment.scheduling.domain.schedule.WorkingHours.ofHours;
import static java.time.Duration.ofMinutes;
import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.assertEquals;

public class ReserveAppointmentTest {

    private FindFreeScheduleRangesService findFreeSlots;
    private ReserveScheduleRangeService reserveScheduleRangeService;
    private DefineNewScheduleService defineNewScheduleService;
    private Factory factory;

    @Test
    public void shouldFindFirst10Appointments() throws Exception {
        Duration fifteenMinutes = ofMinutes(15);
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-16:00"), fifteenMinutes);
        assertFoundAppointments(DateTestUtils.todayAt(8, 0),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("08:00-08:15"), scheduleId),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("08:15-08:30"), scheduleId),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("08:30-08:45"), scheduleId),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("08:45-09:00"), scheduleId),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("09:00-09:15"), scheduleId),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("09:15-09:30"), scheduleId),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("09:30-09:45"), scheduleId),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("09:45-10:00"), scheduleId),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("10:00-10:15"), scheduleId),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("10:15-10:30"), scheduleId)
        );
    }

    @Test
    public void shouldFindAppointmentForSecondSlot() throws Exception {
        buildSearchServiceForMaxResults(1);
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));

        assertFoundAppointments(DateTestUtils.todayAt(8, 1), ScheduleRange.scheduleRange(DateTestUtils.todayBetween("08:15-08:30"), scheduleId));
        assertFoundAppointments(DateTestUtils.todayAt(8, 10), ScheduleRange.scheduleRange(DateTestUtils.todayBetween("08:15-08:30"), scheduleId));
        assertFoundAppointments(DateTestUtils.todayAt(8, 15), ScheduleRange.scheduleRange(DateTestUtils.todayBetween("08:15-08:30"), scheduleId));
    }

    @Test
    public void shouldFindAppointmentWhenInMiddleRequested() throws Exception {
        buildSearchServiceForMaxResults(1);
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));
        assertFoundAppointments(DateTestUtils.todayAt(8, 10), ScheduleRange.scheduleRange(DateTestUtils.todayBetween("08:15-08:30"), scheduleId));
    }

    @Test
    public void shouldFindAppointmentOverNight() throws Exception {
        buildSearchServiceForMaxResults(2);
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("23:30-00:30"), ofMinutes(30));

        assertFoundAppointments(DateTestUtils.todayAt(23, 0),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("23:30-00:00"), scheduleId),
                ScheduleRange.scheduleRange(tommorrow("00:00-00:30"), scheduleId));
    }

    @Test
    public void shouldFindNextDay() throws Exception {
        buildSearchServiceForMaxResults(1);
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:20"), ofMinutes(15));

        assertFoundAppointments(DateTestUtils.todayAt(8, 20), ScheduleRange.scheduleRange(tommorrow("08:00-08:15"), scheduleId));
    }

    @Test
    public void shouldFindAppointmentWhenFirstReserved() throws Exception {
        buildSearchServiceForMaxResults(1);
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));
        reserveScheduleRangeService.reserve(ScheduleRange.scheduleRange(DateTestUtils.todayBetween("08:00-08:15"), scheduleId));

        assertFoundAppointments(DateTestUtils.todayAt(8, 0), ScheduleRange.scheduleRange(DateTestUtils.todayBetween("08:15-08:30"), scheduleId));
    }

    @Test
    public void shouldFindEmptyForFullSchedule() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(
                ofHours("08:00-08:30"), ofMinutes(15), validFromTo(now(), now())
        );

        reserveScheduleRangeService.reserve(ScheduleRange.scheduleRange(todayBetween("08:00-08:15"), scheduleId));
        reserveScheduleRangeService.reserve(ScheduleRange.scheduleRange(todayBetween("08:15-08:30"), scheduleId));

        assertFoundAppointments(DateTestUtils.todayAt(8, 0));
    }

    @Test
    public void shouldFindEmptyForNoSchedules() throws Exception {
        assertFoundAppointments(DateTestUtils.todayAt(8, 0));
    }

    @Test(expectedExceptions = DefineNewScheduleService.AppointmentTakenException.class)
    public void shouldNotReserveSameAppointmentTwice() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));
        reserveScheduleRangeService.reserve(ScheduleRange.scheduleRange(todayBetween("08:00-08:15"), scheduleId));
        reserveScheduleRangeService.reserve(ScheduleRange.scheduleRange(todayBetween("08:00-08:15"), scheduleId));
    }

    @Test
    public void shouldFindOrderedAppointmentsForTwoSchedules() throws Exception {
        buildSearchServiceForMaxResults(3);
        ScheduleId schedule1 = defineNewScheduleService.addDailySchedule(ofHours("15:00-16:00"), ofMinutes(10));
        ScheduleId schedule2 = defineNewScheduleService.addDailySchedule(ofHours("15:05-16:00"), ofMinutes(10));

        assertFoundAppointments(DateTestUtils.todayAt(15, 40),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("15:40-15:50"), schedule1),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("15:45-15:55"), schedule2),
                ScheduleRange.scheduleRange(DateTestUtils.todayBetween("15:50-16:00"), schedule1));
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

    private void assertFoundAppointments(LocalDateTime searchDate, ScheduleRange... expectedScheduleRanges) {
        assertEquals(asList(expectedScheduleRanges), findFreeSlots.findFirstFree(searchDate));
    }


}
