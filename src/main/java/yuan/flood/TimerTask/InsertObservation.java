package yuan.flood.TimerTask;

import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.ITime;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.valueDomains.time.TimePosition;
import yuan.flood.Until.SOSAdapter_01;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Yuan on 2017/3/22.
 */
public class InsertObservation {
    public static void main(String[] args) {
        Timer timer=new Timer();
        InsertObservationTask task=new InsertObservationTask();
        //excute the insertObservation task to insert observation
        timer.schedule(task,0,60000);

    }
    public  static void createInsertObservationXMLDemo(){
        //create observation xml
        ParameterContainer parameterContainer=new ParameterContainer();
        try {
            Operation getCapOperation = new Operation(SOSAdapter_01.INSERT_OBSERVATION,"http://localhost:8080/SOS//sos" ,"http://localhost:8080/SOS//sos?");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_VERSION_PARAMETER,"1.0.0");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_SERVICE_PARAMETER,"SOS");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_PROCEDURE_PARAMETER,"urn:liesmars:insitusensor:BaoxieHydrologicalStation-FY-KYC1");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_SENSOR_ID_PARAMETER,"urn:liesmars:insitusensor:BaoxieHydrologicalStation-FY-KYC1");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_TYPE,"measurement");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_SAMPLING_TIME,"2017-03-23T15:23:15+08:00");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_OBSERVED_PROPERTY_PARAMETER,"urn:ogc:def:property:OGC:1.0:waterLevel");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_VALUE_PARAMETER, String.valueOf(121));
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_VALUE_UOM_ATTRIBUTE,"m");
            //insert of poi
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_FOI_ID_PARAMETER,"sta-a001");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_NEW_FOI_NAME,"station a001");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_NEW_FOI_DESC,"test");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_NEW_FOI_POSITION,"30.535296 114.369321");
            parameterContainer.addParameterShell(ISOSRequestBuilder.INSERT_OBSERVATION_POSITION_SRS,"urn:ogc:def:crs:EPSG::4326");
            SOSAdapter_01 sosAdapter_01=new SOSAdapter_01("1.0.0");
            OperationResult operationResult= sosAdapter_01.doOperation(getCapOperation, parameterContainer);
            int x=0;

        } catch (OXFException e) {
            e.printStackTrace();
        } catch (ExceptionReport exceptionReport) {
            exceptionReport.printStackTrace();
        }
    }
    public static void createSensorML(){

    }
}
