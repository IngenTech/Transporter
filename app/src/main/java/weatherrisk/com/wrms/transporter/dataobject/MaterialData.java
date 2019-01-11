package weatherrisk.com.wrms.transporter.dataobject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 06-02-2017.
 */

public class MaterialData implements Parcelable {

    String materialId;
    String materialName;
    String materialRemark;
    String materialAmount;

    public MaterialData() {
    }

    protected MaterialData(Parcel in) {
        materialId = in.readString();
        materialRemark = in.readString();
        materialAmount = in.readString();
        materialName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(materialId);
        dest.writeString(materialRemark);
        dest.writeString(materialAmount);
        dest.writeString(materialName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MaterialData> CREATOR = new Creator<MaterialData>() {
        @Override
        public MaterialData createFromParcel(Parcel in) {
            return new MaterialData(in);
        }

        @Override
        public MaterialData[] newArray(int size) {
            return new MaterialData[size];
        }
    };

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialRemark() {
        return materialRemark;
    }

    public void setMaterialRemark(String materialRemark) {
        this.materialRemark = materialRemark;
    }

    public String getMaterialAmount() {
        return materialAmount;
    }

    public void setMaterialAmount(String materialAmount) {
        this.materialAmount = materialAmount;
    }
}
