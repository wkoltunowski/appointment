package com.example.appointment.scheduling.infrastructure.restapi;

import com.example.appointment.scheduling.application.FindFreeScheduleRangesService;
import com.example.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.scheduling.domain.schedule.ScheduleId;
import com.example.appointment.visitreservation.application.PatientReservationService;
import com.example.appointment.visitreservation.domain.PatientId;
import com.example.appointment.visitreservation.domain.ServiceId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class ReservationController {
    @Autowired
    private PatientReservationService patientReservationService;
    @Autowired
    private FindFreeScheduleRangesService find;

    @RequestMapping("/reserve")
    public void reserve(@RequestParam(value = "scheduleId", required = true) String scheduleId,
                        @RequestParam(value = "patientId", required = true) String patientId,
                        @RequestParam(value = "serviceId", required = true) String serviceId,
                        @RequestParam(value = "start", required = true) String start,
                        @RequestParam(value = "duration", required = true) String duration

    ) {
        patientReservationService.makeReservationFor(PatientId.of(patientId),
                ServiceId.of(serviceId),
                ScheduleRange.scheduleRange(
                        LocalDateTime.parse(start),
                        Duration.parse(duration),
                        ScheduleId.of(scheduleId)));
    }

    @RequestMapping("/reserveFirst")
    public void reserveFirst(@RequestParam(value = "number", defaultValue = "1") Integer number) {
        List<ScheduleRange> firstFree = find.findFirstFree(LocalDateTime.now());
        int currentVisit = 0;
        while (currentVisit < firstFree.size() && currentVisit < number) {
            patientReservationService.makeReservationFor(
                    PatientId.of("5b2b9bb8-58b7-11e6-8b77-86f30ca893d3"),
                    ServiceId.of("5b2b9bb8-58b7-11e6-8b77-86f30ca893d3"),
                    firstFree.get(currentVisit));
            currentVisit++;
        }
    }
}
