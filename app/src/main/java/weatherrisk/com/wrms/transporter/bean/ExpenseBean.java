package weatherrisk.com.wrms.transporter.bean;

/**
 * Created by Admin on 06-05-2017.
 *
 */
public class ExpenseBean {

    private String id;
    private String expense_amount;
    private String expense_detail;
    private String expenseTypeID;
    private String bill_image;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpense_amount() {
        return expense_amount;
    }

    public void setExpense_amount(String expense_amount) {
        this.expense_amount = expense_amount;
    }

    public String getExpense_detail() {
        return expense_detail;
    }

    public void setExpense_detail(String expense_detail) {
        this.expense_detail = expense_detail;
    }

    public String getExpenseTypeID() {
        return expenseTypeID;
    }

    public void setExpenseTypeID(String expenseTypeID) {
        this.expenseTypeID = expenseTypeID;
    }

    public String getBill_image() {
        return bill_image;
    }

    public void setBill_image(String bill_image) {
        this.bill_image = bill_image;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    private String billDate;
    private String paidBy;
    private String remarks;
}
