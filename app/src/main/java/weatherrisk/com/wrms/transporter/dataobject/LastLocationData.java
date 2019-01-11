package weatherrisk.com.wrms.transporter.dataobject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WRMS on 24-02-2016.
 */
public class LastLocationData implements Parcelable {
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

    public LastLocationData(double lat, double lon, String imei, String vehicleNo, String deviceDateTime, double speed, double dayMaxSpeed, String dayMAxSpeedTime, String haltTime, String runningStatus, String address, String sensorInfo) {
        this.lat = String.valueOf(lat);
        this.lon = String.valueOf(lon);
        this.imei = imei;
        this.vehicleNo = vehicleNo;
        this.deviceDateTime = deviceDateTime;
        this.speed = String.valueOf(speed);
        this.dayMaxSpeed = String.valueOf(dayMaxSpeed);
        this.dayMAxSpeedTime = dayMAxSpeedTime;
        this.haltTime = haltTime;
        this.runningStatus = runningStatus;
        this.address = address;
        this.sensorInfo = sensorInfo;
//        this.allPoints = allPoints;//        this.allPoints = allPoints;
    }


    public LastLocationData() {

    }

    public LastLocationData(Parcel in) {
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
        dest.writeStringArray(new String[]{
                this.lat,
                this.lon,
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

    public static final Creator<LastLocationData> CREATOR = new Creator<LastLocationData>() {

        @Override
        public LastLocationData createFromParcel(Parcel source) {
            return new LastLocationData(source); // using parcelable constructor
        }

        @Override
        public LastLocationData[] newArray(int size) {
            return new LastLocationData[size];
        }
    };


    public double getLat() {
        try {
            return Double.parseDouble(lat);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public void setLat(double lat) {
        this.lat = String.valueOf(lat);
    }

    public double getLon() {
        try {
            return Double.parseDouble(lon);
        } catch (NumberFormatException e) {
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

    public double getSpeed() {
        try {
            return Double.parseDouble(speed);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public void setSpeed(double speed) {
        this.speed = String.valueOf(speed);
    }

    public double getDayMaxSpeed() {
        try {
            return Double.parseDouble(dayMaxSpeed);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public void setDayMaxSpeed(double dayMaxSpeed) {
        this.dayMaxSpeed = String.valueOf(dayMaxSpeed);
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


}


