package yuan.flood.Entity;

/**
 * Created by Yuan on 2017/3/24.
 */
public class Sensor {
    String sensorID;
    String propertyID;
    String uom;
    String foiID;
    String foiName;
    String foiDesc;
    String foiPositon;
    String srsName;

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getFoiID() {
        return foiID;
    }

    public void setFoiID(String foiID) {
        this.foiID = foiID;
    }

    public String getFoiName() {
        return foiName;
    }

    public void setFoiName(String foiName) {
        this.foiName = foiName;
    }

    public String getFoiDesc() {
        return foiDesc;
    }

    public void setFoiDesc(String foiDesc) {
        this.foiDesc = foiDesc;
    }

    public String getFoiPositon() {
        return foiPositon;
    }

    public void setFoiPositon(String foiPositon) {
        this.foiPositon = foiPositon;
    }

    public String getSrsName() {
        return srsName;
    }

    public void setSrsName(String srsName) {
        this.srsName = srsName;
    }
}
