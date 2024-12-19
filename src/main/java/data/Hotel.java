package data;

/**
 * this class is for creating objects of this type and store them in a TreeSet
 */
public class Hotel {
    private final String name;
    private final String id;
    private final String address;
    private final String city;
    private final String state;
    private final String lat;
    private final String lng;

    public Hotel(String name, String id, String address, String city, String state, String lat, String lng) {
        this.name = name;
        this.id = id;
        this.address = address;
        this.city = city;
        this.state = state;
        this.lat = lat;
        this.lng = lng;
    }

    //Getter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    /**
     * Prints data for each Hotel
     *
     * @return Hotel information
     */
    @Override
    public String toString() {
        return name + ": " + id + System.lineSeparator() +
                address + System.lineSeparator() +
                city + ", " + state;
    }
}
