package com.example.appointment;

import com.example.appointment.application.DefineScheduleService;
import com.example.appointment.application.FindFreeAppointmentsService;
import com.example.appointment.application.ReserveAppointmentService;
import com.example.appointment.domain.freeslot.Appointments;
import com.example.appointment.domain.freeslot.FreeSlotRepository;
import com.example.appointment.domain.schedule.WorkingHours;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.example.appointment.DateTestUtils.today;

public class AppointmentReservationPerformanceTest {
    private FindFreeAppointmentsService findFreeSlots;
    private DefineScheduleService defineScheduleService;
    private ReserveAppointmentService reserveAppointmentService;
    private FreeSlotRepository storage;

    @BeforeMethod
    public void setUp() throws Exception {
        Factory factory = new Factory();
        findFreeSlots = factory.findFreeService(50);
        defineScheduleService = factory.scheduleDefinitionService();
        reserveAppointmentService = factory.reservationService();
        storage = factory.freeSlotRepository();
    }

    @Test
    public void shouldReserveAppointmentsStartingFromToday() throws Exception {
        generateNSchedules(100);
        reserveFirstFreeFor(() -> today(8, 0));
    }

    @Test
    public void shouldReserveAppointmentsStartingFromRandom() throws Exception {
        generateNSchedules(100);
        reserveFirstFreeFor(() -> randomDate(today(8, 0), today(8, 0).plusDays(30)));
    }

    private void reserveFirstFreeFor(Supplier<LocalDateTime> date) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Appointments appointments = findFreeSlots.findFirstFree(date.get());
        int count = 0;
        while (!appointments.getAppointments().isEmpty()) {
            reserveAppointmentService.reserve(appointments.getAppointments().first());
            appointments = findFreeSlots.findFirstFree(date.get());
            count++;
        }
        stopwatch.stop();
        long elapsedMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("free slots count \t\t: " + storage.size());
        System.out.println("reservations count \t\t: " + count + " in " + elapsedMs + " ms.");
        System.out.println("res/s \t\t\t\t\t: " + (1000 * count / elapsedMs));
    }

    private void generateNSchedules(int n) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < n; i++) {
            defineScheduleService.addSchedule(WorkingHours.ofHours("08:00-16:00"), Duration.ofMinutes(15));
        }
        System.out.println("free slots count\t\t\t : " + storage.size());
        stopwatch.stop();
        long elapsedMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("generateNSchedules took\t\t :" + elapsedMs);
    }

    @Test
    public void shouldFindAppointmentsForManySchedules() throws Exception {
        generateNSchedules(100);
        int count = 0;
        List<LocalDateTime> randomDates = Lists.newArrayList();
        while (count < 100000) {
            randomDates.add(randomDate(today(8, 0), today(8, 0).plusDays(90)));
            count++;
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (LocalDateTime randomDate : randomDates) {
            findFreeSlots.findFirstFree(randomDate);
        }

        stopwatch.stop();
        long elapsedMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("free slots count : " + storage.size());
        System.out.println("reservations count : " + count + " in " + elapsedMs + " ms.");
        System.out.println("free res/s : " + (1000 * count / elapsedMs));

    }

    private LocalDateTime randomDate(LocalDateTime start, LocalDateTime end) {
        double random = Math.random();
        long secondsBetweenStartEnd = Duration.between(start, end).getSeconds();
        LocalDateTime randomDateBetweenStartEnd = start.plusSeconds(Math.round(random * secondsBetweenStartEnd));
        return randomDateBetweenStartEnd;
    }
}
