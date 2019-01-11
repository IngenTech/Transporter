package weatherrisk.com.wrms.transporter.dataobject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WRMS on 12-04-2016.
 */
public class MaterialTypeData implements Parcelable {
    private String id;
    private String materialTypeId;
    private String materialTypeName;
    private String invoiceId;
    private String amount;
    private String remark;

    public MaterialTypeData(){

    }

    public MaterialTypeData(Parcel in) {
        String[] data = new String[7];
        in.readStringArray(data);
        this.id = data[0];
        this.materialTypeId = data[1];
        this.materialTypeName = data[2];
        this.invoiceId = data[3];
        this.amount = data[4];
        this.remark = data[5];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.id,
                this.materialTypeId,
                this.materialTypeName,
                this.invoiceId,
                this.amount,
                this.remark
        });
    }

    public static final Parcelable.Creator<MaterialTypeData> CREATOR = new Parcelable.Creator<MaterialTypeData>() {

        @Override
        public MaterialTypeData createFromParcel(Parcel source) {
            return new MaterialTypeData(source); // using parcelable constructor
        }

        @Override
        public MaterialTypeData[] newArray(int size) {
            return new MaterialTypeData[size];
        }
    };



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaterialTypeId() {
        return materialTypeId;
    }

    public void setMaterialTypeId(String materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public String getMaterialTypeName() {
        return materialTypeName;
    }

    public void setMaterialTypeName(String materialTypeName) {
        this.materialTypeName = materialTypeName;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
