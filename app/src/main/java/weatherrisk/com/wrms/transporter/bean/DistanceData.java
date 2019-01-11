package weatherrisk.com.wrms.transporter.bean;

/**
 * Created by Admin on 19-04-2017.
 *
 * "DeviceIMEI": "868324020449042",
 "VehicleName": "UP32 AR 5555",
 "DateFrom": "2017-03-04 00:00:01",
 "DateTo": "2017-03-04 00:00:11",
 "Distance": 0
 */
public class DistanceData  {

    String deviceImei;
    String vehicalName;
    String dateFrom;
    String dateTo;
    String distance;

    public String getDeviceImei() {
        return deviceImei;
    }

    public void setDeviceImei(String deviceImei) {
        this.deviceImei = deviceImei;
    }

    public String getVehicalName() {
        return vehicalName;
    }

    public void setVehicalName(String vehicalName) {
        this.vehicalName = vehicalName;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
