package weatherrisk.com.wrms.transporter.dataobject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WRMS on 15-02-2016.
 */
public class VehicleData implements Parcelable {

    public static final String SELECTED = "selected";
    public static final String NOT_SELECTED = "not_selected";

    private String vehicleId;
    private String VehicleNo;
    private String imei;
    private String ModelNo;
    private String registrationNo;
    private String insuranceNo;
    private String validityDate;
    private String pollutionNo;
    private String yearOfPurchase;
    private String capacity;
    private String refrigerated;
    private String closeDore;
    private String selected;

    public VehicleData(String vehicleId, String vehicleNo, String imei,
                       String modelNo, String registrationNo, String insuranceNo,
                       String validityDate, String pollutionNo, String yearOfPurchase,
                       String capacity, String refrigerated, String closeDore,String selected) {
        this.vehicleId = vehicleId;
        this.VehicleNo = vehicleNo;
        this.imei = imei;
        this.ModelNo = modelNo;
        this.registrationNo = registrationNo;
        this.insuranceNo = insuranceNo;
        this.validityDate = validityDate;
        this.pollutionNo = pollutionNo;
        this.yearOfPurchase = yearOfPurchase;
        this.capacity = capacity;
        this.refrigerated = refrigerated;
        this.closeDore = closeDore;
        this.selected = selected;
    }

    public VehicleData() {

    }

    public VehicleData(Parcel in) {
        String[] data = new String[13];
        in.readStringArray(data);
        this.vehicleId = data[0];
        this.VehicleNo = data[1];
        this.imei = data[2];
        this.ModelNo = data[3];
        this.registrationNo = data[4];
        this.insuranceNo = data[5];
        this.validityDate = data[6];
        this.pollutionNo = data[7];
        this.yearOfPurchase = data[8];
        this.capacity = data[9];
        this.refrigerated = data[10];
        this.closeDore = data[11];
        this.selected = data[12];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.vehicleId,
                this.VehicleNo,
                this.imei,
                this.ModelNo,
                this.registrationNo,
                this.insuranceNo,
                this.validityDate,
                this.pollutionNo,
                this.yearOfPurchase,
                this.capacity,
                this.refrigerated,
                this.closeDore,
                this.selected
        });
    }

    public static final Parcelable.Creator<VehicleData> CREATOR = new Parcelable.Creator<VehicleData>() {

        @Override
        public VehicleData createFromParcel(Parcel source) {
            return new VehicleData(source); // using parcelable constructor
        }

        @Override
        public VehicleData[] newArray(int size) {
            return new VehicleData[size];
        }
    };

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        VehicleNo = vehicleNo;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getModelNo() {
        return ModelNo;
    }

    public void setModelNo(String modelNo) {
        ModelNo = modelNo;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getInsuranceNo() {
        return insuranceNo;
    }

    public void setInsuranceNo(String insuranceNo) {
        this.insuranceNo = insuranceNo;
    }

    public String getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(String validityDate) {
        this.validityDate = validityDate;
    }

    public String getPollutionNo() {
        return pollutionNo;
    }

    public void setPollutionNo(String pollutionNo) {
        this.pollutionNo = pollutionNo;
    }

    public String getYearOfPurchase() {
        return yearOfPurchase;
    }

    public void setYearOfPurchase(String yearOfPurchase) {
        this.yearOfPurchase = yearOfPurchase;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getRefrigerated() {
        return refrigerated;
    }

    public void setRefrigerated(String refrigerated) {
        this.refrigerated = refrigerated;
    }

    public String getCloseDore() {
        return closeDore;
    }

    public void setCloseDore(String closeDore) {
        this.closeDore = closeDore;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}
