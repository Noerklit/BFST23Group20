package Model;

public class TemporaryAddress {
    
    private String country, municipality, place, postcode, city, housenumber, street;
    private float lon, lat;

    public String get() {
        return String.format("%s %s, %s %s, %s, %s", street, housenumber, postcode, city, lon, lat);
        //return String.format("%s %s", street, housenumber);
    }

    public void setLon(float lon) {
        this.lon = lon;
    }
    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setHousenumber(String housenumber) {
        this.housenumber = housenumber;
    }
    public void setStreet(String street) {
        this.street = street;
    }
}
