package com.example.testapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.onesignal.OSDeviceState;
import com.onesignal.OSPermissionObserver;
import com.onesignal.OSPermissionStateChanges;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * The Main Activity class, which is used when we have changes in the app
 * (for example, if the user clicks the button).
 *
 */
public class MainActivity extends AppCompatActivity implements OSPermissionObserver,OSSubscriptionObserver {
    //checkboxes for every category
    CheckBox tv, laptop, cellphone, bed, sofa, wash, oven, fridge, wardrobe, air, phone;
    //used for the saving of values of the checkboxes
    private boolean check_tv, check_laptop, check_cellphone, check_bed, check_sofa, check_wash, check_oven, check_fridge, check_wardrobe, check_phone, check_air;
    TextView OurText;

    //for MySQL database
    public static final String DB_URL = "jdbc:mysql://192.168.1.7:3306/Cms?autoReconnect=true&useSSL=false";
    public static final String USER = "root";
    public static final String PASS = "root";

    //save the available devices
    static ArrayList<String> devices = new ArrayList<>();
    //save the players to arraylist of categories
    static ArrayList<String> players_tv = new ArrayList<>();
    static ArrayList<String> players_laptop = new ArrayList<>();
    static ArrayList<String> players_cellphone = new ArrayList<>();
    static ArrayList<String> players_bed = new ArrayList<>();
    static ArrayList<String> players_sofa = new ArrayList<>();
    static ArrayList<String> players_wash = new ArrayList<>();
    static ArrayList<String> players_oven = new ArrayList<>();
    static ArrayList<String> players_fridge = new ArrayList<>();
    static ArrayList<String> players_wardrobe = new ArrayList<>();
    static ArrayList<String> players_air = new ArrayList<>();
    static ArrayList<String> players_phone = new ArrayList<>();

    //map the users with the available devices
    public static HashMap<Integer, String> usersmapping = new HashMap<>();

    //@RequiresApi(api = Build.VERSION_CODES.O)  //for the date
    //this method takes the message from RabbitMQ and and it calls the 2nd RabbitMQ to send the notification to the user
    public static void BeforeSend(int clientID, int product_id, int category_id) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String product_name = null, category_name = null, product_url = null;
        Connection conn;
        Statement stmt;
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
        } catch (Exception e) {
            e.printStackTrace();
        } /*finally {
            conn.close();
        }*/

        String player = null, send_player = null;

        if(devices.size()>0) {
            //Get the iterator over the HashMap
            Iterator<Map.Entry<Integer, String> >
                    iterator = usersmapping.entrySet().iterator();
            //flag to store result
            boolean keyExists = false;

            //Iterate over the HashMap
            while (iterator.hasNext()) {
                //Get the entry at this iteration
                Map.Entry<Integer, String>
                        entry
                        = iterator.next();

                //Check if this key is the required key
                if (clientID == entry.getKey()) {
                    keyExists = true;
                }
            }

            if(keyExists) {
                player=usersmapping.get(clientID);
                System.out.println("Player exists. "+player);
            }
            else {
                //save a new player to hashmap
                Random rand = new Random();
                int random = rand.nextInt(devices.size());

                usersmapping.put(clientID, devices.get(random));
                player=usersmapping.get(clientID);
                System.out.println("New Player. "+player);
            }
        }

        if(player!=null) {
            //match the category and check if player is subscibed
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

        //get the current date (if you want it for the notification)
        /*String date;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        //System.out.println(dtf.format(now));
        date = dtf.format(now);*/

        try {
            //send the events to the second RabbitMQ service
            Rabbit_SendEvents.SendEvents(send_player, category_name, product_name, product_url);
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
    /*this method runs when activity is starting*/
    public void onStart() {
        super.onStart();
        //pernoume tis times twn checkboxes
        tv = findViewById(R.id.checkbox_tv);
        laptop = findViewById(R.id.checkbox_laptop);
        cellphone = findViewById(R.id.checkbox_cellphone);
        bed = findViewById(R.id.checkbox_bed);
        sofa = findViewById(R.id.checkbox_sofa);
        wash = findViewById(R.id.checkbox_washmachine);
        oven = findViewById(R.id.checkbox_oven);
        fridge = findViewById(R.id.checkbox_fridge);
        wardrobe = findViewById(R.id.checkbox_wardrobe);
        phone = findViewById(R.id.checkbox_telephones);
        air = findViewById(R.id.checkbox_aircondition);

        //we check if they are checked or not from the saving value in PreferenceManager
        boolean check_tv = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("tv", false);
        tv.setChecked(check_tv);

        boolean check_laptop = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("laptop", false);
        laptop.setChecked(check_laptop);

        boolean check_cellphone = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("cellphone", false);
        cellphone.setChecked(check_cellphone);

        boolean check_bed = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("bed", false);
        bed.setChecked(check_bed);

        boolean check_sofa = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("sofa", false);
        sofa.setChecked(check_sofa);

        boolean check_wash = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("wash_machine", false);
        wash.setChecked(check_wash);

        boolean check_oven = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("oven", false);
        oven.setChecked(check_oven);

        boolean check_fridge = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("fridge", false);
        fridge.setChecked(check_fridge);

        boolean check_wardrobe = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("wardrobe", false);
        wardrobe.setChecked(check_wardrobe);

        boolean check_phone = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("telephones", false);
        phone.setChecked(check_phone);

        boolean check_air = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("air_condition", false);
        air.setChecked(check_air);
    }

    @Override
    /*this method runs when activity is stopping*/
    public void onStop() {
        super.onStop();
        //we check the values of checkbox and we save the value in PreferenceManager
        check_tv = tv.isChecked();   //returns true or false
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("tv", check_tv).apply();

        check_laptop = laptop.isChecked();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("laptop", check_laptop).apply();

        check_cellphone = cellphone.isChecked();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("cellphone", check_cellphone).apply();

        check_bed = bed.isChecked();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("bed", check_bed).apply();

        check_sofa = sofa.isChecked();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("sofa", check_sofa).apply();

        check_wash = wash.isChecked();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("wash_machine", check_wash).apply();

        check_oven = oven.isChecked();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("oven", check_oven).apply();

        check_fridge = fridge.isChecked();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("fridge", check_fridge).apply();

        check_wardrobe = wardrobe.isChecked();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("wardrobe", check_wardrobe).apply();

        check_phone = phone.isChecked();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("telephones", check_phone).apply();

        check_air = air.isChecked();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("air_condition", check_air).apply();
    }

    //this method runs when the user clicks the subscribe button
    public void onClick(View view) {
        OSDeviceState device = OneSignal.getDeviceState();

        //get user_id, who press the button
        //assert device because the getUserId() method maybe return null
        assert device != null;
        String userId = device.getUserId();
        if(!(devices.contains(userId))) devices.add(userId);  //add the player_id to devices

        OurText = findViewById(R.id.textView);

        int tv1=0, laptop1=0, cellphone1=0, bed1=0, sofa1=0,wash1=0, oven1=0, fridge1=0,wardrobe1=0,air1=0, phone1=0;
        if(tv.isChecked()){
            check_tv = true;
            if (players_tv.contains(userId)) tv1=1;
            if(tv1==0) {
                /*we save the user to players_tv arraylist*/
                players_tv.add(userId);
                System.out.println("Player " +userId+ " subscribed to TV");
            }
        }
        else {
            check_tv = false;
            if (players_tv.contains(userId)) tv1=1;
            else tv1=0;
            if(tv1==1) {
                /*we delete the user from players_tv arraylist*/
                players_tv.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from TV");
            }
        }
        if(laptop.isChecked()){
            check_laptop = true;
            if (players_laptop.contains(userId)) laptop1=1;
            if(laptop1==0) {
                /*we save the user to players_laptop arraylist*/
                players_laptop.add(userId);
                System.out.println("Player " +userId+ " subscribed to Laptop");
            }
        }
        else {
            check_laptop = false;
            if (players_laptop.contains(userId)) laptop1=1;
            else laptop1=0;
            if(laptop1==1) {
                /*we delete the user from players_laptop arraylist*/
                players_laptop.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Laptop");
            }
        }
        if(cellphone.isChecked()){
            check_cellphone = true;
            if (players_phone.contains(userId)) cellphone1=1;
            if(cellphone1==0) {
                /*we save the user to players_phone arraylist*/
                players_phone.add(userId);
                System.out.println("Player " +userId+ " subscribed to CellPhone");
            }
        }
        else {
            check_cellphone = false;
            if (players_phone.contains(userId)) cellphone1=1;
            else cellphone1=0;
            if(cellphone1==1) {
                /*we delete the user from players_phone arraylist*/
                players_phone.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from CellPhone");
            }
        }
        if(bed.isChecked()){
            check_bed = true;
            if (players_bed.contains(userId)) bed1=1;
            if(bed1==0) {
                /*we save the user to players_bed arraylist*/
                players_bed.add(userId);
                System.out.println("Player " +userId+ " subscribed to Bed");
            }
        }
        else {
            check_bed = false;
            if (players_bed.contains(userId)) bed1=1;
            else bed1=0;
            if(bed1==1) {
                /*we delete the user from players_bed arraylist*/
                players_bed.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Bed");
            }
        }
        if(sofa.isChecked()){
            check_sofa = true;
            if (players_sofa.contains(userId)) sofa1=1;
            if(sofa1==0) {
                /*we save the user to players_sofa arraylist*/
                players_sofa.add(userId);
                System.out.println("Player " +userId+ " subscribed to Sofa");
            }
        }
        else {
            check_sofa = false;
            if (players_sofa.contains(userId)) sofa1=1;
            else sofa1=0;
            if(sofa1==1) {
                /*we delete the user from players_sofa arraylist*/
                players_sofa.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Sofa");
            }
        }
        if(wash.isChecked()){
            check_wash = true;
            if (players_wash.contains(userId)) wash1=1;
            if(wash1==0) {
                /*we save the user to players_wash arraylist*/
                players_wash.add(userId);
                System.out.println("Player " +userId+ " subscribed to Washing Machine");
            }
        }
        else {
            check_wash = false;
            if (players_wash.contains(userId)) wash1=1;
            else wash1=0;
            if(wash1==1) {
                /*we delete the user from players_wash arraylist*/
                players_wash.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Washing Machine");
            }
        }
        if(oven.isChecked()){
            check_oven = true;
            if (players_oven.contains(userId)) oven1=1;
            if(oven1==0) {
                /*we save the user to players_oven arraylist*/
                players_oven.add(userId);
                System.out.println("Player " +userId+ " subscribed to Oven");
            }
        }
        else {
            check_oven = false;
            if (players_oven.contains(userId)) oven1=1;
            else oven1=0;
            if(oven1==1) {
                /*we delete the user from players_oven arraylist*/
                players_oven.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Oven");
            }
        }
        if(fridge.isChecked()){
            check_fridge = true;
            if (players_fridge.contains(userId)) fridge1=1;
            if(fridge1==0) {
                /*we save the user to players_fridge arraylist*/
                players_fridge.add(userId);
                System.out.println("Player " +userId+ " subscribed to Fridge");
            }
        }
        else {
            check_fridge = false;
            if (players_fridge.contains(userId)) fridge1=1;
            else fridge1=0;
            if(fridge1==1) {
                /*we delete the user from players_fridge arraylist*/
                players_fridge.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Fridge");
            }
        }
        if(wardrobe.isChecked()){
            check_wardrobe = true;
            if (players_wardrobe.contains(userId)) wardrobe1=1;
            if(wardrobe1==0) {
                /*we save the user to players_wardrobe arraylist*/
                players_wardrobe.add(userId);
                System.out.println("Player " +userId+ " subscribed to Wardrobe");
            }
        }
        else {
            check_wardrobe = false;
            if (players_wardrobe.contains(userId)) wardrobe1=1;
            else wardrobe1=0;
            if(wardrobe1==1) {
                /*we delete the user from players_wardrobe arraylist*/
                players_wardrobe.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Wardrobe");
            }
        }
        if(air.isChecked()){
            check_air = true;
            if (players_air.contains(userId)) air1=1;
            if(air1==0) {
                /*we save the user to players_air arraylist*/
                players_air.add(userId);
                System.out.println("Player " +userId+ " subscribed to Air Condition");
            }
        }
        else {
            check_air = false;
            if (players_air.contains(userId)) air1=1;
            else air1=0;
            if(air1==1) {
                /*we delete the user from players_air arraylist*/
                players_air.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Air Condition");
            }
        }
        if(phone.isChecked()){
            check_phone = true;
            if (players_phone.contains(userId)) phone1=1;
            if(phone1==0) {
                /*we save the user to players_phone arraylist*/
                players_phone.add(userId);
                System.out.println("Player " +userId+ " subscribed to Telephones");
            }
        }
        else {
            check_phone = false;
            if (players_phone.contains(userId)) phone1=1;
            else phone1=0;
            if(phone1==1) {
                /*we delete the user from players_phone arraylist*/
                players_phone.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from Telephones");
            }
        }
        OurText.setText("You subscribed successfully. You can leave the app now.");
    }

    /*epistrefoun tous paiktes pou ekanan subscribe sto sygkekrimeno arraylist kathogorias*/
    public static ArrayList<String> getPlayersTV() {
        return players_tv;
    }
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

