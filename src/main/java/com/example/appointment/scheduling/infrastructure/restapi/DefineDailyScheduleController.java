package com.example.appointment.scheduling.infrastructure.restapi;

import com.example.appointment.scheduling.application.DefineNewScheduleService;
import com.example.appointment.scheduling.domain.SearchTags;
import com.example.appointment.scheduling.domain.schedule.ScheduleId;
import com.example.appointment.scheduling.domain.schedule.WorkingHours;
import com.example.appointment.visitreservation.domain.DoctorId;
import com.example.appointment.visitreservation.domain.ServiceId;
import com.example.appointment.visitreservation.domain.ServiceTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static com.example.appointment.visitreservation.domain.DoctorTag.doctorIs;
import static com.example.appointment.visitreservation.domain.ServiceTag.*;

@RestController
public class DefineDailyScheduleController {
    @Autowired
    private DefineNewScheduleService defineScheduleService;

    @RequestMapping(value = "/addDailySchedule", method = RequestMethod.GET)
    public void addDailySchedule() {
        defineScheduleService.addDailySchedule(WorkingHours.ofHours(""), Duration.parse(""), SearchTags.empty());
    }

    @RequestMapping(value = "/initTestData")
    public ScheduleIdRest initTestData() {
        ScheduleId newScheduleId = defineScheduleService.addDailySchedule(WorkingHours.ofHours("08:00-14:00"),
                Duration.parse("PT15M"),
                SearchTags.empty().withTags(
                        doctorIs(DoctorId.of("20c28032-5992-11e6-8b77-86f30ca893d3")),
                        serviceIs(ServiceId.of("82c03036-5992-11e6-8b77-86f30ca893d3"))));
        return new ScheduleIdRest(newScheduleId.id().toString());

    }
}
