package com.example.appointment;

import com.example.appointment.application.FindFreeAppointmentsService;
import com.example.appointment.domain.freeslot.AppointmentTakenException;
import com.example.appointment.domain.freeslot.FreeAppointments;
import com.example.appointment.domain.freeslot.ScheduleRange;
import com.example.appointment.domain.freeslot.SearchTags;
import com.example.appointment.domain.schedule.*;
import com.example.appointment.tmp.PatientId;
import com.example.appointment.tmp.Reservation;
import com.example.appointment.tmp.ReservationCriteria;
import com.example.appointment.tmp.ScheduleDefinition;
import org.apache.commons.lang3.Validate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.appointment.DateTestUtils.todayAt;
import static com.example.appointment.DateTestUtils.todayBetween;
import static com.example.appointment.domain.freeslot.ScheduleRange.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

public class ReservationAcceptanceTest {

    public static final PatientId PATIENT_DOUGLAS = PatientId.randomId();
    private ServiceId consultation = ServiceId.newId();

    private final DoctorId drSmith = DoctorId.newId();
    private final DoctorId drWilson = DoctorId.newId();

    private ScheduleId smithSchedule;
    private ScheduleId wilsonSchedule;


    private Factory factory;
    private PatientReservationService patientReservationService;

    @BeforeMethod
    public void setUp() throws Exception {
        factory = new Factory();

        smithSchedule = givenSchedule(scheduleDefinition()
                .forDoctor(drSmith)
                .atLocation("Warsaw")
                .withDefaultDuration("PT15M")
                .forWorkingHours("08:00-10:00")
                .forService(consultation)
        );
        wilsonSchedule = givenSchedule(scheduleDefinition()
                .forDoctor(drWilson)
                .atLocation("Warsaw")
                .withDefaultDuration("PT20M")
                .forWorkingHours("08:00-11:00")
                .forService(consultation)
        );
        patientReservationService = factory.patientReservation();
    }

    @Test
    public void shouldFindReservationCandidates() throws Exception {

        List<ScheduleRange> scheduleRanges = findReservationCandidates(
                reservationCriteria()
                        .service(consultation)
                        .startingFrom(todayAt("08:00")),
                maxVisitsCount(10));
        assertThat(scheduleRanges, contains(
                of(todayBetween("08:00-08:15"), smithSchedule),
                of(todayBetween("08:00-08:20"), wilsonSchedule),
                of(todayBetween("08:15-08:30"), smithSchedule),
                of(todayBetween("08:20-08:40"), wilsonSchedule),
                of(todayBetween("08:30-08:45"), smithSchedule),
                of(todayBetween("08:40-09:00"), wilsonSchedule),
                of(todayBetween("08:45-09:00"), smithSchedule),
                of(todayBetween("09:00-09:15"), smithSchedule),
                of(todayBetween("09:00-09:20"), wilsonSchedule),
                of(todayBetween("09:15-09:30"), smithSchedule)

        ));
    }

    @Test
    public void shouldMakeReservation() throws Exception {
        ReservationCriteria reservationCriteria = reservationCriteria()
                .service(consultation)
                .doctor(drSmith)
                .startingFrom(todayAt("08:00"));
        List<ScheduleRange> scheduleRanges = findReservationCandidates(reservationCriteria, maxVisitsCount(1));

        ScheduleRange firstCandidate = scheduleRanges
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No reservations found for :" + reservationCriteria));
        makeReservationFor(PATIENT_DOUGLAS, firstCandidate);


        assertThat(patientReservationService.findAllReservations(), contains(
                Reservation.forService(PATIENT_DOUGLAS, of(todayBetween("08:00-08:15"), smithSchedule), consultation)
        ));
    }

    @Test
    public void shouldNotFindReservationForFullSchedule() throws Exception {

        DoctorId fullScheduleDoctor = DoctorId.newId();
        givenSchedule(scheduleDefinition()
                .forDoctor(fullScheduleDoctor)
                .withDefaultDuration("PT1H")
                .atLocation("Warsaw")
                .forWorkingHours("08:00-09:00")
                .validTill(LocalDate.now())
                .forService(consultation));

        ReservationCriteria criteria = reservationCriteria()
                .service(consultation)
                .doctor(fullScheduleDoctor)
                .startingFrom(todayAt("08:00"));
        reserveFirst(criteria);

        assertThat(findReservationCandidates(criteria, maxVisitsCount(10)), hasSize(0));
    }

    @Test
    public void shouldNotFindReservationForNoSchedule() throws Exception {
        DoctorId noScheduleDoctor = DoctorId.newId();

        List<ScheduleRange> reservationsFor = findReservationCandidates(
                reservationCriteria()
                        .service(consultation)
                        .doctor(noScheduleDoctor)
                        .startingFrom(todayAt("08:00")),
                1);
        assertThat(reservationsFor, hasSize(0));
    }

    @Test(expectedExceptions = AppointmentTakenException.class)
    public void shouldNotReserveTwice() throws Exception {
        ScheduleRange firstCandidate = findReservationCandidates(
                reservationCriteria()
                        .service(consultation)
                        .doctor(drSmith)
                        .startingFrom(LocalDateTime.now()), 1)
                .stream()
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        makeReservationFor(PATIENT_DOUGLAS, firstCandidate);
        makeReservationFor(PATIENT_DOUGLAS, firstCandidate);
    }

    private int maxVisitsCount(int maxVisitsCount) {
        return maxVisitsCount;
    }


    private void reserveFirst(ReservationCriteria reservationCriteria) {
        List<ScheduleRange> scheduleRange = findReservationCandidates(reservationCriteria, 1);
        Validate.notEmpty(scheduleRange, "no reservations found for :" + reservationCriteria);
        patientReservationService.makeReservationFor(PATIENT_DOUGLAS, scheduleRange.get(0));
    }

    private void makeReservationFor(PatientId patient, ScheduleRange scheduleRange) {
        patientReservationService.makeReservationFor(patient, scheduleRange);
    }

    private List<ScheduleRange> findReservationCandidates(ReservationCriteria reservationCriteria, int maxResultCount) {
        FindFreeAppointmentsService freeService = factory.findFreeService(maxResultCount);
        SearchTags searchTags = searchTags(reservationCriteria);
        FreeAppointments firstFree = freeService
                .findFirstFree(reservationCriteria.getStartingFrom(), searchTags);
        return firstFree.getScheduleRanges()
                .stream()
                .map(app -> of(app.range(), app.scheduleId()))
                .collect(Collectors.toList());
    }

    private SearchTags searchTags(ReservationCriteria reservationCriteria) {
        SearchTags searchTags = SearchTags.empty();

        ServiceId serviceId = reservationCriteria.getService();
        if (serviceId != null) {
            searchTags = searchTags.forService(serviceId.toString());
        }
        DoctorId doctorId = reservationCriteria.getDoctor();
        if (doctorId != null) {
            searchTags = searchTags.forDoctor(doctorId.toString());
        }
        return searchTags;
    }

    private ScheduleId givenSchedule(ScheduleDefinition scheduleDefinition) {
        DoctorId doctorId = scheduleDefinition.getDoctor();
        ServiceId serviceId = scheduleDefinition.getService();

        Validity validity = Validity.infinite();
        if (scheduleDefinition.getValidTo() != null) {
            validity = Validity.validTill(scheduleDefinition.getValidTo());
        }
        ScheduleId scheduleId = factory.scheduleDefinitionService()
                .addSchedule(WorkingHours.ofHours(scheduleDefinition.getWorkingHours()),
                        validity,
                        ScheduleConnections.empty()
                                .withDoctorId(doctorId)
                                .withService(serviceId)
                                .withDuration(Duration.parse(scheduleDefinition.getDuration()))

                );
        return scheduleId;
    }

    private ReservationCriteria reservationCriteria() {
        return new ReservationCriteria();
    }

    private ScheduleDefinition scheduleDefinition() {
        return new ScheduleDefinition();
    }
}
