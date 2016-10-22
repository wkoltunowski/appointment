package com.falco.appointment;

import com.falco.appointment.scheduling.application.AppointmentTakenException;
import com.falco.appointment.scheduling.application.FindFreeRangesService;
import com.falco.appointment.scheduling.domain.SearchTags;
import com.falco.appointment.scheduling.domain.TagValue;
import com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.falco.appointment.scheduling.domain.schedule.DailySchedule;
import com.falco.appointment.scheduling.domain.schedule.ScheduleId;
import com.falco.appointment.scheduling.domain.schedule.Validity;
import com.falco.appointment.scheduling.domain.schedule.WorkingHours;
import com.falco.appointment.visitreservation.application.PatientReservationService;
import com.falco.appointment.visitreservation.domain.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.falco.appointment.visitreservation.domain.DoctorTag.doctorIs;
import static com.falco.appointment.visitreservation.domain.LocationTag.locationIs;
import static com.falco.appointment.visitreservation.domain.PatientReservation.serviceReservation;
import static com.falco.appointment.visitreservation.domain.ServiceTag.serviceIs;
import static com.falco.testsupport.DateTestUtils.todayAt;
import static java.time.LocalDateTime.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

public class ReservationAcceptanceTest {

    private static final PatientId PATIENT_DOUGLAS = PatientId.randomId();
    private static final LocationId WARSAW = LocationId.newId();
    private static final ServiceId CONSULTATION = ServiceId.newId();
    private static final DoctorId DR_SMITH = DoctorId.newId();

    private Factory factory;
    private PatientReservationService patientReservationService;
    private ReservationRepository reservationRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        factory = new Factory();
        givenSchedule(
                WorkingHours.ofHours("08:00-10:00"),
                Validity.validFromTo(LocalDate.now(), LocalDate.now()),
                Optional.of(Duration.parse("PT15M")),
                SearchTags.ofTags(doctorIs(DR_SMITH), locationIs(WARSAW), serviceIs(CONSULTATION))
        );

        patientReservationService = factory.patientReservation();
        reservationRepository = factory.reservationRepository();
    }

    @Test
    public void shouldReserveVisit() throws Exception {
        ScheduleRange drSmithAppointment = findFirstFree(todayAt("08:00"), serviceIs(CONSULTATION));
        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, CONSULTATION, drSmithAppointment);
        assertThat(findPatientReservations(PATIENT_DOUGLAS), hasItem(serviceReservation(PATIENT_DOUGLAS, CONSULTATION, drSmithAppointment)));
    }

    @Test
    public void shouldNotFindReserved() throws Exception {
        ScheduleRange drSmithConsultation = findFirstFree(todayAt("08:00"), serviceIs(CONSULTATION), doctorIs(DR_SMITH));
        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, CONSULTATION, drSmithConsultation);
        assertThat(findFree(now(), serviceIs(CONSULTATION), doctorIs(DR_SMITH)), not(hasItem(drSmithConsultation)));
    }

    @Test(expectedExceptions = AppointmentTakenException.class)
    public void shouldNotReserveTwice() throws Exception {
        ScheduleRange drSmithConsultation = findFirstFree(todayAt("08:00"), serviceIs(CONSULTATION), doctorIs(DR_SMITH));

        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, CONSULTATION, drSmithConsultation);
        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, CONSULTATION, drSmithConsultation);
    }

    @Test
    public void shouldCancel() throws Exception {
        ScheduleRange visitAt8am = findFirstFree(todayAt("08:00"), serviceIs(CONSULTATION), doctorIs(DR_SMITH));

        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, CONSULTATION, visitAt8am);
        patientReservationService.cancelReservation(visitAt8am);
        assertThat(findFree(todayAt("08:00"), serviceIs(CONSULTATION), doctorIs(DR_SMITH)), hasItem(visitAt8am));
    }

    private ScheduleRange findFirstFree(LocalDateTime now, TagValue... tags) {
        return findFree(now, tags).get(0);
    }

    private List<ScheduleRange> findFree(LocalDateTime now, TagValue... tags) {
        FindFreeRangesService freeService = factory.findFreeService(10);
        return freeService.findFirstFree(now, SearchTags.ofTags(tags));
    }

    private List<PatientReservation> findPatientReservations(PatientId patientId) {
        return reservationRepository.findPatientReservations(patientId);
    }

    private ScheduleId givenSchedule(WorkingHours workingHours, Validity validity, Optional<Duration> duration, SearchTags searchTags) {
        return factory.scheduleDefinitionService().addDailySchedule(new DailySchedule(workingHours, validity, duration, searchTags));
    }

}
