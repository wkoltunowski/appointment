package com.falco.appointment.visitreservation.infrastructure;

import com.falco.appointment.scheduling.domain.schedule.Schedule;
import com.falco.appointment.scheduling.api.ScheduleId;
import com.falco.appointment.scheduling.domain.schedule.ScheduleRepository;
import com.falco.appointment.visitreservation.domain.DoctorTag;
import com.falco.appointment.visitreservation.domain.PatientReservation;
import com.falco.appointment.visitreservation.domain.ReservationRepository;
import com.falco.appointment.visitreservation.domain.ServiceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

@RestController
public class ReservationListController {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    @RequestMapping("/reservations")
    public List<Reservation> reservations(@RequestParam(value = "rowNo", defaultValue = "100") String rowNo) {
        return reservationRepository.findAll(parseInt(rowNo)).stream().map(toPatientReservation()).collect(toList());
    }

    private Function<PatientReservation, Reservation> toPatientReservation() {
        return r -> new Reservation()
                .withId(r.id().asString())
                .withPatientId(r.patient().asString())
                .withServiceId(r.serviceId().map(ServiceId::asString).orElse(null))
                .withScheduleId(r.scheduleRange().scheduleId().asString())
                .withDate(r.scheduleRange().range().lowerEndpoint().toString())
                .withDuration(r.scheduleRange().duration().toString())
                .withDoctorId(doctorFor(r.scheduleRange().scheduleId()).orElse(null))
                .withState(r.state());
    }

    private Optional<String> doctorFor(ScheduleId scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        return schedule.searchTags().get(DoctorTag.key());
    }
}
