package weatherrisk.com.wrms.transporter.bean;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Admin on 26-04-2017.
 *
 *  "ReminderId": "1",
 "VehicleId": "2",
 "ReminderTypeId": "1",
 "ReminderRemark": "need to servicing for this vehicle",
 "ReminderExpiryDate": "2016-11-30 00:00:00"
 */
public class AlertBean  {

   private String ReminderId;
    private String VehicleId;
    private String ReminderTypeId;
    private String ReminderRemark;

    private String ReminderExpiryDate;
    private String vehicleName;

    public String getReminderId() {
        return ReminderId;
    }

    public void setReminderId(String reminderId) {
        ReminderId = reminderId;
    }

    public String getVehicleId() {
        return VehicleId;
    }

    public void setVehicleId(String vehicleId) {
        VehicleId = vehicleId;
    }

    public String getReminderTypeId() {
        return ReminderTypeId;
    }

    public void setReminderTypeId(String reminderTypeId) {
        ReminderTypeId = reminderTypeId;
    }

    public String getReminderRemark() {
        return ReminderRemark;
    }

    public void setReminderRemark(String reminderRemark) {
        ReminderRemark = reminderRemark;
    }

    public String getReminderExpiryDate() {
        return ReminderExpiryDate;
    }

    public void setReminderExpiryDate(String reminderExpiryDate) {
        ReminderExpiryDate = reminderExpiryDate;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }
}
