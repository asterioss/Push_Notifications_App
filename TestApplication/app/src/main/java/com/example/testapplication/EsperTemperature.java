package com.example.testapplication;

import java.util.ArrayList;
import java.util.Date;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * The esper service class, which receives the messages from rabbitmq and it catches the specific events.
 *
 */
public class EsperTemperature {
    private static int temperature;
    private static int humidity;
    private static String date_now;

    public static boolean check_hum;    //it's true when we catch an event in humidities
    public static boolean check_temp;  //it's true when we catch an event in temperatures

    public static class Temperature {
        Integer price;
        Date timeStamp;

        public Temperature(int p, long t) {
            price = p;
            timeStamp = new Date(t);
        }
        public Integer getPrice() { return price; }
        public Date getTimeStamp() { return timeStamp; }

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
        public Integer getPrice() { return price; }
        public Date getTimeStamp() { return timeStamp; }

        @Override
        public String toString() {
            return "Humidity: " + price.toString() + ", Time: " + timeStamp.toString();
        }
    }

    public static void sendTemperature(EPRuntime cepRT, int temp_price){
        int price = temp_price;
        long timeStamp = System.currentTimeMillis();
        Temperature tick = new Temperature(price,timeStamp);
        System.out.println("Sending temperature: " + tick);
        check_temp=true;
        cepRT.sendEvent(tick);
    }

    public static void sendHumidity(EPRuntime cepRT, int temp_price){
        int price = temp_price;
        long timeStamp = System.currentTimeMillis();
        Humidity tick = new Humidity(price,timeStamp);
        System.out.println("Sending humidity: " + tick);
        check_hum=true;
        cepRT.sendEvent(tick);
    }

    public static boolean get_checkTemp() {
        return check_temp;
    }
    public static boolean get_checkHum() {
        return check_hum;
    }

    /*epistrefei tis telikes times pou epiase to esper san events*/
    public static int get_Temperature() { return temperature; }
    public static int get_Humidity() { return humidity; }
    //epistrefei to date
    public static String get_Date() { return date_now; }
    
    public static class CEPListener implements UpdateListener {
        public void update(EventBean[] newData, EventBean[] oldData) {
            System.out.println("Event received: "
                    + newData[0].getUnderlying());
            //System.out.println("des auto temp: "+get_checkTemp());
            //System.out.println("des auto hum: "+get_checkHum());

            //thermokrasia
            if(get_checkTemp()==true) {
                System.out.println("piasame temperature");
                int temp = (int) newData[0].get("price");
                if(temp>=40 && temp<60) temperature = temp;
                date_now = newData[0].get("timeStamp").toString();
                check_temp=false;
            }
            //ygrasia
            if(get_checkHum()==true) {
                System.out.println("piasame humidity");
                int hum = (int) newData[0].get("price");
                date_now = newData[0].get("timeStamp").toString();
                humidity = hum;
                check_hum=false;
            }
        }
    }

    //den xrhsimopoieitai pleon - NOT USED
    //when button is clicked, then send notification to this user who press the button
    public static void sendNotificationbyEsper() { //String playerId, int i
        ArrayList<String> playerstemp = MainActivity.getPlayersTV();
        ArrayList<String> playershum = MainActivity.getPlayersLaptop();
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
    }

    public static void checkTheEvents(ArrayList<Integer> temperatures, ArrayList<Integer> humidities) {
        //We receive the temperatures and humidities from rabbitmq and we catch the events that we we want
        try {
            //We setup the engine. The Configuration is meant only as an initialization-time object.
            Configuration config = new Configuration();
            EPServiceProvider cep = EPServiceProviderManager.getDefaultProvider(config);
            //cep.initialize();

            /*for Temperatures*/
            //We register Temps as objects the engine will have to handle
            cep.getEPAdministrator().getConfiguration().addEventType("TemperatureEvent", Temperature.class);
            EPRuntime cepRT = cep.getEPRuntime();

            //We register an EPL statement (Query)
            EPStatement cepStatement = cep.getEPAdministrator().createEPL("select * from TemperatureEvent " +
                    "where price between 40 and 60");

            //Attach a listener to the statement
            cepStatement.addListener(new CEPListener());

            //We send the values (temperatures) from temperatures arraylist
            for(int temp : temperatures) {
                sendTemperature(cepRT, temp);
            }

            /*For humidities*/
            //We register Temps as objects the engine will have to handle
            cep.getEPAdministrator().getConfiguration().addEventType("HumidityEvent", Humidity.class);
            EPRuntime cepRT2 = cep.getEPRuntime();

            //We register an EPL statement (Query)
            EPStatement cepStatement2 = cep.getEPAdministrator().createEPL("select * from HumidityEvent " +
                    "where price > 80");

            //Attach a listener to the statement
            cepStatement2.addListener(new CEPListener());

            //We send the values (humidities) from humidities arraylist
            for(int hum : humidities) {
                sendHumidity(cepRT2, hum);
            }

            //cep.destroy();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
