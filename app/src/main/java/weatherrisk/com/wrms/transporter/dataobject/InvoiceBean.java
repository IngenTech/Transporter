package weatherrisk.com.wrms.transporter.dataobject;

/**
 * Created by Admin on 04-04-2017.
 *
 * "InvoiceId": "101",
 "InvoiceNo": "3747757",
 "Amount": "6000",
 "InvoiceDate": "2017-03-31 00:00:00",
 "Materials": [
 {
 "MaterialId": 5,
 "MaterialName": "ctbynyn",
 "Amount": 3000
 },
 {
 "MaterialId": 6,
 "MaterialName": "tvuhig",
 "Amount": 3000
 }
 ]
 }
 */
public class InvoiceBean {

    String invoiceID;
    String invoiceNo;
    String amount;
    String invoiceDate;
    String noOFMatterial;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String image;

    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getNoOFMatterial() {
        return noOFMatterial;
    }

    public void setNoOFMatterial(String noOFMatterial) {
        this.noOFMatterial = noOFMatterial;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
}
