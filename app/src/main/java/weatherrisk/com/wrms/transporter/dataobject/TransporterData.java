package weatherrisk.com.wrms.transporter.dataobject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import weatherrisk.com.wrms.transporter.adapter.DBAdapter;

/**
 * Created by WRMS on 27-04-2016.
 */
public class TransporterData implements Parcelable {

    private String firmId;
    private String firmName;
    private String contactName;
    private String personContactNo;
    private String officeContactNo;
    private String address;
    private String fromCityId;
    private String doorClosed;
    private String refrigerated;
    private String materialId;
    private String capacity;
    private String rate;
    private String availableVehicle;
    private String fromStateId;
    private String toStateId;
    private String toCityId;
    private String fromStateName;
    private String toStateName;
    private String fromCityName;
    private String toCityName;

    public TransporterData() {

    }

    public TransporterData(Parcel in) {
        String[] data = new String[20];
        in.readStringArray(data);
        this.firmId = data[0];
        this.firmName = data[1];
        this.contactName = data[2];
        this.personContactNo = data[3];
        this.officeContactNo = data[4];
        this.address = data[5];
        this.fromCityId = data[6];
        this.doorClosed = data[7];
        this.refrigerated = data[8];
        this.materialId = data[9];
        this.capacity = data[10];
        this.rate = data[11];
        this.availableVehicle = data[12];
        this.fromStateId = data[13];
        this.toStateId = data[14];
        this.toCityId = data[15];
        this.fromStateName = data[16];
        this.toStateName = data[17];
        this.fromCityName = data[18];
        this.toCityName = data[19];


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.firmId,
                this.firmName,
                this.contactName,
                this.personContactNo,
                this.officeContactNo,
                this.address,
                this.fromCityId,
                this.doorClosed,
                this.refrigerated,
                this.materialId,
                this.capacity,
                this.rate,
                this.availableVehicle,
                this.fromStateId,
                this.toStateId,
                this.toCityId,
                this.fromStateName,
                this.toStateName,
                this.fromCityName,
                this.toCityName
        });
    }

    public static final Parcelable.Creator<TransporterData> CREATOR = new Parcelable.Creator<TransporterData>() {

        @Override
        public TransporterData createFromParcel(Parcel source) {
            return new TransporterData(source); // using parcelable constructor
        }

        @Override
        public TransporterData[] newArray(int size) {
            return new TransporterData[size];
        }
    };


    public String getFromStateName() {
        return fromStateName;
    }

    public void setFromStateName(String fromStateName) {
        this.fromStateName = fromStateName;
    }

    public String getToStateName() {
        return toStateName;
    }

    public void setToStateName(String toStateName) {
        this.toStateName = toStateName;
    }

    public String getFromCityName() {
        return fromCityName;
    }

    public void setFromCityName(String fromCityName) {
        this.fromCityName = fromCityName;
    }

    public String getToCityName() {
        return toCityName;
    }

    public void setToCityName(String toCityName) {
        this.toCityName = toCityName;
    }

    public String getFirmId() {
        return firmId;
    }

    public void setFirmId(String firmId) {
        this.firmId = firmId;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPersonContactNo() {
        return personContactNo;
    }

    public void setPersonContactNo(String personContactNo) {
        this.personContactNo = personContactNo;
    }

    public String getOfficeContactNo() {
        return officeContactNo;
    }

    public void setOfficeContactNo(String officeContactNo) {
        this.officeContactNo = officeContactNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFromCityId() {
        return fromCityId;
    }

    /*public void setFromCityId(String fromCityId) {
        this.fromCityId = fromCityId;
    }*/

    public String getDoorClosed() {
        return doorClosed;
    }

    public void setDoorClosed(String doorClosed) {
        this.doorClosed = doorClosed;
    }

    public String getRefrigerated() {
        return refrigerated;
    }

    public void setRefrigerated(String refrigerated) {
        this.refrigerated = refrigerated;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAvailableVehicle() {
        return availableVehicle;
    }

    public void setAvailableVehicle(String availableVehicle) {
        this.availableVehicle = availableVehicle;
    }

    public String getFromStateId() {
        return fromStateId;
    }

    /*public void setFromStateId(String fromStateId) {
        this.fromStateId = fromStateId;
    }*/

    public String getToStateId() {
        return toStateId;
    }

    /*public void setToStateId(String toStateId) {
        this.toStateId = toStateId;
    }*/

    public String getToCityId() {
        return toCityId;
    }

/*
    public void setToCityId(String toCityId) {
        this.toCityId = toCityId;
    }
*/


    public void setFromStateId(String fromStateId,DBAdapter db ) {
        this.fromStateId = fromStateId;
        if(fromStateId!=null && fromStateId.trim().length()>0){
            Cursor stateById = db.stateById(fromStateId);
            if(stateById.moveToFirst()) {
                String stateName = stateById.getString(stateById.getColumnIndex(DBAdapter.STATE_NAME));
                this.fromStateName = stateName;
            }
            stateById.close();
        }else{
            this.fromStateName = "";
        }
    }

    public void setToStateId(String toStateId,DBAdapter db) {
        this.toStateId = toStateId;
        if(toStateId!=null && toStateId.trim().length()>0){
            Cursor stateById = db.stateById(toStateId);
            if(stateById.moveToFirst()) {
                String stateName = stateById.getString(stateById.getColumnIndex(DBAdapter.STATE_NAME));
                this.toStateName = stateName;
            }
            stateById.close();
        }else{
            this.toStateName = "";
        }
    }

    public void setFromCityId(String fromCityId,DBAdapter db) {
        this.fromCityId = fromCityId;
        if(fromCityId!=null && fromCityId.trim().length()>0){
            Cursor cityById = db.cityById(fromCityId);
            if(cityById.moveToFirst()) {
                String cityName = cityById.getString(cityById.getColumnIndex(DBAdapter.CITY_NAME));
                this.fromCityName = cityName;
            }
            cityById.close();
        }else{
            this.fromCityName = "";
        }
    }

    public void setToCityId(String toCityId,DBAdapter db) {
        this.toCityId = toCityId;
        if(toCityId!=null && toCityId.trim().length()>0){
            Cursor cityById = db.cityById(toCityId);
            if(cityById.moveToFirst()) {
                String cityName = cityById.getString(cityById.getColumnIndex(DBAdapter.CITY_NAME));
                this.toCityName = cityName;
            }
            cityById.close();
        }else{
            this.toCityName = "";
        }
    }


}
