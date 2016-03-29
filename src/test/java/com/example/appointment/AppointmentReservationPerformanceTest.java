package com.example.appointment;

import com.example.appointment.application.FindFreeAppointmentsService;
import com.example.appointment.application.ReserveAppointmentService;
import com.example.appointment.application.DefineScheduleService;
import com.example.appointment.domain.*;
import com.google.common.base.Stopwatch;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import static com.example.appointment.DateTestUtils.today;

public class AppointmentReservationPerformanceTest {
    private FindFreeAppointmentsService findFreeSlots;
    private DefineScheduleService defineScheduleService;
    private ReserveAppointmentService reserveAppointmentService;
    private FreeSlotsStorage storage;

    @BeforeMethod
    public void setUp() throws Exception {
        Factory factory = new Factory();
        findFreeSlots = factory.findFreeService(1);
        defineScheduleService = factory.scheduleDefinitionService();
        reserveAppointmentService = factory.reservationService();
        storage = factory.storage();
    }

    @Test
    public void shouldFindAppointmentForManySchedules() throws Exception {
        for (int i = 0; i < 100; i++) {
            defineScheduleService.givenSchedule(LocalTime.of(8, 0), LocalTime.of(16, 0), Duration.ofMinutes(15));
        }
        System.out.println("free slots count : " + storage.size());
        Stopwatch stopwatch = Stopwatch.createStarted();
        FreeAppointments freeAppointments = findFreeSlots.findFirstFree(today(8, 0));
        int count = 0;
        while (!freeAppointments.getAppointments().isEmpty()) {
            reserveAppointmentService.reserve(freeAppointments.getAppointments().first());
            freeAppointments = findFreeSlots.findFirstFree(today(8, 0));
            count++;
        }
        stopwatch.stop();
        long elapsedMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("elapsed:" + elapsedMs);
        System.out.println("free slots count : " + storage.size());
        System.out.println("reservations count : " + count + " in " + elapsedMs + " ms.");
        System.out.println("res/s : " + (1000 * count / elapsedMs));

    }
}
