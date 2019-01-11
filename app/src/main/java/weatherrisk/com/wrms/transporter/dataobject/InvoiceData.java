package weatherrisk.com.wrms.transporter.dataobject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WRMS on 11-04-2016.
 */
public class InvoiceData  implements Parcelable {

    private String sno;
    private String invoiceTempId;
    private String invoiceNumber;
    private String date;
    private String invoiceAmount;
    private String invoiceRemark;
    private String invoicePath;

    public InvoiceData(){

    }

    public InvoiceData(Parcel in) {
        String[] data = new String[7];
        in.readStringArray(data);
        this.sno = data[0];
        this.invoiceTempId = data[1];
        this.invoiceNumber = data[2];
        this.date = data[3];
        this.invoiceAmount = data[4];
        this.invoiceRemark = data[5];
        this.invoicePath = data[6];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.sno,
                this.invoiceTempId,
                this.invoiceNumber,
                this.date,
                this.invoiceAmount,
                this.invoiceRemark,
                this.invoicePath
        });
    }

    public static final Parcelable.Creator<InvoiceData> CREATOR = new Parcelable.Creator<InvoiceData>() {

        @Override
        public InvoiceData createFromParcel(Parcel source) {
            return new InvoiceData(source); // using parcelable constructor
        }

        @Override
        public InvoiceData[] newArray(int size) {
            return new InvoiceData[size];
        }
    };


    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceRemark() {
        return invoiceRemark;
    }

    public void setInvoiceRemark(String invoiceRemark) {
        this.invoiceRemark = invoiceRemark;
    }

    public String getInvoicePath() {
        return invoicePath;
    }

    public void setInvoicePath(String invoicePath) {
        this.invoicePath = invoicePath;
    }

    public String getInvoiceTempId() {
        return invoiceTempId;
    }

    public void setInvoiceTempId(String invoiceTempId) {
        this.invoiceTempId = invoiceTempId;
    }
}
