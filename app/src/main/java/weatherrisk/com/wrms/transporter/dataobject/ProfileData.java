package weatherrisk.com.wrms.transporter.dataobject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;

/**
 * Created by WRMS on 23-04-2016.
 */
public class ProfileData implements Parcelable {


    private String accountId;
    private String accountName;
    private String contactName;
    private String personContactNo;
    private String officeContactNo;
    private String tinNo;
    private String address;
    private String email;
    private String stateId;
    private String cityId;
    private String stateName;
    private String cityName;
    private String homeContactNo;
    private String doNoCall;
    private String bankName;
    private String bankAccountNo;
    private String bankBranchName;
    private String bankBranchCode;
    private String remark;
    private String panNo;

    public ProfileData(){

    }

    public boolean save(DBAdapter db, Context context){
        boolean isSaved = false;

        ContentValues values = new ContentValues();
        values.put(DBAdapter.FIRM_ID,getAccountId(context));
        values.put(DBAdapter.FIRM_NAME,getAccountName());
        values.put(DBAdapter.CONTACT_NAME,getContactName());
        values.put(DBAdapter.PERSON_CONTACT_NO,getPersonContactNo());
        values.put(DBAdapter.HOME_CONTACT_NO,getHomeContactNo());
        values.put(DBAdapter.OFFICE_CONTACT_NO,getOfficeContactNo());
        values.put(DBAdapter.TIN_NO,getTinNo());
        values.put(DBAdapter.PAN_NO,getPanNo());
        values.put(DBAdapter.ADDRESS,getAddress());
        values.put(DBAdapter.EMAIL,getEmail());
        values.put(DBAdapter.DO_NO_CALL,getDoNoCall());
        values.put(DBAdapter.BANK_NAME,getBankName());
        values.put(DBAdapter.BANK_ACCOUNT_NO,getBankAccountNo());
        values.put(DBAdapter.BANK_BRANCH_NAME,getBankBranchName());
        values.put(DBAdapter.BANK_BRANCH_CODE,getBankBranchCode());
        values.put(DBAdapter.REMARK,getRemark());
        values.put(DBAdapter.STATE_ID,getStateId());
        values.put(DBAdapter.CITY_ID,getCityId());

        Cursor accountCursor = db.getProfileByAccountId(getAccountId(context));
        Log.i("AccountId",String.valueOf(getAccountId(context).length()));
        long k = -1;
        if(accountCursor.moveToFirst()){
            k = db.db.update(DBAdapter.TABLE_PROFILE,values,DBAdapter.FIRM_ID+" = '"+getAccountId(context)+"'",null);
            Log.i("Update",String.valueOf(k));
        }else{
            k = db.db.insert(DBAdapter.TABLE_PROFILE,null,values);
            Log.i("Insert",String.valueOf(k));
        }
        if(k!=-1){
            isSaved = true;
        }

        return isSaved;
    }

    protected ProfileData(Parcel in) {
        accountId = in.readString();
        accountName = in.readString();
        contactName = in.readString();
        personContactNo = in.readString();
        officeContactNo = in.readString();
        tinNo = in.readString();
        address = in.readString();
        email = in.readString();
        stateId = in.readString();
        cityId = in.readString();
        stateName = in.readString();
        cityName = in.readString();
        homeContactNo = in.readString();
        doNoCall = in.readString();
        bankName = in.readString();
        bankAccountNo = in.readString();
        bankBranchName = in.readString();
        bankBranchCode = in.readString();
        remark = in.readString();
        panNo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(accountId);
        dest.writeString(accountName);
        dest.writeString(contactName);
        dest.writeString(personContactNo);
        dest.writeString(officeContactNo);
        dest.writeString(tinNo);
        dest.writeString(address);
        dest.writeString(email);
        dest.writeString(stateId);
        dest.writeString(cityId);
        dest.writeString(stateName);
        dest.writeString(cityName);
        dest.writeString(homeContactNo);
        dest.writeString(doNoCall);
        dest.writeString(bankName);
        dest.writeString(bankAccountNo);
        dest.writeString(bankBranchName);
        dest.writeString(bankBranchCode);
        dest.writeString(remark);
        dest.writeString(panNo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProfileData> CREATOR = new Creator<ProfileData>() {
        @Override
        public ProfileData createFromParcel(Parcel in) {
            return new ProfileData(in);
        }

        @Override
        public ProfileData[] newArray(int size) {
            return new ProfileData[size];
        }
    };

    SharedPreferences prefs;

    public String getAccountId(Context ctx) {
        if(prefs == null){
            prefs = ctx.getSharedPreferences(AppController.ACCOUNT_PREFRENCE,ctx.MODE_PRIVATE);
        }
        accountId = prefs.getString(AppController.PREFERENCE_USER_ID,"");
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        /*if(prefs == null){
            prefs = ctx.getSharedPreferences(AppConstant.Preference.APP_PREFERENCE,ctx.MODE_PRIVATE);
        }
        accountName = prefs.getString(AppConstant.Preference.ACCOUNT_NAME,"");*/
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getContactName() {
        return contactName == null ? "" : contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPersonContactNo() {
        return personContactNo == null ? "" : personContactNo;
    }

    public void setPersonContactNo(String personContactNo) {
        this.personContactNo = personContactNo;
    }

    public String getHomeContactNo() {
        return homeContactNo == null ? "" : homeContactNo;
    }

    public void setHomeContactNo(String homeContactNo) {
        this.homeContactNo = homeContactNo;
    }

    public String getOfficeContactNo() {
        return officeContactNo == null ? "" : officeContactNo;
    }

    public void setOfficeContactNo(String officeContactNo) {
        this.officeContactNo = officeContactNo;
    }

    public String getTinNo() {
        return tinNo == null ? "" : tinNo;
    }

    public void setTinNo(String tinNo) {
        this.tinNo = tinNo;
    }

    public String getAddress() {
        return address == null ? "" : address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStateId() {
        return stateId == null ? "" : stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getCityId() {
        return cityId == null ? "" : cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getStateName() {
        return stateName == null ? "" : stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public void setStateId(String stateId, DBAdapter db) {
        this.stateId = stateId;
        if(stateId!=null && stateId.trim().length()>0){
            Cursor stateById = db.stateById(stateId);
            if(stateById.moveToFirst()) {
                String stateName = stateById.getString(stateById.getColumnIndex(DBAdapter.STATE_NAME));
                this.stateName = stateName;
            }
            stateById.close();
        }else{
            this.stateName = "";
        }
    }

    public void setCityId(String CityId, DBAdapter db) {
        this.cityId = CityId;
        if(CityId!=null && CityId.trim().length()>0){
            Cursor cityById = db.cityById(CityId);
            if(cityById.moveToFirst()) {
                String cityName = cityById.getString(cityById.getColumnIndex(DBAdapter.CITY_NAME));
                this.cityName = cityName;
            }
            cityById.close();
        }else{
            this.cityName = "";
        }
    }


    public String getCityName() {
        return cityName == null ? "" : cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDoNoCall() {
        return doNoCall == null ? "1" : doNoCall;
    }

    public void setDoNoCall(String doNoCall) {
        this.doNoCall = doNoCall;
    }

    public String getBankName() {
        return bankName == null ? "" : bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountNo() {
        return bankAccountNo == null ? "" : bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public String getBankBranchName() {
        return bankBranchName == null ? "" : bankBranchName;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public String getBankBranchCode() {
        return bankBranchCode == null ? "" : bankBranchCode;
    }

    public void setBankBranchCode(String bankBranchCode) {
        this.bankBranchCode = bankBranchCode;
    }

    public String getRemark() {
        return remark == null ? "" : remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }


}

