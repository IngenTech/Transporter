package weatherrisk.com.wrms.transporter.dataobject;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by WRMS on 03-03-2016.
 */
public class TravelData {

    private String startDateTime;
    private String endDateTime;
    private String startPlace;
    private String endPlace;
    private String distance;
    private String travelTime;
    private LatLng startLatLng;
    private LatLng endLatLng;

/*
    public TravelData(String startDateTime, String endDateTime, String startPlace, String endPlace, String distance, String travelTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.startPlace = startPlace;
        this.endPlace = endPlace;
        this.distance = distance;
        this.travelTime = travelTime;
    }
*/

    public TravelData(String startDateTime, String endDateTime, String distance, String travelTime, LatLng startLatLng, LatLng endLatLng) {
        this.endDateTime = endDateTime;
        this.distance = distance;
        this.travelTime = travelTime;
        this.startLatLng = startLatLng;
        this.endLatLng = endLatLng;
        this.startDateTime = startDateTime;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public String getEndPlace() {
        return endPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }

    public LatLng getStartLatLng() {
        return startLatLng;
    }

    public void setStartLatLng(LatLng startLatLng) {
        this.startLatLng = startLatLng;
    }

    public LatLng getEndLatLng() {
        return endLatLng;
    }

    public void setEndLatLng(LatLng endLatLng) {
        this.endLatLng = endLatLng;
    }
}
