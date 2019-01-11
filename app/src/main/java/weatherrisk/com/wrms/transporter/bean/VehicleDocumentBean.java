package weatherrisk.com.wrms.transporter.bean;

/**
 * Created by Admin on 06-05-2017.
 */
public class VehicleDocumentBean {

    private String documentId;
    private String documentTitleId;
    private String image;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentTitleId() {
        return documentTitleId;
    }

    public void setDocumentTitleId(String documentTitleId) {
        this.documentTitleId = documentTitleId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
