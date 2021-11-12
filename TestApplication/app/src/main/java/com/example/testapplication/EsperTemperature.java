package com.example.testapplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
//import com.espertech.esper.client.*;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.util.EventRenderer;
import com.espertech.esper.core.service.ConfiguratorContext;
import com.espertech.esper.core.service.EPServiceProviderImpl;
import com.espertech.esper.core.service.EPServiceProviderSPI;
import com.espertech.esper.util.JavaClassHelper;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Hello world!
 *
 */
public class EsperTemperature {
    public static class Temperature {
        Integer price;
        Date timeStamp;

        public Temperature(int p, long t) {
            price = p;
            timeStamp = new Date(t);
        }
        public Integer getPrice() {return price;}
        public Date getTimeStamp() {return timeStamp;}

        @Override
        public String toString() {
            return "Temperature: " + price.toString() + ", Time: " + timeStamp.toString();
        }
    }

    private static Random generator=new Random();

    public static void GenerateRandomTick(EPRuntime cepRT, int temp_price){
        //double price = (double) generator.nextInt(60);
        int price = temp_price;
        long timeStamp = System.currentTimeMillis();
        Temperature tick= new Temperature(price,timeStamp);
        System.out.println("Sending temperature: " + tick);
        cepRT.sendEvent(tick);
    }
    
    public static class CEPListener implements UpdateListener {
        static int i=0;
        public void update(EventBean[] newData, EventBean[] oldData) {
            //Double temperature = (double) newData[0].get("price");
            //System.out.println(String.format("Name: %s, Age: %d", name, age));
            System.out.println("Event received: "
                    + newData[0].getUnderlying());
            //EventBean event = newData[0];
            //System.out.println("Temperature=" + event.get("price"));
            //int i;
            if(i==0) {
                int temp = (int) newData[0].get("price");
                SendNotification.sendDeviceNotification(temp);  //na steilw thermokrasia
                i++;
            }
        }
    }

    public static void checkTemperatureEvents(ArrayList<Integer> temperatures) {
        System.out.println("Skata sta moutra");
        //The Configuration is meant only as an initialization-time object.
        //Configuration cepConfig = new Configuration();
        // We register Ticks as objects the engine will have to handle
        //ei- cepConfig.addEventType("TemperatureEvent",Temperature.class.getName());
        // EPServiceProvider epServiceProvider = EPServiceProviderManager.getDefaultProvider();
        // epServiceProvider.getEPAdministrator().getConfiguration().addEventType("StartEvent", Tick.class);
        try {
            // We setup the engine
            Configuration config = new Configuration();
            EPServiceProvider cep = EPServiceProviderManager.getDefaultProvider(config);
            //cep.initialize();
            cep.getEPAdministrator().getConfiguration().addEventType("TemperatureEvent", Temperature.class);
            EPRuntime cepRT = cep.getEPRuntime();

            // We register an EPL statement (Query)
            //EPAdministrator cepAdm = cep.getEPAdministrator();
            EPStatement cepStatement = cep.getEPAdministrator().createEPL("select * from TemperatureEvent " +
                    "where price > 40");

            //Attach a listener to the statement
            cepStatement.addListener(new CEPListener());

            //Generate random values
            for(int temp : temperatures) {
                //System.out.print("Temperatureeeeee: "+temp + ", ");
                GenerateRandomTick(cepRT, temp);

            }
            /*for (int i = 0; i < 10; i++) {
                GenerateRandomTick(cepRT);
            }*/

            //cep.destroy();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
