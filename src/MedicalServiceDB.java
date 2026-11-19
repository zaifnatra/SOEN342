import java.util.List;

public class MedicalServiceDB {
    private final List<MedicalService> services = List.of(
            new MedicalService("MS1", "General consultation", false),
            new MedicalService("MS2", "Specialist consultation", true));

    public MedicalService find(String serviceType) {
        return services.stream().filter(service -> service.serviceID().equals(serviceType)
                || service.serviceName().equalsIgnoreCase(serviceType)).findFirst().orElse(null);
    }

    public List<MedicalService> findAll() {
        return services;
    }
}
