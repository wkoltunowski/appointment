package com.example.appointment;

import com.example.appointment.domain.Factory;

public class Application {
    private final DoctorScheduleDefinitionService doctorScheduleDefinitionService;
    private final Factory factory = new Factory();

    public Application() {
        doctorScheduleDefinitionService = new DoctorScheduleDefinitionService(factory.scheduleDefinitionService());
    }

    public DoctorScheduleDefinitionService defineDoctorSchedule() {
        return doctorScheduleDefinitionService;

    }

    public FindFreeSlotsService findFreeSlots(int maxResultCount) {
        return new FindFreeSlotsService(defineDoctorSchedule(), factory.findFreeService(maxResultCount));
    }
}
