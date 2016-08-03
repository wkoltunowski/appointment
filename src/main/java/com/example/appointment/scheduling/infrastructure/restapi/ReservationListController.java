package com.example.appointment.scheduling.infrastructure.restapi;

import com.example.appointment.scheduling.domain.schedule.Schedule;
import com.example.appointment.scheduling.domain.schedule.ScheduleId;
import com.example.appointment.scheduling.domain.schedule.ScheduleRepository;
import com.example.appointment.visitreservation.domain.DoctorTag;
import com.example.appointment.visitreservation.domain.PatientReservation;
import com.example.appointment.visitreservation.domain.ReservationRepository;
import com.example.appointment.visitreservation.domain.ServiceId;
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
                .withPatientId(r.patient().asString())
                .withServiceId(r.serviceId().map(ServiceId::asString).orElse(null))
                .withDate(r.scheduleRange().range().lowerEndpoint().toString())
                .withDoctorId(doctorFor(r.scheduleRange().scheduleId()).orElse(null));
    }

    private Optional<String> doctorFor(ScheduleId scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        return schedule.searchTags().get(DoctorTag.key());
    }
}
