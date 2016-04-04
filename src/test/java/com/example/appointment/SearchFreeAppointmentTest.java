package com.example.appointment;

import com.example.appointment.application.DefineScheduleService;
import com.example.appointment.application.FindFreeSlotsService;
import com.example.appointment.domain.SearchFreeSlotsCriteria;
import com.example.appointment.domain.freeslot.Appointment;
import com.example.appointment.domain.freeslot.Appointments;
import com.example.appointment.domain.schedule.ScheduleId;
import com.example.appointment.domain.schedule.WorkingHours;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.example.appointment.DateTestUtils.tommorrow;
import static com.example.appointment.DateTestUtils.tommorrowAt;
import static com.example.appointment.domain.schedule.WorkingHours.ofHours;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SearchFreeAppointmentTest {

    private FindFreeSlotsService freeSlots;
    private DefineScheduleService defineScheduleService;


    @BeforeMethod
    public void setUp() throws Exception {
        Application app = new Application();
        freeSlots = app.findFreeSlots(1);
        defineScheduleService = app.defineDoctorSchedule();
    }

    @Test
    public void shouldFindAnySlot() throws Exception {
        ScheduleId smithSchedule = addSchedule(ofHours("08:00-15:00"), drSmithJohn());
        addService(smithSchedule, consultation());

        assertThat(
                freeSlots.findFreeSlots(startingFrom(tommorrowAt(8, 0))),
                is(Appointments.of(Appointment.appointmentFor(tommorrow("08:00-08:15"), smithSchedule))
                ));
    }

    @Test
    public void shouldFindSlotByDoctor() throws Exception {
        DoctorId howardMichael = drHowardMichael();
        ScheduleId howardSchedule = addSchedule(ofHours("08:00-15:00"), howardMichael);
        addService(howardSchedule, consultation());

        ScheduleId smithSchedule = addSchedule(ofHours("08:00-15:00"), drSmithJohn());
        addService(smithSchedule, consultation());
        assertThat(
                freeSlots.findFreeSlots(startingFrom(tommorrowAt(8, 0)).forDoctor(howardMichael)),
                is(Appointments.of(Appointment.appointmentFor(tommorrow("08:00-08:15"), howardSchedule))));
    }

    @Test
    public void shouldFindSlotByService() throws Exception {

        ScheduleId smithSchedule = addSchedule(ofHours("08:00-15:00"), drSmithJohn());
        addService(smithSchedule, surgery());

        ScheduleId howardSchedule = addSchedule(ofHours("08:00-15:00"), drHowardMichael());
        ServiceId consultation = consultation();
        addService(howardSchedule, consultation);

        assertThat(freeSlots.findFreeSlots(startingFrom(tommorrowAt(8, 0)).forService(consultation)),
                is(Appointments.of(Appointment.appointmentFor(tommorrow("08:00-08:15"), howardSchedule))));
    }

    private DoctorId drSmithJohn() {
        return addDoctor("dr. Smith, John");
    }

    @Test
    public void shouldFindSlotByLocation() throws Exception {
        ScheduleId smithSchedule = addSchedule(ofHours("08:00-15:00"), drSmithJohn());
        LocationId warsaw = warsaw();
        addLocation(smithSchedule, warsaw);

        ScheduleId howardSchedule = addSchedule(ofHours("08:00-15:00"), drHowardMichael());
        addLocation(howardSchedule, lublin());

        assertThat(
                freeSlots.findFreeSlots(startingFrom(tommorrowAt(8, 0)).forLocation(warsaw.toString())),
                is(Appointments.of(Appointment.appointmentFor(tommorrow("08:00-08:15"), smithSchedule))));
    }

    private DoctorId drHowardMichael() {
        return addDoctor("dr. Howard, Michael");
    }


    private SearchFreeSlotsCriteria startingFrom(LocalDateTime startingAt) {
        return new SearchFreeSlotsCriteria(startingAt);
    }

    private LocationId lublin() {
        return LocationId.newId();
    }

    private LocationId warsaw() {
        return LocationId.newId();
    }

    private ServiceId consultation() {
        return ServiceId.newId();
    }

    private ServiceId surgery() {
        return ServiceId.newId();
    }

    private DoctorId addDoctor(String description) {
        return DoctorId.newId();
    }


    private void addLocation(ScheduleId scheduleId, LocationId location) {
        defineScheduleService.addLocation(scheduleId, location);
    }

    private void addService(ScheduleId scheduleId, ServiceId service) {
        defineScheduleService.addService(scheduleId, service);
    }

    private ScheduleId addSchedule(WorkingHours workingHours, DoctorId doctorId) {
        return defineScheduleService.addSchedule(workingHours, Duration.ofMinutes(15), doctorId);

    }


}
