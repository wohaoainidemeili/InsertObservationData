package yuan.flood.Until;

import net.opengis.sensorML.x101.*;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.VectorType;
import org.apache.xmlbeans.XmlException;
import yuan.flood.Entity.Sensor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Yuan on 2017/3/24.
 */
public class DecodeSOS {
    public Sensor decodeDescribeSensor(String sensorXML) throws XmlException {
        Sensor sensor=new Sensor();
        //parse the station sensorML to get the sensor name under the station
        SensorMLDocument parseSationDocument=SensorMLDocument.Factory.parse(sensorXML);
        SensorMLDocument.SensorML sensorML1=parseSationDocument.getSensorML();
        AbstractProcessType xb_prc=parseSationDocument.getSensorML().getMemberArray(0).getProcess();
        SystemType staSystem=(SystemType)xb_prc;

        //get sensor id and sensor name
        IdentificationDocument.Identification.IdentifierList.Identifier[] identifiers= staSystem.getIdentificationArray(0).getIdentifierList().getIdentifierArray();
        for (int i=0;i<identifiers.length;i++){
            if (identifiers[i].getTerm().getDefinition().equalsIgnoreCase("urn:ogc:def:identifier:OGC:1.0:uniqueID")
                    ||identifiers[i].getTerm().getDefinition().equalsIgnoreCase("urn:ogc:def:identifier:OGC:uniqueID")){
                sensor.setSensorID(identifiers[i].getTerm().getValue());
            }
        }

        //get lat and lon for foi
        if (staSystem.getPosition()!=null) {
            PositionType positionType = staSystem.getPosition().getPosition();
            sensor.setSrsName(positionType.getReferenceFrame());
            //get latitude and longtitude
            VectorType.Coordinate[] coordinates = positionType.getLocation().getVector().getCoordinateArray();
            double x=0;
            double y=0;
            for (VectorType.Coordinate coordinate : coordinates) {
                String axisID = coordinate.getQuantity().getAxisID();
                if (axisID.equalsIgnoreCase("x")) x=coordinate.getQuantity().getValue();
                else if (axisID.equalsIgnoreCase("y")) y=coordinate.getQuantity().getValue();
            }
           sensor.setFoiPositon(y+" "+x);
        }
        //foi
        String sensorID=sensor.getSensorID();
        String[] sensorEles= sensorID.split(":");
        sensor.setFoiID(sensorEles[sensorEles.length-1]);
        sensor.setFoiName(sensorEles[sensorEles.length-1]);
        sensor.setFoiDesc("point");

        //get ObservationProperties
        //get the sensor property and every property is set by quantity class

        IoComponentPropertyType[] properties =staSystem.getOutputs().getOutputList().getOutputArray();
        for (IoComponentPropertyType property:properties){
            //get the property ID
            String propertyID=property.getQuantity().getDefinition();
            //get property unit
            String propertyUnit=property.getQuantity().getUom().getCode();
            sensor.setPropertyID(propertyID);
            sensor.setUom(propertyUnit);
        }

        return sensor;
    }

}
