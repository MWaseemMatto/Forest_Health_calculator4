package foerst.health.foresthealthcalculator4;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Model_id {
    @SerializedName("mobile_image_id")
    @Expose
    private Integer id;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("canopy")
    @Expose
    private String canopy;
    @SerializedName("trunk")
    @Expose
    private String trunk;
    @SerializedName("reference_object_circumference")
    @Expose
    private String width;
    @SerializedName("latitude")
    @Expose
    private String lat;
    @SerializedName("longitude")
    @Expose
    private String longi;
    @SerializedName("date_time")
    @Expose
    private String time;
    @SerializedName("android_id")
    @Expose
    private String and_id;

    public Model_id(Integer id, String image, String canopy, String trunk, String width, String lat, String longi, String time, String and_id) {
        this.id = id;
        this.image = image;
        this.canopy = canopy;
        this.trunk = trunk;
        this.width = width;
        this.lat = lat;
        this.longi = longi;
        this.time = time;
        this.and_id = and_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCanopy() {
        return canopy;
    }

    public void setCanopy(String canopy) {
        this.canopy = canopy;
    }

    public String getTrunk() {
        return trunk;
    }

    public void setTrunk(String trunk) {
        this.trunk = trunk;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAnd_id() {
        return and_id;
    }

    public void setAnd_id(String and_id) {
        this.and_id = and_id;
    }
}
