package weatherrisk.com.wrms.transporter.dataobject;

/**
 * Created by Admin on 04-04-2017.
 */
public class RoadPermitBean {

    String permitID;
    String createDate;
    String remark;
    String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPermitID() {
        return permitID;
    }

    public void setPermitID(String permitID) {
        this.permitID = permitID;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
