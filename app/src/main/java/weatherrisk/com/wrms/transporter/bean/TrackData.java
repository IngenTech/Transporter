package weatherrisk.com.wrms.transporter.bean;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by WRMS on 20-02-2016.
 */
public class TrackData {

    private String speed;
    private double latitude;
    private double longitude;
    private String distance;
    private String dateTime;
    private String address;
    private Marker marker;


    public TrackData(String speed, double latitude, double longitude, String distance, String dateTime, String address) {
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.dateTime = dateTime;
        this.address = address;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSpeed() {

        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
