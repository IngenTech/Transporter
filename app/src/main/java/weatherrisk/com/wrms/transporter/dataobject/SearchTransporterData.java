package weatherrisk.com.wrms.transporter.dataobject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WRMS on 23-05-2016.
 */
public class SearchTransporterData implements Parcelable{

    String fromStateId;
    String fromCityId;
    String toStateId;
    String toCityId;
    String date;
    String materialId;
    boolean doorClosed;
    boolean refrigerated;
    String item;
    String noOfVehicles;
    String capacity;

    public SearchTransporterData(){
    }

    protected SearchTransporterData(Parcel in) {
        fromStateId = in.readString();
        fromCityId = in.readString();
        toStateId = in.readString();
        toCityId = in.readString();
        date = in.readString();
        materialId = in.readString();
        doorClosed = in.readByte() != 0;
        refrigerated = in.readByte() != 0;
        item = in.readString();
        noOfVehicles = in.readString();
        capacity = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromStateId);
        dest.writeString(fromCityId);
        dest.writeString(toStateId);
        dest.writeString(toCityId);
        dest.writeString(date);
        dest.writeString(materialId);
        dest.writeByte((byte) (doorClosed ? 1 : 0));
        dest.writeByte((byte) (refrigerated ? 1 : 0));
        dest.writeString(item);
        dest.writeString(noOfVehicles);
        dest.writeString(capacity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SearchTransporterData> CREATOR = new Creator<SearchTransporterData>() {
        @Override
        public SearchTransporterData createFromParcel(Parcel in) {
            return new SearchTransporterData(in);
        }

        @Override
        public SearchTransporterData[] newArray(int size) {
            return new SearchTransporterData[size];
        }
    };

    public boolean isDoorClosed() {
        return doorClosed;
    }

    public void setDoorClosed(boolean doorClosed) {
        this.doorClosed = doorClosed;
    }

    public boolean isRefrigerated() {
        return refrigerated;
    }

    public void setRefrigerated(boolean refrigerated) {
        this.refrigerated = refrigerated;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getNoOfVehicles() {
        return noOfVehicles;
    }

    public void setNoOfVehicles(String noOfVehicles) {
        this.noOfVehicles = noOfVehicles;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getFromStateId() {
        return fromStateId;
    }

    public void setFromStateId(String fromStateId) {
        this.fromStateId = fromStateId;
    }

    public String getFromCityId() {
        return fromCityId;
    }

    public void setFromCityId(String fromCityId) {
        this.fromCityId = fromCityId;
    }

    public String getToStateId() {
        return toStateId;
    }

    public void setToStateId(String toStateId) {
        this.toStateId = toStateId;
    }

    public String getToCityId() {
        return toCityId;
    }

    public void setToCityId(String toCityId) {
        this.toCityId = toCityId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

}
