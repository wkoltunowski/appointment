package com.example.appointment.scheduling.infrastructure.restapi;

import com.example.appointment.visitreservation.domain.DoctorId;
import com.example.appointment.visitreservation.domain.LocationId;
import com.example.appointment.visitreservation.domain.ServiceId;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Arrays.asList;

@RestController
public class DictionaryController {
    @RequestMapping("/doctors")
    public List<Doctor> doctors() {
        return asList(
                Doctor.of(DoctorId.of("20c28032-5992-11e6-8b77-86f30ca893d3"), "Kowalski", "Jan"),
                Doctor.of(DoctorId.of("29a60070-5992-11e6-8b77-86f30ca893d3"), "Malinowski", "Piotr")

        );
    }

    @RequestMapping("/locations")
    public List<Location> locations() {
        return asList(
                Location.of(LocationId.of("249b73da-5992-11e6-8b77-86f30ca893d3"), "Warszawa"),
                Location.of(LocationId.of("4857a6d6-5992-11e6-8b77-86f30ca893d3"), "Lublin")
        );
    }

    @RequestMapping("/services")
    public List<Service> services() {
        return asList(
                Service.of(ServiceId.of("82c03036-5992-11e6-8b77-86f30ca893d3"), "internista"),
                Service.of(ServiceId.of("86b14c52-5992-11e6-8b77-86f30ca893d3"), "stomatolog")
        );
    }
}
