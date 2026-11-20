import java.util.ArrayList;
import java.util.List;

public class ClinicLocationDB {
    private final List<ClinicLocation> locations = new ArrayList<>();

    public ClinicLocation find(String locationID) {
        return locations.stream().filter(location -> location.getLocationID().equals(locationID)).findFirst().orElse(null);
    }

    public ClinicLocation findOrCreate(String address) {
        String validAddress = Checks.text(address, "Clinic name/address");
        for (ClinicLocation location : locations) {
            if (location.getAddress().equalsIgnoreCase(validAddress)) {
                return location;
            }
        }
        ClinicLocation location = new ClinicLocation("CL" + (locations.size() + 1), validAddress);
        locations.add(location);
        return location;
    }

    public List<ClinicLocation> findAll() {
        return List.copyOf(locations);
    }
}
