package com.example.appointment;

import com.example.appointment.application.FindFreeAppointmentsService;
import com.example.appointment.domain.freeslot.AppointmentTakenException;
import com.example.appointment.domain.freeslot.FreeAppointment;
import com.example.appointment.domain.freeslot.FreeAppointments;
import com.example.appointment.domain.freeslot.SearchTags;
import com.example.appointment.domain.schedule.*;
import com.example.appointment.tmp.Reservation;
import com.example.appointment.tmp.ReservationCandidate;
import com.example.appointment.tmp.ReservationCriteria;
import com.example.appointment.tmp.ScheduleDefinition;
import com.google.common.collect.Range;
import org.apache.commons.lang3.Validate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.appointment.tmp.ReservationCandidate.reservationFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

public class ReservationAcceptanceTest {

    private Factory factory;

    private Map<String, DoctorId> doctorsByName;
    private Map<ScheduleId, String> doctorsNamesByScheduleId;

    private Map<String, ServiceId> servicesByName;
    private Map<ScheduleId, String> servicesNamesByScheduleId;

    private List<Reservation> reservations;

    @BeforeMethod
    public void setUp() throws Exception {
        factory = new Factory();
        doctorsByName = new HashMap<>();
        doctorsNamesByScheduleId = new HashMap<>();
        servicesByName = new HashMap<>();
        servicesNamesByScheduleId = new HashMap<>();
        reservations = new ArrayList<>();


        givenDoctor(drWilson());
        givenDoctor(drSmith());
        givenService(consultation());

    }

    @Test
    public void shouldFindReservationCandidates() throws Exception {
        ScheduleId smithSchedule = givenSchedule(scheduleDefinition()
                .forDoctor(drSmith())
                .atLocation("Warsaw")
                .withDefaultDuration("PT15M")
                .forWorkingHours("08:00-10:00")
                .forService(consultation())
        );
        ScheduleId wilsonSchedule = givenSchedule(scheduleDefinition()
                .forDoctor(drWilson())
                .atLocation("Warsaw")
                .withDefaultDuration("PT20M")
                .forWorkingHours("08:00-11:00")
                .forService(consultation())
        );

        ReservationCriteria reservationCriteria = reservationCriteria()
                .service(consultation())
                .startingFrom(todayAt(LocalTime.of(8, 0)));
        List<ReservationCandidate> reservationCandidates = findReservationCandidates(reservationCriteria, 10);
        assertThat(reservationCandidates, contains(
                reservationFor(today("08:00-08:15"), smithSchedule),
                reservationFor(today("08:00-08:20"), wilsonSchedule),
                reservationFor(today("08:15-08:30"), smithSchedule),
                reservationFor(today("08:20-08:40"), wilsonSchedule),
                reservationFor(today("08:30-08:45"), smithSchedule),
                reservationFor(today("08:40-09:00"), wilsonSchedule),
                reservationFor(today("08:45-09:00"), smithSchedule),
                reservationFor(today("09:00-09:15"), smithSchedule),
                reservationFor(today("09:00-09:20"), wilsonSchedule),
                reservationFor(today("09:15-09:30"), smithSchedule)

        ));
    }

    private String drWilson() {
        return "dr. Wilson";
    }

    @Test
    public void shouldMakeReservation() throws Exception {
        ScheduleId scheduleId = givenSchedule(scheduleDefinition()
                .forDoctor(drSmith())
                .atLocation("Warsaw")
                .withDefaultDuration("PT15M")
                .forWorkingHours("08:00-15:00")
                .forService(consultation())
        );

        ReservationCriteria reservationCriteria = reservationCriteria()
                .service(consultation())
                .doctor(drSmith())
                .startingFrom(todayAt(LocalTime.of(0, 8)));
        List<ReservationCandidate> reservationCandidates = findReservationCandidates(reservationCriteria, 1);
        assertThat(reservationCandidates, contains(reservationFor(today("08:00-08:15"), scheduleId)));
        Validate.notEmpty(reservationCandidates, "no reservations found for :" + reservationCriteria);
        makeReservationFor(patient("Smith, John"), reservationCandidates.get(0));

        assertThat(reservations(), contains(Reservation.of("Smith, John", drSmith(), consultation())));
    }

    private String consultation() {
        return "consultation";
    }

    @Test
    public void shouldNotFindReservationForFullSchedule() throws Exception {
        givenSchedule(scheduleDefinition()
                .forDoctor(drSmith())
                .withDefaultDuration("PT1H")
                .atLocation("Warsaw")
                .forWorkingHours("08:00-09:00")
                .validTill(LocalDate.now())
                .forService(consultation()));

        ReservationCriteria criteria = reservationCriteria()
                .service(consultation())
                .doctor(drSmith())
                .startingFrom(todayAt(LocalTime.of(8, 0)));
        reserveFirstAvailable(criteria);

        List<ReservationCandidate> reservationsFor = findReservationCandidates(criteria, 1);
        assertThat(reservationsFor, hasSize(0));
    }

    @Test
    public void shouldNotFindReservationForNoSchedule() throws Exception {
        givenSchedule(scheduleDefinition()
                .forDoctor(drWilson())
                .forWorkingHours("08:00-09:00")
                .withDefaultDuration("PT1H")
                .atLocation("Warsaw")
                .forService(consultation()));

        List<ReservationCandidate> reservationsFor = findReservationCandidates(
                reservationCriteria()
                        .service(consultation())
                        .doctor(drSmith())
                        .startingFrom(todayAt(LocalTime.of(8, 0))),
                1);
        assertThat(reservationsFor, hasSize(0));
    }

    @Test(expectedExceptions = AppointmentTakenException.class)
    public void shouldNotReserveTwice() throws Exception {
        givenSchedule(scheduleDefinition()
                .forDoctor(drSmith())
                .withDefaultDuration("PT15M")
                .atLocation("Warsaw")
                .forWorkingHours("08:00-09:00")
                .forService(consultation()));

        ReservationCandidate firstCandidate = findReservationCandidates(
                reservationCriteria()
                        .service(consultation())
                        .doctor(drSmith())
                        .startingFrom(LocalDateTime.now()), 1)
                .stream()
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        makeReservationFor(patient("Smith, John"), firstCandidate);
        makeReservationFor(patient("Smith, John"), firstCandidate);
    }

    private LocalDateTime todayAt(LocalTime time) {
        return LocalDate.now().atTime(time);
    }

    private String drSmith() {
        return "dr.Smith";
    }

    private Range<LocalDateTime> today(String visitHours) {
        return DateTestUtils.today(visitHours);
    }

    private void reserveFirstAvailable(ReservationCriteria reservationCriteria) {
        List<ReservationCandidate> reservationCandidate = findReservationCandidates(reservationCriteria, 1);
        Validate.notEmpty(reservationCandidate, "no reservations found for :" + reservationCriteria);
        makeReservationFor(patient("Smith, John"), reservationCandidate.get(0));
    }

    private List<Reservation> reservations() {
        return reservations;
    }

    private void makeReservationFor(String patient, ReservationCandidate reservationCandidate) {
        factory.reservationService().reserve(FreeAppointment.appointmentFor(reservationCandidate.range(), reservationCandidate.scheduleId()));
        reserveForPatient(patient, reservationCandidate);
    }

    private void reserveForPatient(String patient, ReservationCandidate reservationCandidate) {
        ScheduleId scheduleId = reservationCandidate.scheduleId();
        ScheduleConnections scheduleConnections = factory.scheduleRepository().findById(scheduleId).scheduleDefinition();

        String doctorName = findDoctorName(scheduleConnections.doctorId());
        String serviceName = findServiceName(scheduleConnections.serviceId());

        reservations.add(Reservation.of(patient, doctorName, serviceName));
    }

    private String findServiceName(Optional<ServiceId> serviceId) {
        return this.servicesByName.entrySet().stream().filter(entry -> entry.getValue().equals(serviceId.get())).findFirst().get().getKey();
    }

    private String findDoctorName(Optional<DoctorId> doctorId) {
        return this.doctorsByName.entrySet().stream().filter(entry -> entry.getValue().equals(doctorId.get())).findFirst().get().getKey();
    }

    private List<ReservationCandidate> findReservationCandidates(ReservationCriteria reservationCriteria, int maxResultCount) {
        FindFreeAppointmentsService freeService = factory.findFreeService(maxResultCount);
        SearchTags searchTags = searchTags(reservationCriteria);
        FreeAppointments firstFree = freeService
                .findFirstFree(reservationCriteria.getStartingFrom(), searchTags);
        return firstFree.getFreeAppointments()
                .stream()
                .map(app -> reservationFor(app.range(), app.scheduleId()))
                .collect(Collectors.toList());
    }

    private SearchTags searchTags(ReservationCriteria reservationCriteria) {
        SearchTags searchTags = SearchTags.empty();

        ServiceId serviceId = findServiceId(reservationCriteria.getServiceName());
        if (serviceId != null) {
            searchTags = searchTags.forService(serviceId.toString());
        }
        DoctorId doctorId = findDoctorId(reservationCriteria.getDoctorName());
        if (doctorId != null) {
            searchTags = searchTags.forDoctor(doctorId.toString());
        }
        return searchTags;
    }

    private ScheduleId givenSchedule(ScheduleDefinition scheduleDefinition) {
        DoctorId doctorId = findDoctorId(scheduleDefinition.getDoctorName());
        ServiceId serviceId = findServiceId(scheduleDefinition.getServiceName());

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

    private ServiceId givenService(String serviceName) {
        ServiceId serviceId = servicesByName.get(serviceName);
        if (serviceId == null) {
            serviceId = ServiceId.newId();
            servicesByName.put(serviceName, serviceId);
        }
        return serviceId;
    }

    private DoctorId givenDoctor(String doctorName) {
        DoctorId doctorId = doctorsByName.get(doctorName);
        if (doctorId == null) {
            doctorId = DoctorId.newId();
            doctorsByName.put(doctorName, doctorId);
        }
        return doctorId;
    }

    private ServiceId findServiceId(String serviceName) {
        return servicesByName.get(serviceName);
    }

    private DoctorId findDoctorId(String doctorName) {
        return doctorsByName.get(doctorName);
    }

    private String patient(String patientName) {
        return patientName;
    }

    private ReservationCriteria reservationCriteria() {
        return new ReservationCriteria();
    }

    private ScheduleDefinition scheduleDefinition() {
        return new ScheduleDefinition();
    }
}
