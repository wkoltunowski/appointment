package com.example.appointment;

import com.example.appointment.domain.schedule.ServiceId;

import java.util.HashMap;
import java.util.Map;

public class ServiceDefinitionService implements ServiceFinder {

    private Map<String, ServiceId> servicesByName = new HashMap<>();

    public ServiceId addService(String serviceName) {
        ServiceId serviceId = servicesByName.get(serviceName);
        if (serviceId == null) {
            serviceId = ServiceId.newId();
            servicesByName.put(serviceName, serviceId);
        }
        return serviceId;
    }

    @Override
    public String findServiceName(ServiceId serviceId) {
        return this.servicesByName.entrySet().stream().filter(entry -> entry.getValue().equals(serviceId)).findFirst().get().getKey();
    }

    @Override
    public ServiceId findServiceId(String serviceName) {
        return servicesByName.get(serviceName);
    }
}
