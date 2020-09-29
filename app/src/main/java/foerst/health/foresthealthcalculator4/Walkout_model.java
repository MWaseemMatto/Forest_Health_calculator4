package foerst.health.foresthealthcalculator4;

public class Walkout_model {
    private String service;
    private String details;
    private int icon;

    public Walkout_model(String service, String details, int icon) {
        this.service = service;
        this.details = details;
        this.icon = icon;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
