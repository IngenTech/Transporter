package weatherrisk.com.wrms.transporter.dataobject;

import android.database.Cursor;

import org.json.JSONObject;

import java.util.ArrayList;

import weatherrisk.com.wrms.transporter.adapter.DBAdapter;

/**
 * Created by Admin on 02-12-2016.
 */

public class CustomerConfirmOrder {
    String docketNo;
    String transporterName;
    String contactName;
    String fromCityId;
    String toCityId;
    String toCityName;
    String fromCityName;
    String tripDate;

    String DoorStatus;
    String RefrigeratedStatus;
    String Rate;
    String TransporterContactNo;


    String OrderDate;
    String Latitude;
    String Longitude;
    String VehicleQuantity;
    String Capacity;
    String Remark;
    String CustomerName;
    String CustomerContactNo;

    ArrayList<InvoiceData> invoices = new ArrayList<>();


    public ArrayList<ContentData> getContentData(){

        ArrayList<ContentData> contentDatas = new ArrayList<>();

        contentDatas.add(ContentData.getContentData("Customer Name",getCustomerName()));
        contentDatas.add(ContentData.getContentData("Customer Contact No.",getCustomerContactNo()));
        contentDatas.add(ContentData.getContentData("Transporter Name",getTransporterName()));
        contentDatas.add(ContentData.getContentData("Transporter Contact No.",getTransporterContactNo()));
        contentDatas.add(ContentData.getContentData("From",getFromCityName()));
        contentDatas.add(ContentData.getContentData("To",getToCityName()));
        contentDatas.add(ContentData.getContentData("Order Date",getOrderDate()));

        contentDatas.add(ContentData.getContentData("Rate",getRate()+" / KM."));
        contentDatas.add(ContentData.getContentData("Capacity",getCapacity()));
        contentDatas.add(ContentData.getContentData("Vehicle Quantity",getVehicleQuantity()));
        contentDatas.add(ContentData.getContentData("Docket No",getDocketNo()));
        contentDatas.add(ContentData.getContentData("Door Status",getDoorStatus().contains("0") ? "Door Open" : "Door Close"));
        contentDatas.add(ContentData.getContentData("Refrigerated Status",getRefrigeratedStatus().contains("0") ? " Not Refrigerated" : "Refrigerated"));
        contentDatas.add(ContentData.getContentData("Remark",getRemark()));


        return contentDatas;
    }


    public CustomerConfirmOrder(JSONObject jsonString, DBAdapter db) {

        try {
            this.docketNo = jsonString.getString("DocketNo");
            this.OrderDate= jsonString.getString("OrderDate");
            this.tripDate = jsonString.getString("TripDate");
            this.DoorStatus= jsonString.getString("DoorStatus");
            this.RefrigeratedStatus= jsonString.getString("RefrigeratedStatus");


            this.Rate= jsonString.getString("Rate");
            this.fromCityId = jsonString.getString("FromCityID");
            this.toCityId = jsonString.getString("ToCityID");
            this.Capacity = jsonString.getString("Capacity");
            this.VehicleQuantity = jsonString.getString("VehicleQuantity");
            this.Remark = jsonString.getString("Remark");
            this.transporterName = jsonString.getString("TransporterName");
        //    this.contactName = jsonString.getString("ContactName");
            this.TransporterContactNo= jsonString.getString("TransporterContactNo");
            this.CustomerContactNo = jsonString.getString("CustomerContactNo");
            this.CustomerName = jsonString.getString("CustomerName");
            Cursor cursor = db.cityById(fromCityId);
            if (cursor.moveToFirst()) {
                this.fromCityName = cursor.getString(cursor.getColumnIndex(DBAdapter.CITY_NAME));
            }
            cursor.close();

            Cursor cursor1 = db.cityById(toCityId);
            if (cursor1.moveToFirst()) {
                this.toCityName = cursor1.getString(cursor1.getColumnIndex(DBAdapter.CITY_NAME));
            }
            cursor1.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setDocketNo(String docketNo) {
        this.docketNo = docketNo;
    }

    public String getVehicleQuantity() {
        return VehicleQuantity;
    }

    public void setVehicleQuantity(String vehicleQuantity) {
        VehicleQuantity = vehicleQuantity;
    }

    public String getCapacity() {
        return Capacity;
    }

    public void setCapacity(String capacity) {
        Capacity = capacity;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getDocketNo() {
        return docketNo;
    }

    public String getTransporterName() {
        return transporterName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getFromCityId() {
        return fromCityId;
    }

    public String getToCityId() {
        return toCityId;
    }

    public String getToCityName() {
        return toCityName;
    }

    public String getFromCityName() {
        return fromCityName;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void addInvoice(InvoiceData invoiceData){
        invoices.add(invoiceData);
    }

    public ArrayList<InvoiceData> getInvoices() {
        return invoices;
    }


    public String getDoorStatus() {
        return DoorStatus;
    }

    public String getRefrigeratedStatus() {
        return RefrigeratedStatus;
    }

    public String getRate() {
        return Rate;
    }

    public String getTransporterContactNo() {
        return TransporterContactNo;
    }



    public String getOrderDate() {
        return OrderDate;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }


    public void setTransporterName(String transporterName) {
        this.transporterName = transporterName;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCustomerContactNo() {
        return CustomerContactNo;
    }

    public void setCustomerContactNo(String customerContactNo) {
        CustomerContactNo = customerContactNo;
    }
}

