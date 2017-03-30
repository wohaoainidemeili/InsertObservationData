package yuan.flood.TimerTask;

import net.opengis.sensorML.x10.SensorMLDocument;
import org.apache.xmlbeans.XmlException;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.ITime;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.valueDomains.time.TimePosition;
import yuan.flood.Entity.Sensor;
import yuan.flood.Until.DecodeSOS;
import yuan.flood.Until.SOSAdapter_01;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yuan on 2017/3/23.
 * get sensor information and insertobservation
 */
public class InsertObservationThread extends Thread{

    private String sosURL;
    private Sensor sensor;
    private double data;
    private SOSAdapter_01 sosAdapter_01=new SOSAdapter_01("1.0.0");
    private DecodeSOS decodeSOS=new DecodeSOS();
    public InsertObservationThread(Sensor sensor,String sosURL){
        this.sosURL=sosURL;
        this.sensor=sensor;
//        ParameterContainer param=new ParameterContainer();
//        Operation getCapOperation = new Operation(SOSAdapter_01.DESCRIBE_SENSOR, this.sosURL + "?", this.sosURL);
//
//        try {
//            param.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_VERSION_PARAMETER,"1.0.0");
//            param.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_SERVICE_PARAMETER,"SOS");
//            param.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_PARAMETER,sensorID);
//            param.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_OUTPUT_FORMAT,"text/xml;subtype=&quot;sensorML/1.0.1&quot;");
//            OperationResult operationResult=sosAdapter_01.doOperation(getCapOperation,param);
//            SensorMLDocument sensorMLDocument= SensorMLDocument.Factory.parse(operationResult.getIncomingResultAsStream());
//            sensor=decodeSOS.decodeDescribeSensor(sensorMLDocument.xmlText());
//        } catch (OXFException e) {
//            e.printStackTrace();
//        } catch (ExceptionReport exceptionReport) {
//            exceptionReport.printStackTrace();
//        } catch (XmlException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void run() {
        //create observation xml
        ParameterContainer parameterContainer=new ParameterContainer();
        try {
            Operation getCapOperation = new Operation(SOSAdapter_01.INSERT_OBSERVATION,this.sosURL ,this.sosURL+"?");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_VERSION_PARAMETER,"1.0.0");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_SERVICE_PARAMETER,"SOS");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_PROCEDURE_PARAMETER,sensor.getSensorID());
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_SENSOR_ID_PARAMETER,sensor.getSensorID());
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_TYPE,"measurement");
            //create time
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            String t= simpleDateFormat.format(new Date());
            String timeStr= t.replace("+0800", "+08:00");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_SAMPLING_TIME, timeStr);
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_OBSERVED_PROPERTY_PARAMETER,sensor.getPropertyID());
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_VALUE_PARAMETER,String.valueOf(data));
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_VALUE_UOM_ATTRIBUTE,sensor.getUom());
            //insert of poi
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_FOI_ID_PARAMETER,sensor.getFoiID());
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_NEW_FOI_NAME,sensor.getFoiName());
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_NEW_FOI_DESC,sensor.getFoiDesc());
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_NEW_FOI_POSITION,sensor.getFoiPositon());
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_POSITION_SRS,"urn:ogc:def:crs:EPSG::4326");
            OperationResult operationResult= sosAdapter_01.doOperation(getCapOperation, parameterContainer);
            System.out.println(timeStr+ ","+data);
        } catch (OXFException e) {
            e.printStackTrace();
        } catch (ExceptionReport exceptionReport) {
            exceptionReport.printStackTrace();
        }

    }
    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }
}
