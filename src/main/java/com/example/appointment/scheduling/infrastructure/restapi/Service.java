package com.example.appointment.scheduling.infrastructure.restapi;

import com.example.appointment.visitreservation.domain.ServiceId;

public class Service {
    private String serviceId;
    private String name;

    public static Service of(ServiceId id, String name) {
        Service service = new Service();
        service.serviceId = id.asString();
        service.name = name;
        return service;
    }

    public String getName() {
        return name;
    }

    public String getServiceId() {
        return serviceId;
    }
}
