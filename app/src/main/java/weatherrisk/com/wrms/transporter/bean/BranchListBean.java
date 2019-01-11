package weatherrisk.com.wrms.transporter.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 10-05-2017.
 * <p/>
 * "serial": 2,
 * "firm_name": "lucknow branch",
 * "contact_name": "9879879876",
 * "personal_contact_no": "9879879876",
 * "home_contact_no": "9879879876",
 * "office_contact_no": "9879879876",
 * "tin_no": "asdsdfasddsfa",
 * "country_id": 1,
 * "state_id": 33,
 * "city_id": 2681,
 * "address": "afsdfds",
 * "email_id": "abc@gmail.com"
 */
public class BranchListBean implements Parcelable {

    private String serial;
    private String firm_name;
    private String contact_name;
    private String personal_contact_no;
    private String home_contact_no;
    private String office_contact_no;
    private String tin_no;
    private String country_id;
    private String state_id;
    private String city_id;
    private String address;
    private String email_id;
    private String stateName, cityName;

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getFirm_name() {
        return firm_name;
    }

    public void setFirm_name(String firm_name) {
        this.firm_name = firm_name;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getPersonal_contact_no() {
        return personal_contact_no;
    }

    public void setPersonal_contact_no(String personal_contact_no) {
        this.personal_contact_no = personal_contact_no;
    }

    public String getHome_contact_no() {
        return home_contact_no;
    }

    public void setHome_contact_no(String home_contact_no) {
        this.home_contact_no = home_contact_no;
    }

    public String getOffice_contact_no() {
        return office_contact_no;
    }

    public void setOffice_contact_no(String office_contact_no) {
        this.office_contact_no = office_contact_no;
    }

    public String getTin_no() {
        return tin_no;
    }

    public void setTin_no(String tin_no) {
        this.tin_no = tin_no;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public BranchListBean() {

    }

    public BranchListBean(Parcel in) {
        String[] data = new String[14];
        in.readStringArray(data);
        this.serial = data[0];
        this.firm_name = data[1];
        this.contact_name = data[2];
        this.personal_contact_no = data[3];
        this.home_contact_no = data[4];
        this.office_contact_no = data[5];
        this.tin_no = data[6];
        this.country_id = data[7];
        this.state_id = data[8];
        this.city_id = data[9];
        this.address = data[10];
        this.email_id = data[11];
        this.stateName = data[12];
        this.cityName = data[13];

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.serial,
                this.firm_name,
                this.contact_name,
                this.personal_contact_no,
                this.home_contact_no,
                this.office_contact_no,
                this.tin_no,
                this.country_id,
                this.state_id,
                this.city_id,
                this.address,
                this.email_id,
                this.stateName,
                this.cityName
        });
    }

    public static final Parcelable.Creator<BranchListBean> CREATOR = new Parcelable.Creator<BranchListBean>() {

        @Override
        public BranchListBean createFromParcel(Parcel source) {
            return new BranchListBean(source); // using parcelable constructor
        }

        @Override
        public BranchListBean[] newArray(int size) {
            return new BranchListBean[size];
        }
    };
}
