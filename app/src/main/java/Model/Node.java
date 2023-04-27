package Model;

public class Node implements Comparable<Node> {
    double lat, lon;
    Long id;

    //private String country, municipality, place, postcode, city, housenumber, street;
    //private String postcode, city, housenumber, street;
    //private StringBuilder address;

    public Node(double lat, double lon, Long id) {
        this.lat = lat;
        this.lon = lon;
        this.id = id;
    }
    @Override
    public int compareTo(Node otherNode){
        return Long.compare(id, otherNode.id);
    }
    
    /*/
    public String getAddress() {
        String res = String.format("%s %s, %s %s", street, housenumber, postcode, city);
        postcode = city = housenumber = street = null;
        return res;
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
    //*/

    /*/
    public String getAddress() {
        return String.format("%s %s, %s %s", street, housenumber, postcode, city);
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
    //*/
}