package weatherrisk.com.wrms.transporter.bean;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by WRMS on 15-02-2016.
 */
public class LiveData {

//    TODO Check the below link for communication from service to activity
//    http://android-coding.blogspot.in/2011/11/pass-data-from-service-to-activity.html

    private String lat;
    private String lon;
    private String imei;
    private String vehicleNo;
    private String deviceDateTime;
    private String speed;
    private String dayMaxSpeed;
    private String dayMAxSpeedTime;
    private String haltTime;
    private String runningStatus;
    private String address;
    private String sensorInfo;
    private ArrayList<LatLng> allPoints = new ArrayList<LatLng>();
    private Marker directionMarker ;

    public LiveData(double lat, double lon, String imei, String vehicleNo, String deviceDateTime, String speed,  String haltTime, String runningStatus, String address, String sensorInfo, ArrayList<LatLng> allPoints) {
        this.lat = String.valueOf(lat);
        this.lon = String.valueOf(lon);
        this.imei = imei;
        this.vehicleNo = vehicleNo;
        this.deviceDateTime = deviceDateTime;
        this.speed = String.valueOf(speed);
        this.dayMaxSpeed = String.valueOf(dayMaxSpeed);

        this.haltTime = haltTime;
        this.runningStatus = runningStatus;
        this.address = address;
        this.sensorInfo = sensorInfo;
        this.allPoints = allPoints;
    }


    public LiveData(){

    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public Marker getDirectionMarker() {
        return directionMarker;
    }

    public void setDirectionMarker(Marker directionMarker) {
        this.directionMarker = directionMarker;
    }

    /*public LiveData(Parcel in) {
        String[] data = new String[12];

        in.readStringArray(data);


        this.lat = data[0];
        this.lon = data[1];
        this.imei = data[2];
        this.vehicleNo = data[3];
        this.deviceDateTime = data[4];
        this.speed = data[5];
        this.dayMaxSpeed = data[6];
        this.dayMAxSpeedTime = data[7];
        this.haltTime = data[8];
        this.runningStatus = data[9];
        this.address = data[10];
        this.sensorInfo = data[11];

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.lat,
                this.lon ,
                this.imei,
                this.vehicleNo,
                this.deviceDateTime,
                this.speed,
                this.dayMaxSpeed,
                this.dayMAxSpeedTime,
                this.haltTime,
                this.runningStatus,
                this.address,
                this.sensorInfo
        });
    }

    public static final Parcelable.Creator<LiveData> CREATOR = new Parcelable.Creator<LiveData>() {

        @Override
        public LiveData createFromParcel(Parcel source) {
            return new LiveData(source); // using parcelable constructor
        }

        @Override
        public LiveData[] newArray(int size) {
            return new LiveData[size];
        }
    };*/


    public double getLat() {
        try{
            return Double.parseDouble(lat);
        }catch(NumberFormatException e){
            e.printStackTrace();
            return 0.0;
        }
    }

    public void setLat(double lat) {
        this.lat = String.valueOf(lat);
    }

    public double getLon() {
        try{
            return Double.parseDouble(lon);
        }catch(NumberFormatException e){
            e.printStackTrace();
            return 0.0;
        }
    }

    public void setLon(double lon) {
        this.lon = String.valueOf(lon);
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getDeviceDateTime() {
        return deviceDateTime;
    }

    public void setDeviceDateTime(String deviceDateTime) {
        this.deviceDateTime = deviceDateTime;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDayMaxSpeed() {
       return dayMaxSpeed;
    }

    public void setDayMaxSpeed(String dayMaxSpeed) {
        this.dayMaxSpeed = dayMaxSpeed;
    }

    public String getDayMAxSpeedTime() {
        return dayMAxSpeedTime;
    }

    public void setDayMAxSpeedTime(String dayMAxSpeedTime) {
        this.dayMAxSpeedTime = dayMAxSpeedTime;
    }

    public String getHaltTime() {
        return haltTime;
    }

    public void setHaltTime(String haltTime) {
        this.haltTime = haltTime;
    }

    public String getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(String runningStatus) {
        this.runningStatus = runningStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSensorInfo() {
        return sensorInfo;
    }

    public void setSensorInfo(String sensorInfo) {
        this.sensorInfo = sensorInfo;
    }

    public ArrayList<LatLng> getAllPoints() {
        return allPoints;
    }

    public void setAllPoints(ArrayList<LatLng> allPoints) {
        this.allPoints = allPoints;
    }

    public void addPoint(LatLng latlng){
        this.allPoints.add(latlng);
    }

    public LatLng getPoint(int index){
        if(this.allPoints.size()>index){
            return this.allPoints.get(index);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if(o!=null) {
            if (o instanceof String) {
                String vehicleNo = (String) o;
                if (vehicleNo.toLowerCase().equals(this.vehicleNo.toLowerCase())){
                    return true;
                }
            }
        }
        return false;
    }
}
