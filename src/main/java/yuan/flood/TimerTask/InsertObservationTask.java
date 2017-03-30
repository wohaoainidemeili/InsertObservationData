package yuan.flood.TimerTask;


import net.opengis.sos.x10.DescribeSensorDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.util.web.HttpClientException;
import yuan.flood.Entity.Sensor;
import yuan.flood.Until.DecodeSOS;
import yuan.flood.Until.IOHelper_01;
import yuan.flood.Until.SOSAdapter_01;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Yuan on 2017/3/22.
 */
public class InsertObservationTask extends TimerTask {
    public String sos_url="http://localhost:8080/SOS//sos";
    //multiple thread for getting data
    private double[][] data=null;
    private List<Sensor> sensors=new ArrayList<Sensor>();// for store sensor info
    private int count=0;
    private ExecutorService executorService= Executors.newFixedThreadPool(4);
    private SOSAdapter_01 sosAdapter_01=new SOSAdapter_01("1.0.0");
    private DecodeSOS decodeSOS=new DecodeSOS();
    public InsertObservationTask() {
        //read config and get sensor information
        int sensorNum=0;
        try {
            BufferedReader sensorReader=new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("Sensors.txt")));
            BufferedReader dataReader=new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("data.csv")));
            while (sensorReader.readLine()!=null){
                sensorNum++;
            }
            sensorReader.close();
            sensorReader=new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("Sensors.txt")));
            String eles;
            while ((eles=sensorReader.readLine())!=null){
                Sensor sensor;
//                ParameterContainer param=new ParameterContainer();
//                Operation getCapOperation = new Operation(SOSAdapter_01.DESCRIBE_SENSOR, this.sos_url + "?", this.sos_url);

                try {
//                    param.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_VERSION_PARAMETER,"1.0.0");
//                    param.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_SERVICE_PARAMETER,"SOS");
//                    param.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_PARAMETER,eles);
//                    param.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_OUTPUT_FORMAT,"text/xml;subtype=\"sensorML/1.0.1\"");
//                    OperationResult operationResult=sosAdapter_01.doOperation(getCapOperation,param);
//                    SensorMLDocument sensorMLDocument= (SensorMLDocument) replaceSpecialCharacters(SensorMLDocument.Factory.parse(operationResult.getIncomingResultAsStream()));
                    net.opengis.sensorML.x101.SensorMLDocument sensorMLDocument=getSensorML(eles);
                    sensor=decodeSOS.decodeDescribeSensor(sensorMLDocument.xmlText());
                    sensors.add(sensor);
                } catch (XmlException e) {
                    e.printStackTrace();
                }
            }
            sensorReader.close();
            //read data
            data=new double[sensorNum][365];
            for (int i=0;i<sensorNum;i++){
                String[] dataStrs= dataReader.readLine().split(",");
                for (int j=0;j<365;j++){
                    data[i][j]=Double.valueOf(dataStrs[j]);
                }
            }
            dataReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //read data information and store it in task

    }
    @Override
    public void run() {
        if (count<365) {
            //create multi thread to create insertobservation xml and insert it
            for (int i = 0; i < sensors.size(); i++) {
                InsertObservationThread insertObservationThread = new InsertObservationThread(sensors.get(i), this.sos_url);
                insertObservationThread.setData(data[i][count]);
                if (!executorService.isShutdown()) {
                    executorService.execute(insertObservationThread);
                }
            }
            count++;
        }else {
            System.out.println("finished inserting data!");
            Thread.interrupted();
        }
    }
    public net.opengis.sensorML.x101.SensorMLDocument getSensorML(String procedure){
        // create request
        DescribeSensorDocument descSensDoc = DescribeSensorDocument.Factory.newInstance();
        DescribeSensorDocument.DescribeSensor descSens = descSensDoc.addNewDescribeSensor();
        descSens.setProcedure(procedure);
        descSens.setService("SOS");
        descSens.setVersion("1.0.0");
        descSens.setOutputFormat("text/xml;subtype=\"sensorML/1.0.1\"");

        // send request
        XmlObject response = null;
        try {
            response = sendRequest(descSensDoc);
        } catch (IOException e) {
           e.getMessage();
        }
        // parse request to SensorML Document
        // FIXME add try catch if response is not SensorML
        try {
            net.opengis.sensorML.x101.SensorMLDocument sensorML = (net.opengis.sensorML.x101.SensorMLDocument) response;
            return sensorML;
        } catch (Exception e) {
           e.getMessage();
        }
        return null;
    }
    private XmlObject sendRequest(XmlObject request) throws IOException {
        XmlObject response = null;
        String requestString = request.xmlText();


        try {
            InputStream responseIS = IOHelper_01.sendPostMessage(this.sos_url, requestString);
            response = replaceSpecialCharacters(XmlObject.Factory.parse(responseIS));
        } catch (XmlException e) {
           e.getMessage();
        }
        catch (HttpClientException e) {
           e.getMessage();
        }

        return response;
    }
    private XmlObject replaceSpecialCharacters(XmlObject xmlObject) {
        String tempStr = xmlObject.toString();
        tempStr = tempStr.replace("?", "Ae");
        tempStr = tempStr.replace("?", "Oe");
        tempStr = tempStr.replace("?", "Ue");
        tempStr = tempStr.replace("?", "ae");
        tempStr = tempStr.replace("?", "oe");
        tempStr = tempStr.replace("��", "ue");
        tempStr = tempStr.replace("?", "ss");
        tempStr = tempStr.replace("?", "'");
        tempStr = tempStr.replace("`", "'");
        try {
            return XmlObject.Factory.parse(tempStr);
        } catch (XmlException e) {
           e.getMessage();
        }
        return null;
    }
}
