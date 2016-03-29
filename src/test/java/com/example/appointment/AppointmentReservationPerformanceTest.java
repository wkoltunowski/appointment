package com.example.appointment;

import com.example.appointment.domain.FreeAppointments;
import com.example.appointment.domain.freeslots.FindFreeAppointmentsService;
import com.google.common.base.Stopwatch;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalTime;

import static com.example.appointment.DateTestUtils.today;

public class AppointmentReservationPerformanceTest {
    private FindFreeAppointmentsService findFreeSlots;

    @BeforeMethod
    public void setUp() throws Exception {
        findFreeSlots = new FindFreeAppointmentsService(10);
    }

    @Test
    public void shouldFindAppointmentForManySchedules() throws Exception {
        findFreeSlots = new FindFreeAppointmentsService(1);
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
}
