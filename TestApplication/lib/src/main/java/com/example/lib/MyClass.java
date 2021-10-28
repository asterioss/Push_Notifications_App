package com.example.lib;

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
import com.espertech.esper.client.context.*;
/**
 * Hello world!
 *
 */
public class MyClass {
    public static class Temperature {
        String symbol;
        Double price;
        Date timeStamp;

        public Temperature(String s, double p, long t) {
            symbol = s;
            price = p;
            timeStamp = new Date(t);
        }
        public double getPrice() {return price;}
        public String getSymbol() {return symbol;}
        public Date getTimeStamp() {return timeStamp;}

        @Override
        public String toString() {
            return "Temperature: " + price.toString() + ", Time: " + timeStamp.toString();
        }
    }

    private static Random generator=new Random();

    public static void GenerateRandomTick(EPRuntime cepRT){
        double price = (double) generator.nextInt(60);
        long timeStamp = System.currentTimeMillis();
        String symbol = "AAPL";
        Temperature tick= new Temperature(symbol,price,timeStamp);
        System.out.println("Sending temperature: " + tick);
        cepRT.sendEvent(tick);
    }

    public static class CEPListener implements UpdateListener {
        public void update(EventBean[] newData, EventBean[] oldData) {
            //String pricere = (double) newData[0].get("price");
            //int age = (int) newData[0].get("age");
            //System.out.println(String.format("Name: %s, Age: %d", name, age));
            System.out.println("Event received: "
                    + newData[0].getUnderlying());
        }
    }

    public static void main(String args[]) {
        //The Configuration is meant only as an initialization-time object.
        //Configuration cepConfig = new Configuration();
        // We register Ticks as objects the engine will have to handle
        //ei- cepConfig.addEventType("TemperatureEvent",Temperature.class.getName());
        // EPServiceProvider epServiceProvider = EPServiceProviderManager.getDefaultProvider();
        // epServiceProvider.getEPAdministrator().getConfiguration().addEventType("StartEvent", Tick.class);
        // We setup the engine
        EPServiceProvider cep = EPServiceProviderManager.getDefaultProvider();
        //cep.initialize();
        cep.getEPAdministrator().getConfiguration().addEventType("TemperatureEvent", Temperature.class);
        EPRuntime cepRT = cep.getEPRuntime();

        // We register an EPL statement (Query)
        //EPAdministrator cepAdm = cep.getEPAdministrator();
        EPStatement cepStatement = cep.getEPAdministrator().createEPL("select * from TemperatureEvent " +
                "where price > 40.0");

        //Attach a listener to the statement
        cepStatement.addListener(new CEPListener());

        for (int i = 0; i < 5; i++) { GenerateRandomTick(cepRT); }
        //GenerateRandomTick(cepRT);
        //System.out.println( "Hello World!" );
    }


}