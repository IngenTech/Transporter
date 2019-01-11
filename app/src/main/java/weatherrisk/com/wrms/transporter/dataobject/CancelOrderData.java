package weatherrisk.com.wrms.transporter.dataobject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.ArrayList;

import weatherrisk.com.wrms.transporter.adapter.DBAdapter;

/**
 * Created by Admin on 10-06-2017.
 */
public class CancelOrderData implements Parcelable {

    String docketNo;
    String orderDate;
    String refrigeratedStatus;
    String doorStatus;
    String fromCityId;
    String toCityId;
    String toCityName;
    String fromCityName;
    String tripDate;

    String roadPermitSttring;
    String transporterName;
    String customerName;
    String transporterNO;
    String customerNO;
    String rate,remark,vehicleQuantity,capacity;

    public CancelOrderData(JSONObject jsonString, DBAdapter db) {
        try {

            if (jsonString.has("DocketNo")) {
                this.docketNo = jsonString.getString("DocketNo");
            }
            if (jsonString.has("OrderDate")) {
                this.orderDate = jsonString.getString("OrderDate");
            }
            if (jsonString.has("RefrigeratedStatus")) {
                this.refrigeratedStatus = jsonString.getString("RefrigeratedStatus");
            }
            if (jsonString.has("DoorStatus")) {
                this.doorStatus = jsonString.getString("DoorStatus");
            }

            if (jsonString.has("FromCityID")) {
                this.fromCityId = jsonString.getString("FromCityID");
            }
            if (jsonString.has("ToCityID")) {
                this.toCityId = jsonString.getString("ToCityID");
            }
            if (jsonString.has("TripDate")) {
                this.tripDate = jsonString.getString("TripDate");
            }
            if (jsonString.has("Rate")) {
                this.rate = jsonString.getString("Rate");
            }
            if (jsonString.has("RoadPermits")) {
                this.roadPermitSttring = jsonString.getString("RoadPermits");
            }
            if (jsonString.has("TransporterName")) {

                this.transporterName = jsonString.getString("TransporterName");
            }

            if (jsonString.has("TransporterContactNo")) {

                this.transporterNO = jsonString.getString("TransporterContactNo");
            }

            if (jsonString.has("CustomerName")) {
                this.customerName = jsonString.getString("CustomerName");
            }
            if (jsonString.has("CustomerContactNo")) {
                this.customerNO = jsonString.getString("CustomerContactNo");
            }

            if (jsonString.has("Remark")) {
                this.remark = jsonString.getString("Remark");
            }

            if (jsonString.has("VehicleQuantity")) {
                this.vehicleQuantity = jsonString.getString("VehicleQuantity");
            }

            if (jsonString.has("Capacity")) {
                this.capacity = jsonString.getString("Capacity");
            }

            if (fromCityId!=null && fromCityId.length()>0) {
                Cursor cursor = db.cityById(fromCityId);
                if (cursor.moveToFirst()) {
                    this.fromCityName = cursor.getString(cursor.getColumnIndex(DBAdapter.CITY_NAME));
                }
                cursor.close();
            }

            if (toCityId!=null && toCityId.length()>0) {

                Cursor cursor1 = db.cityById(toCityId);
                if (cursor1.moveToFirst()) {
                    this.toCityName = cursor1.getString(cursor1.getColumnIndex(DBAdapter.CITY_NAME));
                }
                cursor1.close();
            }


        } catch (Exception e) {


            e.printStackTrace();
        }

    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getVehicleQuantity() {
        return vehicleQuantity;
    }

    public void setVehicleQuantity(String vehicleQuantity) {
        this.vehicleQuantity = vehicleQuantity;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public void setDocketNo(String docketNo) {
        this.docketNo = docketNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTransporterNO() {
        return transporterNO;
    }

    public void setTransporterNO(String transporterNO) {
        this.transporterNO = transporterNO;
    }

    public String getCustomerNO() {
        return customerNO;
    }

    public void setCustomerNO(String customerNO) {
        this.customerNO = customerNO;
    }

    public String getDocketNo() {
        return docketNo;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getRefrigeratedStatus() {
        return refrigeratedStatus;
    }

    public String getDoorStatus() {
        return doorStatus;
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

    public String getTransporterName(){return transporterName;}



    public CancelOrderData(Parcel in) {
        String[] data = new String[12];
        in.readStringArray(data);
        this.docketNo = data[0];
        this.orderDate = data[1];
        this.refrigeratedStatus = data[2];
        this.doorStatus = data[3];

        this.fromCityId = data[4];
        this.toCityId = data[5];
        this.toCityName = data[6];
        this.fromCityName = data[7];
        this.tripDate = data[8];

        this.roadPermitSttring = data[10];
        this.transporterName = data[11];


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.docketNo,
                this.orderDate,
                this.refrigeratedStatus,
                this.doorStatus,
                this.fromCityId,
                this.toCityId,
                this.toCityName,
                this.fromCityName,
                this.tripDate,
                this.roadPermitSttring,
                this.transporterName,
        });
    }

    public static final Creator<CustomerPendingOrder> CREATOR = new Creator<CustomerPendingOrder>() {

        @Override
        public CustomerPendingOrder createFromParcel(Parcel source) {
            return new CustomerPendingOrder(source); // using parcelable constructor
        }

        @Override
        public CustomerPendingOrder[] newArray(int size) {
            return new CustomerPendingOrder[size];
        }
    };


    public ArrayList<ContentData> getContentData(){

        ArrayList<ContentData> contentDatas = new ArrayList<>();

        contentDatas.add(ContentData.getContentData("Customer Name",getCustomerName()));
        contentDatas.add(ContentData.getContentData("Customer Contact No.",getCustomerNO()));
        contentDatas.add(ContentData.getContentData("Transporter Name",getTransporterName()));
        contentDatas.add(ContentData.getContentData("Transporter Contact No.",getTransporterNO()));

        contentDatas.add(ContentData.getContentData("From",getFromCityName()));
        contentDatas.add(ContentData.getContentData("To",getToCityName()));

        contentDatas.add(ContentData.getContentData("Order Date",getOrderDate()));
        contentDatas.add(ContentData.getContentData("Capacity",getCapacity()));
        contentDatas.add(ContentData.getContentData("Docket No",getDocketNo()));
        contentDatas.add(ContentData.getContentData("Door Status",getDoorStatus().contains("0") ? "Door Open" : "Door Close"));
        contentDatas.add(ContentData.getContentData("Refrigerated Status",getRefrigeratedStatus().contains("0") ? " Not Refrigerated" : "Refrigerated"));
        contentDatas.add(ContentData.getContentData("Rate",getRate()+" / KM."));

        contentDatas.add(ContentData.getContentData("Vehicle Quantity",getVehicleQuantity()));

        contentDatas.add(ContentData.getContentData("Remark",getRemark()));


        return contentDatas;
    }
}