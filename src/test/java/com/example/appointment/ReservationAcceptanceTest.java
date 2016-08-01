package com.example.appointment;

import com.example.appointment.application.AppointmentTakenException;
import com.example.appointment.application.FindFreeScheduleRangesService;
import com.example.appointment.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.domain.reservation.PatientId;
import com.example.appointment.domain.reservation.PatientReservation;
import com.example.appointment.domain.reservation.PatientReservationService;
import com.example.appointment.domain.reservation.ReservationRepository;
import com.example.appointment.domain.DoctorId;
import com.example.appointment.domain.schedule.ScheduleId;
import com.example.appointment.domain.ServiceId;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static com.example.appointment.DateTestUtils.todayAt;
import static com.example.appointment.DateTestUtils.todayBetween;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ReservationAcceptanceTest {

    private static final PatientId PATIENT_DOUGLAS = PatientId.randomId();
    private static final String WARSAW = "Warsaw";
    private static final ServiceId CONSULTATION = ServiceId.newId();
    private static final DoctorId DR_SMITH = DoctorId.newId();
    private static final DoctorId DR_WILSON = DoctorId.newId();

    private ScheduleId smithSchedule;
    private ScheduleId wilsonSchedule;


    private Factory factory;
    private PatientReservationService patientReservationService;
    private ReservationRepository reservationRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        factory = new Factory();

        smithSchedule = givenSchedule(scheduleDefinition()
                .forDoctor(DR_SMITH)
                .atLocation(WARSAW)
                .withDefaultDuration("PT15M")
                .forWorkingHours("08:00-10:00")
                .forService(CONSULTATION)
        );
        wilsonSchedule = givenSchedule(scheduleDefinition()
                .forDoctor(DR_WILSON)
                .atLocation(WARSAW)
                .withDefaultDuration("PT20M")
                .forWorkingHours("08:00-11:00")
                .forService(CONSULTATION)
        );
        patientReservationService = factory.patientReservation();
        reservationRepository = factory.reservationRepository();
    }

    @Test
    public void shouldFindReservationCandidates() throws Exception {
        List<ScheduleRange> scheduleRanges = findScheduleRanges(
                reservationCriteria()
                        .service(CONSULTATION)
                        .startingFrom(todayAt("08:00")),
                maxVisitsCount(10));
        assertThat(scheduleRanges, is(ImmutableList.of(
                ScheduleRange.scheduleRange(todayBetween("08:00-08:15"), smithSchedule),
                ScheduleRange.scheduleRange(todayBetween("08:00-08:20"), wilsonSchedule),
                ScheduleRange.scheduleRange(todayBetween("08:15-08:30"), smithSchedule),
                ScheduleRange.scheduleRange(todayBetween("08:20-08:40"), wilsonSchedule),
                ScheduleRange.scheduleRange(todayBetween("08:30-08:45"), smithSchedule),
                ScheduleRange.scheduleRange(todayBetween("08:40-09:00"), wilsonSchedule),
                ScheduleRange.scheduleRange(todayBetween("08:45-09:00"), smithSchedule),
                ScheduleRange.scheduleRange(todayBetween("09:00-09:15"), smithSchedule),
                ScheduleRange.scheduleRange(todayBetween("09:00-09:20"), wilsonSchedule),
                ScheduleRange.scheduleRange(todayBetween("09:15-09:30"), smithSchedule))

        ));
    }

    @Test
    public void shouldMakeReservation() throws Exception {
        ReservationCriteria reservationCriteria = reservationCriteria()
                .service(CONSULTATION)
                .doctor(DR_SMITH)
                .startingFrom(todayAt("08:00"));
        List<ScheduleRange> freeRanges = findFreeRanges(reservationCriteria, maxVisitsCount(1));
        if (freeRanges.isEmpty()) {
            throw noReservationsException(reservationCriteria).get();
        }
        ScheduleRange firstCandidate = freeRanges.get(0);
        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, firstCandidate, CONSULTATION);


        assertThat(reservationRepository.findPatientReservations(PATIENT_DOUGLAS), contains(
                PatientReservation.serviceReservation(PATIENT_DOUGLAS, CONSULTATION,
                        ScheduleRange.scheduleRange(todayBetween("08:00-08:15"), smithSchedule))
        ));
    }

    private Supplier<IllegalStateException> noReservationsException(ReservationCriteria reservationCriteria) {
        return () -> new IllegalStateException("No reservations found for :" + reservationCriteria);
    }

    @Test
    public void shouldNotFindReservationForFullSchedule() throws Exception {
        DoctorId fullScheduleDoctor = DoctorId.newId();
        givenSchedule(scheduleDefinition()
                .forDoctor(fullScheduleDoctor)
                .withDefaultDuration("PT1H")
                .atLocation(WARSAW)
                .forWorkingHours("08:00-09:00")
                .validTill(LocalDate.now())
                .forService(CONSULTATION));

        ReservationCriteria criteria = reservationCriteria()
                .service(CONSULTATION)
                .doctor(fullScheduleDoctor)
                .startingFrom(todayAt("08:00"));
        reserveFirst(criteria);

        assertThat(findScheduleRanges(criteria, maxVisitsCount(10)), hasSize(0));
    }

    @Test
    public void shouldNotFindReservationForNoSchedule() throws Exception {
        DoctorId noScheduleDoctor = DoctorId.newId();

        List<ScheduleRange> reservationsFor = findScheduleRanges(
                reservationCriteria()
                        .service(CONSULTATION)
                        .doctor(noScheduleDoctor)
                        .startingFrom(todayAt("08:00")),
                1);
        assertThat(reservationsFor, hasSize(0));
    }

    @Test(expectedExceptions = AppointmentTakenException.class)
    public void shouldNotReserveTwice() throws Exception {
        List<ScheduleRange> firstFree = findFreeRanges(reservationCriteria()
                .service(CONSULTATION)
                .doctor(DR_SMITH)
                .startingFrom(LocalDateTime.now()), 1);
        if (firstFree.isEmpty()) {
            throw new IllegalStateException();
        }
        ScheduleRange firstFreeRange = firstFree.get(0);

        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, firstFreeRange, CONSULTATION);
        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, firstFreeRange, CONSULTATION);
    }

    private int maxVisitsCount(int maxVisitsCount) {
        return maxVisitsCount;
    }


    private void reserveFirst(ReservationCriteria reservationCriteria) {
        List<ScheduleRange> firstFree = findFreeRanges(reservationCriteria, 1);
        if (firstFree.isEmpty()) {
            throw new IllegalArgumentException("no reservations found for :" + reservationCriteria);
        }
        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, firstFree.get(0), reservationCriteria.serviceId());
    }

    private List<ScheduleRange> findScheduleRanges(ReservationCriteria reservationCriteria, int maxResultCount) {
        return findFreeRanges(reservationCriteria, maxResultCount);
    }

    private List<ScheduleRange> findFreeRanges(ReservationCriteria reservationCriteria, int maxResultCount) {
        FindFreeScheduleRangesService freeService = factory.findFreeService(maxResultCount);
        return freeService.findFirstFree(reservationCriteria.getStartingFrom(), reservationCriteria.searchTags());
    }


    private ScheduleId givenSchedule(ScheduleDefinition scheduleDefinition) {
        return factory.scheduleDefinitionService()
                .addDailySchedule(
                        scheduleDefinition.workingHours(),
                        scheduleDefinition.validity(),
                        scheduleDefinition.duration(),
                        scheduleDefinition.scheduleConnections().searchTagsFor()

                );
    }

    private ReservationCriteria reservationCriteria() {
        return new ReservationCriteria();
    }

    private ScheduleDefinition scheduleDefinition() {
        return new ScheduleDefinition();
    }
}
