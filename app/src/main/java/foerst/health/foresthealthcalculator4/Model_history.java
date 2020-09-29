package foerst.health.foresthealthcalculator4;

public class Model_history {
    private String img;
    private String time;
    private String imageid;

    public Model_history(String img, String time, String imageid) {
        this.img = img;
        this.time = time;
        this.imageid = imageid;
    }

    public Model_history() {

    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }
}
