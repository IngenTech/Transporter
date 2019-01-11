package weatherrisk.com.wrms.transporter.dataobject;

import android.database.Cursor;

import org.json.JSONObject;

import java.util.ArrayList;

import weatherrisk.com.wrms.transporter.adapter.DBAdapter;

/**
 * Created by Admin on 05-12-2016.
 */

public class CustomerRunningTripData {
    String docketNo;
    String fromCity;
    String fromCityName;
    String toCity;
    String toCityName;
    String orderDate;
    String dispatchDate;

    String doorStatus;
    String refrigeratedStatus;
    String rate;
    String expectedArrival;
    String transporterName;
    String contactNo;
    String vehicleNo;
    String latitude;
    String longitude;
    String vehicleRunningStatus;
    String tripId;

    String imei;
    String vehicleID;
    String tripDate;
    String speed;
    String lastHaltTime;



    public ArrayList<ContentData> getContentData(){

        ArrayList<ContentData> contentDatas = new ArrayList<>();

        contentDatas.add(ContentData.getContentData("Transporter Name",getTransporterName()));
        contentDatas.add(ContentData.getContentData("Transporter Contact No.",getContactNo()));
        contentDatas.add(ContentData.getContentData("Vehicle No.",getVehicleNo()));
        contentDatas.add(ContentData.getContentData("From City",getFromCityName()));
        contentDatas.add(ContentData.getContentData("To City",getToCityName()));
        contentDatas.add(ContentData.getContentData("Order Date",getOrderDate()));
        contentDatas.add(ContentData.getContentData("Dispatch Date",getDispatchDate()));
        contentDatas.add(ContentData.getContentData("Trip Date",getTripDate()));
        contentDatas.add(ContentData.getContentData("Expected Arrival",getExpectedArrival()));
        contentDatas.add(ContentData.getContentData("Speed",getSpeed()));
        contentDatas.add(ContentData.getContentData("Last Halt Time",getLastHaltTime()));
        contentDatas.add(ContentData.getContentData("Imei",getImei()));

        contentDatas.add(ContentData.getContentData("Trip Id",getTripId()));
        contentDatas.add(ContentData.getContentData("Docket No",getDocketNo()));
        contentDatas.add(ContentData.getContentData("Rate",getRate()+" / KM."));

        contentDatas.add(ContentData.getContentData("Vehicle Id",getVehicleID()));
        contentDatas.add(ContentData.getContentData("Door Status",getDoorStatus().contains("0") ? "Door Open" : "Door Close"));
        contentDatas.add(ContentData.getContentData("Refrigerated Status",getRefrigeratedStatus().contains("0") ? " Not Refrigerated" : "Refrigerated"));

        contentDatas.add(ContentData.getContentData("Vehicle Running Status",getVehicleRunningStatus()));

        return contentDatas;
    }

    public CustomerRunningTripData(JSONObject jsonString, DBAdapter db) {
        try {

            this.tripId = jsonString.getString("TripId");
            this.docketNo = jsonString.getString("DocketNo");
            this.fromCity = jsonString.getString("FromCityId");
            this.toCity = jsonString.getString("ToCityId");
            this.transporterName = jsonString.getString("TransporterName");
            this.orderDate = jsonString.getString("OrderDate");
            this.dispatchDate = jsonString.getString("DispatchDate");
           // this.material = jsonString.getString("MaterialId");
            this.doorStatus = jsonString.getString("DoorStatus");
            this.refrigeratedStatus = jsonString.getString("RefrigeratedStatus");


            this.rate = jsonString.getString("Rate");
            this.expectedArrival = jsonString.getString("ExpectedArrival");
            this.contactNo = jsonString.getString("TransporterContactNo");
            this.vehicleNo = jsonString.getString("VehicleNo");
            this.vehicleRunningStatus = jsonString.getString("VehicleRunningStatus");

            Cursor cursor = db.cityById(fromCity);
            if (cursor.moveToFirst()) {
                this.fromCityName = cursor.getString(cursor.getColumnIndex(DBAdapter.CITY_NAME));
            }
            cursor.close();

            Cursor cursor1 = db.cityById(toCity);
            if (cursor1.moveToFirst()) {
                this.toCityName = cursor1.getString(cursor1.getColumnIndex(DBAdapter.CITY_NAME));
            }
            cursor1.close();

            this.vehicleID = jsonString.getString("VehicleId");
            this.imei = jsonString.getString("IMEI");
            this.tripDate = jsonString.getString("TripDate");
            this.speed = jsonString.getString("speed");
            this.lastHaltTime = jsonString.getString("lastHaltTime");




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDocketNo() {
        return docketNo;
    }

    public String getFromCity() {
        return fromCity;
    }

    public String getFromCityName() {
        return fromCityName;
    }

    public String getToCity() {
        return toCity;
    }

    public String getToCityName() {
        return toCityName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getDispatchDate() {
        return dispatchDate;
    }



    public String getDoorStatus() {
        return doorStatus;
    }

    public String getRefrigeratedStatus() {
        return refrigeratedStatus;
    }

    public String getRate() {
        return rate;
    }

    public String getExpectedArrival() {
        return expectedArrival;
    }

    public String getTransporterName() {
        return transporterName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getVehicleRunningStatus() {
        return vehicleRunningStatus;
    }

    public String getTripId() {
        return tripId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getLastHaltTime() {
        return lastHaltTime;
    }

    public void setLastHaltTime(String lastHaltTime) {
        this.lastHaltTime = lastHaltTime;
    }

    /* public String getInvoiceList() {
        return invoiceList;
    }*/
}
