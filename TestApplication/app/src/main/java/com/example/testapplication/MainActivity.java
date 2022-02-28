package com.example.testapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.sql.*;
import com.onesignal.OSDeviceState;
import com.onesignal.OSPermissionObserver;
import com.onesignal.OSPermissionStateChanges;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The activity class, which is used when user make changes in the app (for example, if user clicks the button)
 *
 */
public class MainActivity extends AppCompatActivity implements OSPermissionObserver,OSSubscriptionObserver {
    CheckBox tv, laptop, cellphone, bed, sofa, wash, oven, fridge, wardrobe, air, phone;
    boolean checked1, checked2, checked3, checked4, checked5, checked6, checked7, checked8, checked9, checked10, checked11;   /*xrisimopoiountai gia to save twn timwn tou checkbox*/
    private static boolean check_tv, check_laptop, check_cellphone, check_bed, check_sofa, check_wash, check_oven, check_fridge, check_wardrobe, check_air, check_phone;    /*analoga to subscribe, ginetai true to antistoixo*/
    //String player;    //player_id
    TextView OurText;

    //for MySQL database
    public static final String DB_URL = "jdbc:mysql://192.168.1.8:3306/Cms?autoReconnect=true&useSSL=false";
    public static final String USER = "root";
    public static final String PASS = "root";

    static ArrayList<String> devices = new ArrayList<String>();
    //save the players to arraylist categories
    static ArrayList<String> players_tv = new ArrayList<String>(); // Create an ArrayList with player subscribes to temperatures
    static ArrayList<String> players_laptop = new ArrayList<String>(); // Create an ArrayList object with player subscribes to humidities
    static ArrayList<String> players_cellphone = new ArrayList<String>(); // Create an ArrayList with player subscribes to temperatures
    static ArrayList<String> players_bed = new ArrayList<String>(); // Create an ArrayList object with player subscribes to humidities
    static ArrayList<String> players_sofa = new ArrayList<String>();
    static ArrayList<String> players_wash = new ArrayList<String>();
    static ArrayList<String> players_oven = new ArrayList<String>();
    static ArrayList<String> players_fridge = new ArrayList<String>();
    static ArrayList<String> players_wardrobe = new ArrayList<String>();
    static ArrayList<String> players_air = new ArrayList<String>();
    static ArrayList<String> players_phone = new ArrayList<String>();

    //send the notification to the user after a delay of 15 seconds
    /*private final static int DELAY = 15000;
    private final Handler handler = new Handler();
    private final Timer timer = new Timer();
    private final TimerTask task = new TimerTask() {
        private int counter = 0;
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    //System.out.println("mphka");
                    //kalei 2o rabbitmq
                    int temp, hum;
                    String date;

                    if(check_tv==false) temp=0;
                    else temp = EsperTemperature.get_Temperature();
                    if(humid==false) hum=0;
                    else hum = EsperTemperature.get_Humidity();
                    date = EsperTemperature.get_Date();

                    try {
                        Rabbit_SendEvents.ReceiveEvents(player, temp, hum, date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Rabbit_SendEvents.SendEvents(player, temp, hum, date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    timer.cancel();
                    //counter++;
                }
            });
        }
    };*/

    //take the message from RabbitMQ and send the notification to the user
    //@RequiresApi(api = Build.VERSION_CODES.O)  //for date
    public static void BeforeSend(int clientID, int product_id, int category_id) {
        //kalei 2o rabbitmq
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String product_name = null, category_name=null, product_url=null;
        Connection conn=null;
        Statement stmt=null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                    DB_URL, USER, PASS);
            System.out.println("Connected to database successfully.");
            stmt = conn.createStatement();
            String sql = "SELECT * FROM cms.product_categories where id="+category_id+"";
            ResultSet result = stmt.executeQuery(sql);

            while(result.next()) {
                category_name = result.getString("name");
                System.out.println("Category: "+category_name);
            }

            sql = "SELECT * FROM cms.products where id="+product_id+"";
            result = stmt.executeQuery(sql);

            while(result.next()) {
                product_name = result.getString("sku");
                product_url = result.getString("productUrl");
                System.out.println("Product: "+product_name);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        } /*finally {
            conn.close();
        }*/

        ArrayList<String> players = new ArrayList<String>();
        String player = null, send_player=null;

        if(devices.size()>0) {
            /*for(String device: getPlayersTV()) {
                System.out.println("device: "+device);
            }*/
            if(clientID>=5 && clientID<=18) {
                player=devices.get(0);
                //d347f547-0864-4d72-93a8-ce474ffb675c
            }
            else {
                if(devices.size()==1) player=devices.get(0);
                else {
                    player=devices.get(1);
                    //c4846f1e-8c36-11ec-adb5-620e4f3c75e3
                }
            }
        }

        if(player!=null) {
            //match the category (players = getPlayersTV();
            if (category_id == 1 && players_tv.contains(player)) send_player = player;
            else if (category_id == 15 && players_fridge.contains(player)) send_player = player;
            else if (category_id == 16 && players_wash.contains(player)) send_player = player;
            else if (category_id == 17 && players_sofa.contains(player)) send_player = player;
            else if (category_id == 18 && players_wardrobe.contains(player)) send_player = player;
            else if (category_id == 19 && players_bed.contains(player)) send_player = player;
            else if (category_id == 20 && players_air.contains(player)) send_player = player;
            else if (category_id == 21 && players_cellphone.contains(player)) send_player = player;
            else if (category_id == 22 && players_laptop.contains(player)) send_player = player;
            else if (category_id == 23 && players_oven.contains(player)) send_player = player;
            else if (category_id == 24 && players_phone.contains(player)) send_player = player;
        }

        //check where user is subscribed
        /*if(check_tv==true) tv1 = EsperTemperature.get_Temperature();
        if(check_laptop==true) laptop1 = EsperTemperature.get_Humidity();
        if(check_cellphone==true) {

        }
        if(check_bed==true) {
            if(category.equals("bed")) {
                //send notification
            }
        }*/
        String date;
        //get the current date
        /*DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        //System.out.println(dtf.format(now));
        date = dtf.format(now);*/
        date = null;   //gia thn wra

       try {
            //send the events to RabbitMQ
            Rabbit_SendEvents.SendEvents(send_player, category_name, product_name, product_url, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OneSignal.addPermissionObserver(this);
        OneSignal.addSubscriptionObserver(this);
    }

    @Override
    public void onOSPermissionChanged(OSPermissionStateChanges stateChanges) {
        if (stateChanges.getFrom().areNotificationsEnabled() &&
                !stateChanges.getTo().areNotificationsEnabled()) {
            new AlertDialog.Builder(this)
                    .setMessage("Notifications Disabled!")
                    .show();
        }
        Log.i("Debug", "onOSPermissionChanged: " + stateChanges);
    }

    /*subscribe the new users to push notifications*/
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        if (!stateChanges.getFrom().isSubscribed() &&
                stateChanges.getTo().isSubscribed()) {
            new AlertDialog.Builder(this)
                    .setMessage("You've successfully subscribed to push notifications!")
                    .show();
            stateChanges.getTo().getUserId();
            //if(!(devices.contains(stateChanges.getTo().getUserId()))) devices.add(stateChanges.getTo().getUserId(););
        }
        Log.i("Debug", "onOSSubscriptionChanged: " + stateChanges);
    }

    @Override
    /*auto ginetai otan mpoume sto activity*/
    public void onStart() {
        super.onStart();
        //pernoume tis times twn checkboxes
        tv = (CheckBox)findViewById(R.id.checkbox_tv);
        laptop = (CheckBox)findViewById(R.id.checkbox_laptop);
        cellphone = (CheckBox)findViewById(R.id.checkbox_cellphone);
        bed = (CheckBox)findViewById(R.id.checkbox_bed);
        sofa = (CheckBox)findViewById(R.id.checkbox_sofa);
        wash = (CheckBox)findViewById(R.id.checkbox_washmachine);
        oven = (CheckBox)findViewById(R.id.checkbox_oven);
        fridge = (CheckBox)findViewById(R.id.checkbox_fridge);
        wardrobe = (CheckBox)findViewById(R.id.checkbox_wardrobe);
        phone = (CheckBox)findViewById(R.id.checkbox_telephones);
        air = (CheckBox)findViewById(R.id.checkbox_aircondition);

        //we check if they are checked or not from the saving value in PreferenceManager
        boolean checked1 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("tv", false);
        tv.setChecked(checked1);

        boolean checked2 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("laptop", false);
        laptop.setChecked(checked2);

        boolean checked3 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("cellphone", false);
        cellphone.setChecked(checked3);

        boolean checked4 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("bed", false);
        bed.setChecked(checked4);

        boolean checked5 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("sofa", false);
        sofa.setChecked(checked5);

        boolean checked6 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("wash_machine", false);
        wash.setChecked(checked6);

        boolean checked7 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("oven", false);
        oven.setChecked(checked7);

        boolean checked8 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("fridge", false);
        fridge.setChecked(checked8);

        boolean checked9 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("wardrobe", false);
        wardrobe.setChecked(checked9);

        boolean checked10 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("telephones", false);
        phone.setChecked(checked10);

        boolean checked11 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("air_condition", false);
        air.setChecked(checked11);
    }

    @Override
    /*auto ginetai otan kleisei to app*/
    public void onStop() {
        super.onStop();
        //we check the values of checkbox and we save the value in PreferenceManager
        if(tv.isChecked()) checked1=true;
        else checked1=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("tv", checked1).commit();

        if(laptop.isChecked()) checked2=true;
        else checked2=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("laptop", checked2).commit();

        if(cellphone.isChecked()) checked3=true;
        else checked3=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("cellphone", checked3).commit();

        if(bed.isChecked()) checked4=true;
        else checked4=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("bed", checked4).commit();

        if(sofa.isChecked()) checked5=true;
        else checked5=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("sofa", checked5).commit();

        if(wash.isChecked()) checked6=true;
        else checked6=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("wash_machine", checked6).commit();

        if(oven.isChecked()) checked7=true;
        else checked7=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("oven", checked7).commit();

        if(fridge.isChecked()) checked8=true;
        else checked8=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("fridge", checked8).commit();

        if(wardrobe.isChecked()) checked9=true;
        else checked9=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("wardrobe", checked9).commit();

        if(phone.isChecked()) checked10=true;
        else checked10=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("telephones", checked10).commit();

        if(air.isChecked()) checked11=true;
        else checked11=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("air_condition", checked11).commit();

        //System.out.println("ola komple");
    }

    //when user clicks the subscribe button
    public void onClick(View view) {
        OSDeviceState device = OneSignal.getDeviceState();

        //get player_id, who press the button
        String userId = device.getUserId();
        //player = userId;
        if(!(devices.contains(userId))) devices.add(userId);  //add the player_id to devices
        /*for(String devicee: devices) {
            System.out.println("device: "+devicee);
        }*/

        OurText = findViewById(R.id.textView);

        int tv1=0, laptop1=0, cellphone1=0, bed1=0, sofa1=0,wash1=0, oven1=0, fridge1=0,wardrobe1=0,air1=0, phone1=0;
        if(tv.isChecked()){
            check_tv = true;
            if (players_tv.contains(userId)) tv1=1;
            if(tv1==0) {
                /*we save the user to playerstemp arraylist*/
                players_tv.add(userId);
                System.out.println("Player " +userId+ " subscribed to TV");
            }
        }
        else {
            check_tv = false;
            if (players_tv.contains(userId)) tv1=1;
            else tv1=0;
            if(tv1==1) {
                /*we delete the user from playerstemp arraylist*/
                players_tv.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from TV");
            }
        }
        if(laptop.isChecked()){
            check_laptop = true;
            if (players_laptop.contains(userId)) laptop1=1;
            if(laptop1==0) {
                /*we save the user to playershum arraylist*/
                players_laptop.add(userId);
                System.out.println("Player " +userId+ " subscribed to Laptop");
            }
        }
        else {
            check_laptop = false;
            if (players_laptop.contains(userId)) laptop1=1;
            else laptop1=0;
            if(laptop1==1) {
                /*we delete the user from playerstemp arraylist*/
                players_laptop.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Laptop");
            }
        }
        if(cellphone.isChecked()){
            check_cellphone = true;
            if (players_phone.contains(userId)) cellphone1=1;
            if(cellphone1==0) {
                /*we save the user to playershum arraylist*/
                players_phone.add(userId);
                System.out.println("Player " +userId+ " subscribed to CellPhone");
            }
        }
        else {
            check_cellphone = false;
            if (players_phone.contains(userId)) cellphone1=1;
            else cellphone1=0;
            if(cellphone1==1) {
                /*we delete the user from playerstemp arraylist*/
                players_phone.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from CellPhone");
            }
        }
        if(bed.isChecked()){
            check_bed = true;
            if (players_bed.contains(userId)) bed1=1;
            if(bed1==0) {
                /*we save the user to playershum arraylist*/
                players_bed.add(userId);
                System.out.println("Player " +userId+ " subscribed to Bed");
            }
        }
        else {
            check_bed = false;
            if (players_bed.contains(userId)) bed1=1;
            else bed1=0;
            if(bed1==1) {
                /*we delete the user from playerstemp arraylist*/
                players_bed.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Bed");
            }
        }
        if(sofa.isChecked()){
            check_sofa = true;
            if (players_sofa.contains(userId)) sofa1=1;
            if(sofa1==0) {
                /*we save the user to playerstemp arraylist*/
                players_sofa.add(userId);
                System.out.println("Player " +userId+ " subscribed to Sofa");
            }
        }
        else {
            check_sofa = false;
            if (players_sofa.contains(userId)) sofa1=1;
            else sofa1=0;
            if(sofa1==1) {
                /*we delete the user from playerstemp arraylist*/
                players_sofa.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Sofa");
            }
        }
        if(wash.isChecked()){
            check_wash = true;
            if (players_wash.contains(userId)) wash1=1;
            if(wash1==0) {
                /*we save the user to playershum arraylist*/
                players_wash.add(userId);
                System.out.println("Player " +userId+ " subscribed to Washing Machine");
            }
        }
        else {
            check_wash = false;
            if (players_wash.contains(userId)) wash1=1;
            else wash1=0;
            if(wash1==1) {
                /*we delete the user from playerstemp arraylist*/
                players_wash.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Washing Machine");
            }
        }
        if(oven.isChecked()){
            check_oven = true;
            if (players_oven.contains(userId)) oven1=1;
            if(oven1==0) {
                /*we save the user to playershum arraylist*/
                players_oven.add(userId);
                System.out.println("Player " +userId+ " subscribed to Oven");
            }
        }
        else {
            check_oven = false;
            if (players_oven.contains(userId)) oven1=1;
            else oven1=0;
            if(oven1==1) {
                /*we delete the user from playerstemp arraylist*/
                players_oven.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Oven");
            }
        }
        if(fridge.isChecked()){
            check_fridge = true;
            if (players_fridge.contains(userId)) fridge1=1;
            if(fridge1==0) {
                /*we save the user to playershum arraylist*/
                players_fridge.add(userId);
                System.out.println("Player " +userId+ " subscribed to Fridge");
            }
        }
        else {
            check_fridge = false;
            if (players_fridge.contains(userId)) fridge1=1;
            else fridge1=0;
            if(fridge1==1) {
                /*we delete the user from playerstemp arraylist*/
                players_fridge.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Fridge");
            }
        }
        if(wardrobe.isChecked()){
            check_wardrobe = true;
            if (players_wardrobe.contains(userId)) wardrobe1=1;
            if(wardrobe1==0) {
                /*we save the user to playershum arraylist*/
                players_wardrobe.add(userId);
                System.out.println("Player " +userId+ " subscribed to Wardrobe");
            }
        }
        else {
            check_wardrobe = false;
            if (players_wardrobe.contains(userId)) wardrobe1=1;
            else wardrobe1=0;
            if(wardrobe1==1) {
                /*we delete the user from playerstemp arraylist*/
                players_wardrobe.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Wardrobe");
            }
        }
        if(air.isChecked()){
            check_air = true;
            if (players_air.contains(userId)) air1=1;
            if(air1==0) {
                /*we save the user to playershum arraylist*/
                players_air.add(userId);
                System.out.println("Player " +userId+ " subscribed to Air Condition");
            }
        }
        else {
            check_air = false;
            if (players_air.contains(userId)) air1=1;
            else air1=0;
            if(air1==1) {
                /*we delete the user from playerstemp arraylist*/
                players_air.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Air Condition");
            }
        }
        if(phone.isChecked()){
            check_phone = true;
            if (players_phone.contains(userId)) phone1=1;
            if(phone1==0) {
                /*we save the user to playershum arraylist*/
                players_phone.add(userId);
                System.out.println("Player " +userId+ " subscribed to Telephones");
            }
        }
        else {
            check_phone = false;
            if (players_phone.contains(userId)) phone1=1;
            else phone1=0;
            if(phone1==1) {
                /*we delete the user from playerstemp arraylist*/
                players_phone.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Telephones");
            }
        }
        OurText.setText("You subscribed successfully. You can leave the app now.");
        //BeforeSend();
        //timer.schedule(task, DELAY, DELAY);

        /*if(userId!=null) EsperTemperature.whenButtonClicked(userId, 1);
        else System.out.println("Null UserId. Can't send notification");*/
    }

    /*epistrefei tous paiktes pou ekanan subscribe sth tv*/
    public static ArrayList<String> getPlayersTV() {
        return players_tv;
    }
    /*epistrefei tous paiktes pou ekanan subscribe sto laptop*/
    public static ArrayList<String> getPlayersLaptop() {
        return players_laptop;
    }
    public static ArrayList<String> getPlayersCellPhone() {
        return players_cellphone;
    }
    public static ArrayList<String> getPlayersBed() {
        return players_bed;
    }
    public static ArrayList<String> getPlayersSofa() {
        return players_sofa;
    }
    public static ArrayList<String> getPlayersWashMachine() {
        return players_wash;
    }
    public static ArrayList<String> getPlayersOven() {
        return players_oven;
    }
    public static ArrayList<String> getPlayersFridge() {
        return players_fridge;
    }
    public static ArrayList<String> getPlayersWardrobe() {
        return players_wardrobe;
    }
    public static ArrayList<String> getPlayersAirCondition() {
        return players_air;
    }
    public static ArrayList<String> getPlayersPhone() {
        return players_phone;
    }
}

