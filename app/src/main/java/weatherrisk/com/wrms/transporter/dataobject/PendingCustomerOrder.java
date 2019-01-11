package weatherrisk.com.wrms.transporter.dataobject;

import android.database.Cursor;

import weatherrisk.com.wrms.transporter.adapter.DBAdapter;

/**
 * Created by WRMS on 29-04-2016.
 */
public class PendingCustomerOrder {

    private String orderId;
    private String customerID;
    private String transporterID;
    private String fromCityID;
    private String fromAddress;
    private String toCityID;
    private String toAddress;
    private String vehicleQuantity;
    private String materialTypeID;
    private String item;
    private String capacity;
    private String rate;
    private String doorStatus;
    private String refrigerated;
    private String invoiceAmount;
    private String orderDate;
    private String remark;
    private String fromCityName;
    private String toCityName;

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

    public String getFromCityID() {
        return fromCityID;
    }

    public void setFromCityID(String fromCityID) {
        this.fromCityID = fromCityID;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getTransporterID() {
        return transporterID;
    }

    public void setTransporterID(String transporterID) {
        this.transporterID = transporterID;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToCityID() {
        return toCityID;
    }

    public void setToCityID(String toCityID) {
        this.toCityID = toCityID;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getVehicleQuantity() {
        return vehicleQuantity;
    }

    public void setVehicleQuantity(String vehicleQuantity) {
        this.vehicleQuantity = vehicleQuantity;
    }

    public String getMaterialTypeID() {
        return materialTypeID;
    }

    public void setMaterialTypeID(String materialTypeID) {
        this.materialTypeID = materialTypeID;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setFromCityID(String fromCityId,DBAdapter db) {
        this.fromCityID = fromCityId;
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

    public void setToCityID(String toCityId,DBAdapter db) {
        this.toCityID = toCityId;
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
