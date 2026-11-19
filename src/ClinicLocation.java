public record ClinicLocation(String locationID, String address) {
    public ClinicLocation {
        address = Checks.text(address, "Clinic name/address");
    }

    @Override
    public String toString() {
        return address;
    }
}
