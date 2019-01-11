package weatherrisk.com.wrms.transporter.dataobject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import weatherrisk.com.wrms.transporter.adapter.DBAdapter;

/**
 * Created by WRMS on 19-04-2016.
 */
public class OrderData implements Parcelable {

    private String orderId;
    private String customerId;
    private String customerName;
    private String fromCityId;
    private String fromAddress;
    private String toCityId;
    private String toAddress;
    private String fromCityName;
    private String toCityName;
    private String vehicleQuantity;
    private String materialTypeId;
    private String materialTypeName;
    private String item;
    private String capacity;
    private String rate;
    private String doorStatus;
    private String refrigerated;
    private String invoiceAmount;
    private String orderDate;
    private String orderStatus;


    public OrderData(){

    }

    public OrderData(Parcel in) {
        String[] data = new String[20];
        in.readStringArray(data);
        this.orderId = data[0];
        this.customerId = data[1];
        this.customerName = data[2];
        this.fromCityId = data[3];
        this.fromAddress = data[4];
        this.toCityId = data[5];
        this.toAddress = data[6];
        this.fromCityName = data[7];
        this.toCityName = data[8];
        this.vehicleQuantity = data[9];
        this.materialTypeId = data[10];
        this.materialTypeName = data[11];
        this.item = data[12];
        this.capacity = data[13];
        this.rate = data[14];
        this.doorStatus = data[15];
        this.refrigerated = data[16];
        this.invoiceAmount = data[17];
        this.orderDate = data[18];
        this.orderStatus = data[19];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.orderId,
                this.customerId,
                this.customerName,
                this.fromCityId,
                this.fromAddress,
                this.toCityId,
                this.toAddress,
                this.fromCityName,
                this.toCityName,
                this.vehicleQuantity,
                this.materialTypeId,
                this.materialTypeName,
                this.item,
                this.capacity,
                this.rate,
                this.doorStatus,
                this.refrigerated,
                this.invoiceAmount,
                this.orderDate,
                this.orderStatus
        });
    }


    public static final Parcelable.Creator<OrderData> CREATOR = new Parcelable.Creator<OrderData>() {

        @Override
        public OrderData createFromParcel(Parcel source) {
            return new OrderData(source); // using parcelable constructor
        }

        @Override
        public OrderData[] newArray(int size) {
            return new OrderData[size];
        }
    };


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMaterialTypeName() {
        return materialTypeName;
    }

    public void setMaterialTypeName(String materialTypeName) {
        this.materialTypeName = materialTypeName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFromCityId() {
        return fromCityId;
    }

    public void setFromCityId(String fromCityId) {
        this.fromCityId = fromCityId;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToCityId() {
        return toCityId;
    }

    public void setToCityId(String toCityId) {
        this.toCityId = toCityId;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public void setFromCityId(String fromCityId, DBAdapter db) {
        this.fromCityId = fromCityId;
        if (fromCityId != null && fromCityId.trim().length() > 0) {
            Cursor cityById = db.cityById(fromCityId);
            if (cityById.moveToFirst()) {
                String cityName = cityById.getString(cityById.getColumnIndex(DBAdapter.CITY_NAME));
                this.fromCityName = cityName;
            }
            cityById.close();
        } else {
            this.fromCityName = "";
        }
    }

    public void setToCityId(String toCityId, DBAdapter db) {
        this.toCityId = toCityId;
        if (toCityId != null && toCityId.trim().length() > 0) {
            Cursor cityById = db.cityById(toCityId);
            if (cityById.moveToFirst()) {
                String cityName = cityById.getString(cityById.getColumnIndex(DBAdapter.CITY_NAME));
                this.toCityName = cityName;
            }
            cityById.close();
        } else {
            this.toCityName = "";
        }
    }

    public String getVehicleQuantity() {
        return vehicleQuantity;
    }

    public void setVehicleQuantity(String vehicleQuantity) {
        this.vehicleQuantity = vehicleQuantity;
    }

    public String getMaterialTypeId() {
        return materialTypeId;
    }

    public void setMaterialTypeId(String materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
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

    public String getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(String doorStatus) {
        this.doorStatus = doorStatus;
    }

    public String getRefrigerated() {
        return refrigerated;
    }

    public void setRefrigerated(String refrigerated) {
        this.refrigerated = refrigerated;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
