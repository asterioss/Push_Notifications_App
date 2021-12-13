package com.example.testapplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPRuntime;
//import com.espertech.esper.client.*;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * Hello world!
 *
 */
public class EsperTemperature {
    private static int temperature;
    private static int humidity;
    private static String date_now;
    public static boolean check_hum;
    public static boolean check_temp;
    //private static boolean ckeck_temp;
    //static int ckeck_temp=0;
    //static int ckeck_hum=0;
    //private static boolean check_hum;
    //static int check_temp;
    //static int check_hum;

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
    public static class Humidity {
        Integer price;
        Date timeStamp;

        public Humidity(int p, long t) {
            price = p;
            timeStamp = new Date(t);
        }
        public Integer getPrice() {return price;}
        public Date getTimeStamp() {return timeStamp;}

        @Override
        public String toString() {
            return "Humidity: " + price.toString() + ", Time: " + timeStamp.toString();
        }
    }

    private static Random generator=new Random();

    public static void sendTemperature(EPRuntime cepRT, int temp_price){
        //double price = (double) generator.nextInt(60);
        int price = temp_price;
        long timeStamp = System.currentTimeMillis();
        Temperature tick= new Temperature(price,timeStamp);
        System.out.println("Sending temperature: " + tick);
        check_temp=true;
        cepRT.sendEvent(tick);
    }
    public static void sendHumidity(EPRuntime cepRT, int temp_price){
        //double price = (double) generator.nextInt(60);
        int price = temp_price;
        long timeStamp = System.currentTimeMillis();
        Humidity tick= new Humidity(price,timeStamp);
        System.out.println("Sending humidity: " + tick);
        check_hum=true;
        cepRT.sendEvent(tick);
    }
    public static boolean get_check1() {
        return check_temp;
    }
    public static boolean get_check2() {
        return check_hum;
    }
    
    public static class CEPListener implements UpdateListener {

        //static boolean ckeck_tempr=check_temp;
        //static boolean ckeck_humi=check_hum;
        public void update(EventBean[] newData, EventBean[] oldData) {
            //Double temperature = (double) newData[0].get("price");
            //System.out.println(String.format("Name: %s, Age: %d", name, age));
            System.out.println("Event received: "
                    + newData[0].getUnderlying());
            //EventBean event = newData[0];
            System.out.println("des auto temp: "+get_check1());
            System.out.println("des auto hum: "+get_check2());
            //System.out.println("Temperature=" + event.get("price"));
            //int i;
            //thermokrasia

            //boolean ckeck_temp=check_temp;
            if(get_check1()==true) {
                System.out.println("piasame temperature");
                int temp = (int) newData[0].get("price");
                if(temp>=40 && temp<60) temperature = temp;
                date_now = newData[0].get("timeStamp").toString();

                //SendNotification.sendDeviceNotification(temp);  //temperature as parameter
                //ckeck_temp++;
                check_temp=false;
            }
            //ygrasia
            //boolean ckeck_hum=check_hum;
            if(get_check2()==true) {
                System.out.println("piasame humidity");
                int hum = (int) newData[0].get("price");
                date_now = newData[0].get("timeStamp").toString();
                humidity = hum;
                //SendNotification.sendDeviceNotification(temp);  //temperature as parameter
                //ckeck_hum++;
                check_hum=false;
            }
        }
    }

    //when button is clicked, then send notification to this user who press the button
    public static void sendNotificationbyEsper() { //String playerId, int i
        ArrayList<String> playerstemp = MainActivity.getPlayersTemp();
        ArrayList<String> playershum = MainActivity.getPlayersHum();
        if(playerstemp.size()!=0) {
            for(String temp_player : playerstemp) {
                //System.out.print(temp_player);
                //System.out.print("Temperatureeeeee: "+temp + ", ");
                SendNotification.sendTempetatureNotification(temp_player, temperature, date_now);
            }
        }
        if(playershum.size()!=0) {
            for(String temp_player : playershum) {
                //System.out.print(temp_player);
                //System.out.print("Temperatureeeeee: "+temp + ", ");
                SendNotification.sendHumidityNotification(temp_player, humidity, date_now);
            }
        }

        /*if(i==1) SendNotification.sendTempetatureNotification(playerId, temperature);
        else if(i==2) SendNotification.sendHumidityNotification(playerId, humidity);
        else System.out.println("Error");*/
    }

    public static void checkTemperatureEvents(ArrayList<Integer> temperatures) {
        //System.out.println("Skata sta moutra");
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
            /*for Temperatures*/
            cep.getEPAdministrator().getConfiguration().addEventType("TemperatureEvent", Temperature.class);
            EPRuntime cepRT = cep.getEPRuntime();

            // We register an EPL statement (Query)
            //EPAdministrator cepAdm = cep.getEPAdministrator();
            EPStatement cepStatement = cep.getEPAdministrator().createEPL("select * from TemperatureEvent " +
                    "where price between 40 and 60");

            //Attach a listener to the statement
            cepStatement.addListener(new CEPListener());

            //Generate random values
            for(int temp : temperatures) {
                //System.out.print("Temperatureeeeee: "+temp + ", ");
                sendTemperature(cepRT, temp);

            }
            /*for (int i = 0; i < 10; i++) {
                GenerateRandomTick(cepRT);
            }*/
            cep.getEPAdministrator().getConfiguration().addEventType("HumidityEvent", Humidity.class);
            EPRuntime cepRT2 = cep.getEPRuntime();

            // We register an EPL statement (Query)
            //EPAdministrator cepAdm = cep.getEPAdministrator();
            EPStatement cepStatement2 = cep.getEPAdministrator().createEPL("select * from HumidityEvent " +
                    "where price > 80");

            //Attach a listener to the statement
            cepStatement2.addListener(new CEPListener());

            //Generate random values
            for(int temp : temperatures) {
                //System.out.print("Temperatureeeeee: "+temp + ", ");
                sendHumidity(cepRT2, temp);

            }

            //cep.destroy();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
