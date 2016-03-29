package com.example.appointment;

import com.example.appointment.domain.*;
import com.example.appointment.domain.freeslots.FindFreeSlotsService;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.example.appointment.domain.Appointment.appointmentFor;
import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.assertEquals;

public class ReserveAppointmentTest {

    private FindFreeSlotsService findFreeSlots;

    @Test
    public void shouldFindFirst10Appointments() throws Exception {
        Duration fifteenMinutes = Duration.ofMinutes(15);
        ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(16, 0), fifteenMinutes);
        assertFoundAppointments(today(8, 0),
                appointmentFor(today("08:00-08:15"), scheduleId),
                appointmentFor(today("08:15-08:30"), scheduleId),
                appointmentFor(today("08:30-08:45"), scheduleId),
                appointmentFor(today("08:45-09:00"), scheduleId),
                appointmentFor(today("09:00-09:15"), scheduleId),
                appointmentFor(today("09:15-09:30"), scheduleId),
                appointmentFor(today("09:30-09:45"), scheduleId),
                appointmentFor(today("09:45-10:00"), scheduleId),
                appointmentFor(today("10:00-10:15"), scheduleId),
                appointmentFor(today("10:15-10:30"), scheduleId)
        );
    }

    @Test
    public void shouldFindAppointmentForSecondSlot() throws Exception {
        findFreeSlots = new FindFreeSlotsService(1);
        ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(8, 30), Duration.ofMinutes(15));

        assertFoundAppointments(today(8, 1), appointmentFor(today("08:15-08:30"), scheduleId));
        assertFoundAppointments(today(8, 10), appointmentFor(today("08:15-08:30"), scheduleId));
        assertFoundAppointments(today(8, 15), appointmentFor(today("08:15-08:30"), scheduleId));
    }

    @Test
    public void shouldFindAppointmentWhenInMiddleRequested() throws Exception {
        findFreeSlots = new FindFreeSlotsService(1);
        ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(8, 30), Duration.ofMinutes(15));
        assertFoundAppointments(today(8, 10), appointmentFor(today("08:15-08:30"), scheduleId));
    }

    @Test
    public void shouldFindAppointmentOverNight() throws Exception {
        findFreeSlots = new FindFreeSlotsService(2);
        ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(23, 30), LocalTime.of(0, 30), Duration.ofMinutes(30));

        assertFoundAppointments(today(23, 0),
                appointmentFor(today("23:30-00:00"), scheduleId),
                appointmentFor(tommorrow("00:00-00:30"), scheduleId));
    }

    @Test
    public void shouldFindNextDay() throws Exception {
        findFreeSlots = new FindFreeSlotsService(1);
        ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(8, 20), Duration.ofMinutes(15));

        assertFoundAppointments(today(8, 20), appointmentFor(tommorrow("08:00-08:15"), scheduleId));
    }

    @Test
    public void shouldFindAppointmentWhenFirstReserved() throws Exception {
        findFreeSlots = new FindFreeSlotsService(1);
        ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(8, 30), Duration.ofMinutes(15));
        findFreeSlots.reserve(appointmentFor(today("08:00-08:15"), scheduleId));

        assertFoundAppointments(today(8, 0), appointmentFor(today("08:15-08:30"), scheduleId));
    }

    @Test
    public void shouldFindEmptyForFullSchedule() throws Exception {
        ScheduleId scheduleId = findFreeSlots.givenSchedule(
                LocalTime.of(8, 0), LocalTime.of(8, 30),
                Validity.fromTo(LocalDate.now(), LocalDate.now()),
                Duration.ofMinutes(15));

        findFreeSlots.reserve(appointmentFor(today(8, 0), today(8, 15), scheduleId));
        findFreeSlots.reserve(appointmentFor(today(8, 15), today(8, 30), scheduleId));

        assertFoundAppointments(today(8, 0));
    }

    @Test
    public void shouldFindEmptyForNoSchedules() throws Exception {
        assertFoundAppointments(today(8, 0));
    }

    @Test(expectedExceptions = AppointmentTakenException.class)
    public void shouldNotReserveSameAppointmentTwice() throws Exception {
        ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(8, 30), Duration.ofMinutes(15));
        findFreeSlots.reserve(appointmentFor(today(8, 0), today(8, 15), scheduleId));
        findFreeSlots.reserve(appointmentFor(today(8, 0), today(8, 15), scheduleId));
    }

    @Test
    public void shouldFindOrderedAppointmentsForTwoSchedules() throws Exception {
        findFreeSlots = new FindFreeSlotsService(3);
        ScheduleId schedule1 = findFreeSlots.givenSchedule(LocalTime.of(15, 0), LocalTime.of(16, 0), Duration.ofMinutes(10));
        ScheduleId schedule2 = findFreeSlots.givenSchedule(LocalTime.of(15, 5), LocalTime.of(16, 0), Duration.ofMinutes(10));

        assertFoundAppointments(today(15, 40),
                appointmentFor(today("15:40-15:50"), schedule1),
                appointmentFor(today("15:45-15:55"), schedule2),
                appointmentFor(today("15:50-16:00"), schedule1));
    }

    @Test
    public void shouldFindAppointmentForManySchedules() throws Exception {
        findFreeSlots = new FindFreeSlotsService(1);
        for (int i = 0; i < 100; i++) {
            findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(16, 0), Duration.ofMinutes(15));
        }
        System.out.println("free slots count : " + findFreeSlots.freeSlotsCount());
        Stopwatch stopwatch = new Stopwatch().start();
        FreeAppointments freeAppointments = findFreeSlots.findFirstFree(today(8, 0));
        int count = 0;
        while (!freeAppointments.getAppointments().isEmpty()) {
            findFreeSlots.reserveFirst(freeAppointments);
            freeAppointments = findFreeSlots.findFirstFree(today(8, 0));
            count++;
        }
        stopwatch.stop();
        System.out.println("elapsed:" + stopwatch.elapsedMillis());
        System.out.println("free slots count : " + findFreeSlots.freeSlotsCount());
        System.out.println("reservations count : " + count + " in " + stopwatch.elapsedMillis() + " ms.");
        System.out.println("res/s : " + (1000 * count / stopwatch.elapsedMillis()));

    }

    @BeforeMethod
    public void setUp() throws Exception {
        findFreeSlots = new FindFreeSlotsService(10);
    }

    private void assertFoundAppointments(LocalDateTime searchDate, Appointment... expectedAppointments) {
        assertEquals(FreeAppointments.of(asList(expectedAppointments)), findFreeSlots.findFirstFree(searchDate));
    }

    private Range<LocalDateTime> today(String timeRangeStr) {
        return toRange(LocalDate.now(), timeRangeStr);
    }

    private Range<LocalDateTime> tommorrow(String rangeStr) {
        return toRange(LocalDate.now().plusDays(1), rangeStr);
    }

    private Range<LocalDateTime> toRange(LocalDate date, String rangeStr) {
        String[] split = rangeStr.split("-");
        LocalTime startTime = parseTime(split[0]);
        LocalTime endTime = parseTime(split[1]);
        int daysToAdd = startTime.isAfter(endTime) ? 1 : 0;
        return Ranges.closedOpen(date.atTime(startTime), date.plusDays(daysToAdd).atTime(endTime));
    }

    private LocalTime parseTime(String from) {
        return LocalTime.parse(from);
    }

    private LocalDateTime tommorrowAt(int hour, int minute) {
        return today(hour, minute).plusDays(1);
    }

    private LocalDateTime today(int hour, int minute) {
        return LocalDate.now().atTime(LocalTime.of(hour, minute));
    }

}
