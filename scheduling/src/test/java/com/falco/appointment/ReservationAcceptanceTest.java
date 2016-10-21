package com.falco.appointment;

import com.falco.appointment.scheduling.application.AppointmentTakenException;
import com.falco.appointment.scheduling.application.FindFreeRangesService;
import com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.falco.appointment.scheduling.domain.schedule.DailySchedule;
import com.falco.appointment.scheduling.domain.schedule.ScheduleId;
import com.falco.appointment.visitreservation.application.PatientReservationService;
import com.falco.appointment.visitreservation.domain.*;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.falco.appointment.DateTestUtils.todayAt;
import static com.falco.appointment.DateTestUtils.todayBetween;
import static com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleRange.*;
import static com.falco.appointment.visitreservation.domain.DoctorTag.doctorIs;
import static com.falco.appointment.visitreservation.domain.LocationTag.locationIs;
import static com.falco.appointment.visitreservation.domain.PatientReservation.serviceReservation;
import static com.falco.appointment.visitreservation.domain.ServiceTag.serviceIs;
import static com.google.common.base.Preconditions.checkArgument;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ReservationAcceptanceTest {

    private static final PatientId PATIENT_DOUGLAS = PatientId.randomId();
    private static final LocationId WARSAW = LocationId.newId();
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

        smithSchedule = givenSchedule(schedule()
                .withTags(doctorIs(DR_SMITH), locationIs(WARSAW), serviceIs(CONSULTATION))
                .withDefaultDuration("PT15M")
                .forWorkingHours("08:00-10:00")
        );
        wilsonSchedule = givenSchedule(schedule()
                .withTags(doctorIs(DR_WILSON), locationIs(WARSAW), serviceIs(CONSULTATION))
                .withDefaultDuration("PT20M")
                .forWorkingHours("08:00-11:00")
        );
        patientReservationService = factory.patientReservation();
        reservationRepository = factory.reservationRepository();
    }

    @Test
    public void shouldFindReservationCandidates() throws Exception {
        List<ScheduleRange> scheduleRanges = findScheduleRanges(
                reservation()
                        .withTag(serviceIs(CONSULTATION))
                        .startingFrom(todayAt("08:00")),
                maxVisitsCount(10));
        assertThat(scheduleRanges, is(ImmutableList.of(
                scheduleRange(todayBetween("08:00-08:15"), smithSchedule),
                scheduleRange(todayBetween("08:00-08:20"), wilsonSchedule),
                scheduleRange(todayBetween("08:15-08:30"), smithSchedule),
                scheduleRange(todayBetween("08:20-08:40"), wilsonSchedule),
                scheduleRange(todayBetween("08:30-08:45"), smithSchedule),
                scheduleRange(todayBetween("08:40-09:00"), wilsonSchedule),
                scheduleRange(todayBetween("08:45-09:00"), smithSchedule),
                scheduleRange(todayBetween("09:00-09:15"), smithSchedule),
                scheduleRange(todayBetween("09:00-09:20"), wilsonSchedule),
                scheduleRange(todayBetween("09:15-09:30"), smithSchedule))

        ));
    }

    @Test
    public void shouldMakeReservation() throws Exception {
        ScheduleRange drSmithAppointment = scheduleRange(todayBetween("08:00-08:15"), smithSchedule);
        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, CONSULTATION, drSmithAppointment);

        assertThat(patientReservations(PATIENT_DOUGLAS), contains(serviceReservation(PATIENT_DOUGLAS, CONSULTATION, drSmithAppointment)));

        List<ScheduleRange> scheduleRanges = findScheduleRanges(
                reservation().withTag(serviceIs(CONSULTATION)).startingFrom(todayAt("08:00")),
                maxVisitsCount(10));

        assertThat(scheduleRanges, not(contains(drSmithAppointment)));
    }

    @Test
    public void shouldNotFindReservationForFullSchedule() throws Exception {
        DoctorId fullScheduleDoctor = DoctorId.newId();
        ScheduleId scheduleId = givenSchedule(schedule()
                .withTags(doctorIs(fullScheduleDoctor), locationIs(WARSAW), serviceIs(CONSULTATION))
                .withDefaultDuration("PT1H")
                .forWorkingHours("08:00-09:00")
                .validTill(LocalDate.now()));

        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, CONSULTATION, scheduleRange(todayBetween("08:00-09:00"), scheduleId));

        assertThat(findScheduleRanges(reservation()
                .withTags(serviceIs(CONSULTATION), doctorIs(fullScheduleDoctor))
                .startingFrom(todayAt("08:00")), maxVisitsCount(10)), hasSize(0));
    }

    @Test
    public void shouldNotFindReservationForNoSchedule() throws Exception {
        DoctorId noScheduleDoctor = DoctorId.newId();

        List<ScheduleRange> reservationsFor = findScheduleRanges(
                reservation()
                        .withTags(serviceIs(CONSULTATION), doctorIs(noScheduleDoctor))
                        .startingFrom(todayAt("08:00")),
                1);
        assertThat(reservationsFor, hasSize(0));
    }

    @Test(expectedExceptions = AppointmentTakenException.class)
    public void shouldNotReserveTwice() throws Exception {
        List<ScheduleRange> firstFree = findFreeRanges(reservation().withTags(serviceIs(CONSULTATION), doctorIs(DR_SMITH))
                .startingFrom(LocalDateTime.now()), 1);
        checkArgument(!firstFree.isEmpty());
        ScheduleRange firstFreeRange = firstFree.get(0);

        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, CONSULTATION, firstFreeRange);
        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, CONSULTATION, firstFreeRange);
    }

    private List<PatientReservation> patientReservations(PatientId patientId) {
        return reservationRepository.findPatientReservations(patientId);
    }

    private int maxVisitsCount(int maxVisitsCount) {
        return maxVisitsCount;
    }


    private List<ScheduleRange> findScheduleRanges(ReservationCriteria reservationCriteria, int maxResultCount) {
        return findFreeRanges(reservationCriteria, maxResultCount);
    }

    private List<ScheduleRange> findFreeRanges(ReservationCriteria reservationCriteria, int maxResultCount) {
        FindFreeRangesService freeService = factory.findFreeService(maxResultCount);
        return freeService.findFirstFree(reservationCriteria.getStartingFrom(), reservationCriteria.searchTags());
    }


    private ScheduleId givenSchedule(ScheduleDefinition scheduleDefinition) {
        return factory.scheduleDefinitionService()
                .addDailySchedule(
                        new DailySchedule(scheduleDefinition.workingHours(), scheduleDefinition.validity(), Optional.of(scheduleDefinition.duration()), scheduleDefinition.searchTags())
                );
    }

    private ReservationCriteria reservation() {
        return new ReservationCriteria();
    }

    private ScheduleDefinition schedule() {
        return new ScheduleDefinition();
    }
}
