public final class ClinicLocation {
    // The identifier stays fixed when the clinic description changes.
    private final String locationID;
    private String address;

    public ClinicLocation(String locationID, String address) {
        this.locationID = Checks.text(locationID, "Location ID");
        setAddress(address);
    }

    public String getLocationID() {
        return locationID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = Checks.text(address, "Clinic name/address");
    }

    // Equality uses stable identity, including when descriptive fields are edited.
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof ClinicLocation that && locationID.equals(that.locationID);
    }

    @Override
    public int hashCode() {
        return locationID.hashCode();
    }

    @Override
    public String toString() {
        return address;
    }
}
