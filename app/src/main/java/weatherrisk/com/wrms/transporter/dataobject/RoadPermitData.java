package weatherrisk.com.wrms.transporter.dataobject;

/**
 * Created by WRMS on 15-04-2016.
 */
public class RoadPermitData {

    private String sno;
    private String roadPermitRemark;
    private String roadPermitReturnId;
    private String roadPermitImageString;


    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getRoadPermitRemark() {
        return roadPermitRemark;
    }

    public void setRoadPermitRemark(String roadPermitRemark) {
        this.roadPermitRemark = roadPermitRemark;
    }

    public String getRoadPermitReturnId() {
        return roadPermitReturnId;
    }

    public void setRoadPermitReturnId(String roadPermitReturnId) {
        this.roadPermitReturnId = roadPermitReturnId;
    }

    public String getRoadPermitImageString() {
        return roadPermitImageString;
    }

    public void setRoadPermitImageString(String roadPermitImageString) {
        this.roadPermitImageString = roadPermitImageString;
    }
}
