package weatherrisk.com.wrms.transporter.dataobject;

import android.database.Cursor;

import weatherrisk.com.wrms.transporter.adapter.DBAdapter;

/**
 * Created by WRMS on 03-05-2016.
 */
public class CustomerOrderData {

    private String orderId;
    private String vehicleId;
    private String vehicleName;
    private String accountId;
    private String fromCountryId;
    private String toCountryId;
    private String fromStateId;
    private String toStateId;
    private String fromCityId;
    private String toCityId;
    private String fromStateName;
    private String toStateName;
    private String fromCityName;
    private String toCityName;
    private String fromAddress;
    private String toAddress;
    private String customerName;
    private String orderRequestId;
    private String materialTypeId;
    private String quantity;
    private String invoiceIds;
    private String dispatchDate;
    private String arrivalDate;
    private String driverName;
    private String driverMobileNo;
    private String roadPermitTempIds;
    private String orderRemark;

    public String getOrderRemark() {
        return orderRemark;
    }

    public void setOrderRemark(String orderRemark) {
        this.orderRemark = orderRemark;
    }

    public String getRoadPermitTempIds() {
        return roadPermitTempIds;
    }

    public void setRoadPermitTempIds(String roadPermitTempIds) {
        this.roadPermitTempIds = roadPermitTempIds;
    }

    public void setToCountryId(String toCountryId) {
        this.toCountryId = toCountryId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setVehicleId(String vehicleId,DBAdapter db) {
        this.vehicleId = vehicleId;
        if(vehicleId!=null && vehicleId.trim().length()>0){
            Cursor getVehicleById = db.getVehicleById(vehicleId);
            if(getVehicleById.moveToFirst()) {
                String vehicleName = getVehicleById.getString(getVehicleById.getColumnIndex(DBAdapter.VEHICLE_NO));
                this.vehicleName = vehicleName;
            }
            getVehicleById.close();
        }else{
            this.vehicleName = "";
        }
    }

    public String getVehicleName(){
        return this.vehicleName;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setFromCountryId(String fromCountryId) {
        this.fromCountryId = fromCountryId;
    }

    public void setFromStateId(String fromStateId ) {
        this.fromStateId = fromStateId;
    }

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

    public void setFromStateName(String fromStateName) {
        this.fromStateName = fromStateName;
    }

    public void setToStateName(String toStateName) {
        this.toStateName = toStateName;
    }

    public void setFromCityName(String fromCityName) {
        this.fromCityName = fromCityName;
    }

    public void setToCityName(String toCityName) {
        this.toCityName = toCityName;
    }

    public void setToStateId(String toStateId) {
        this.toStateId = toStateId;
    }

    public void setFromCityId(String fromCityId) {
        this.fromCityId = fromCityId;
    }

    public void setToCityId(String toCityId) {
        this.toCityId = toCityId;
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

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setOrderRequestId(String orderRequestId) {
        this.orderRequestId = orderRequestId;
    }

    public void setMaterialTypeId(String materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setInvoiceIds(String invoiceIds) {
        this.invoiceIds = invoiceIds;
    }

    public void setDispatchDate(String dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setDriverMobileNo(String driverMobileNo) {
        this.driverMobileNo = driverMobileNo;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getFromCountryId() {
        if(fromCountryId!=null && fromCountryId.trim().length()>0) {
            return fromCountryId;
        }else{
            return "1";
        }
    }

    public String getToCountryId() {
        if(toCountryId!=null && toCountryId.trim().length()>0) {
            return toCountryId;
        }else{
            return "1";
        }
    }

    public String getFromStateId() {
        return fromStateId;
    }

    public String getToStateId() {
        return toStateId;
    }

    public String getFromCityId() {
        return fromCityId;
    }

    public String getToCityId() {
        return toCityId;
    }

    public String getFromStateName() {
        return fromStateName;
    }

    public String getToStateName() {
        return toStateName;
    }

    public String getFromCityName() {
        return fromCityName;
    }

    public String getToCityName() {
        return toCityName;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getOrderRequestId() {
        if(orderRequestId!=null && orderRequestId.trim().length()>0) {
            return orderRequestId;
        }else{
            return "0";
        }
    }

    public String getMaterialTypeId() {
        return materialTypeId;
    }

    public String getQuantity() {
        if(quantity!=null && quantity.trim().length()>0) {
            return quantity;
        }else{
            return "0";
        }
    }

    public String getInvoiceIds() {
        return invoiceIds;
    }

    public String getDispatchDate() {
        return dispatchDate;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverMobileNo() {
        return driverMobileNo;
    }

}
