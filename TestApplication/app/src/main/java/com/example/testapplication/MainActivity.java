package com.example.testapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.onesignal.OSDeviceState;
import com.onesignal.OSPermissionObserver;
import com.onesignal.OSPermissionStateChanges;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The activity class, which is used when user make changes in the app (for example, if user clicks the button)
 *
 */
public class MainActivity extends AppCompatActivity implements OSPermissionObserver,OSSubscriptionObserver {
    CheckBox tv, laptop, phone, bed;
    boolean checked1, checked2, checked3, checked4;   /*xrisimopoiountai gia to save twn timwn tou checkbox*/
    boolean check_tv, check_laptop, check_phone, check_bed;    /*analoga to subscribe, ginetai true to antistoixo*/
    String player;    //player_id
    TextView OurText;

    //save the players to arraylist categories
    static ArrayList<String> players_tv = new ArrayList<String>(); // Create an ArrayList with player subscribes to temperatures
    static ArrayList<String> players_laptop = new ArrayList<String>(); // Create an ArrayList object with player subscribes to humidities
    static ArrayList<String> players_phone = new ArrayList<String>(); // Create an ArrayList with player subscribes to temperatures
    static ArrayList<String> players_bed = new ArrayList<String>(); // Create an ArrayList object with player subscribes to humidities

    //send the notification to the user after a delay of 15 seconds
    /*private final static int DELAY = 5000;
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

    //send the notification to the user
    public void BeforeSend() {
        //kalei 2o rabbitmq
        int tv1, laptop1;
        String date;

        if(check_tv==false) tv1=0;
        else tv1 = EsperTemperature.get_Temperature();
        if(check_laptop==false) laptop1=0;
        else laptop1 = EsperTemperature.get_Humidity();
        date = EsperTemperature.get_Date();

        try {
            Rabbit_SendEvents.SendEvents(player, tv1, laptop1, date);
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
        }
        Log.i("Debug", "onOSSubscriptionChanged: " + stateChanges);
    }

    @Override
    /*auto ginetai otan mpoume sto activity*/
    public void onStart() {
        super.onStart();
        //pernoume tis times tou checkbox
        tv = (CheckBox)findViewById(R.id.checkbox_tv);
        laptop = (CheckBox)findViewById(R.id.checkbox_laptop);
        phone = (CheckBox)findViewById(R.id.checkbox_phone);
        bed = (CheckBox)findViewById(R.id.checkbox_bed);

        //we check if they are checked or not from the saving value in PreferenceManager
        boolean checked1 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("tv", false);
        tv.setChecked(checked1);

        boolean checked2 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("laptop", false);
        laptop.setChecked(checked2);

        boolean checked3 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("phone", false);
        phone.setChecked(checked3);

        boolean checked4 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("bed", false);
        bed.setChecked(checked4);
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

        if(phone.isChecked()) checked3=true;
        else checked3=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("phone", checked3).commit();

        if(bed.isChecked()) checked4=true;
        else checked4=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("bed", checked4).commit();

        //System.out.println("ola komple");
    }

    //when user clicks the subscribe button
    public void onClick(View view) {
        OSDeviceState device = OneSignal.getDeviceState();

        //get player_id, who press the button
        String userId = device.getUserId();
        player = userId;

        OurText = findViewById(R.id.textView);

        int k=0, l=0;  //k for temps, l for hums
        if(tv.isChecked()){
            check_tv = true;
            if (players_tv.contains(userId)) k=1;
            if(k==0) {
                /*we save the user to playerstemp arraylist*/
                players_tv.add(userId);
                System.out.println("Player " +userId+ " subscribed to TV");
            }
        }
        else {
            check_tv = false;
            if (players_tv.contains(userId)) k=1;
            else k=0;
            if(k==1) {
                /*we delete the user from playerstemp arraylist*/
                players_tv.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from TV");
            }
        }
        if(laptop.isChecked()){
            check_laptop = true;
            if (players_laptop.contains(userId)) l=1;
            if(l==0) {
                /*we save the user to playershum arraylist*/
                players_laptop.add(userId);
                System.out.println("Player " +userId+ " subscribed to humidities");
            }
        }
        else {
            check_laptop = false;
            if (players_laptop.contains(userId)) l=1;
            else l=0;
            if(l==1) {
                /*we delete the user from playerstemp arraylist*/
                players_laptop.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from humidities");
            }
        }
        if(phone.isChecked()){
            check_phone = true;
            if (players_phone.contains(userId)) l=1;
            if(l==0) {
                /*we save the user to playershum arraylist*/
                players_phone.add(userId);
                System.out.println("Player " +userId+ " subscribed to humidities");
            }
        }
        else {
            check_phone = false;
            if (players_phone.contains(userId)) l=1;
            else l=0;
            if(l==1) {
                /*we delete the user from playerstemp arraylist*/
                players_phone.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from humidities");
            }
        }
        if(bed.isChecked()){
            check_bed = true;
            if (players_bed.contains(userId)) l=1;
            if(l==0) {
                /*we save the user to playershum arraylist*/
                players_bed.add(userId);
                System.out.println("Player " +userId+ " subscribed to humidities");
            }
        }
        else {
            check_bed = false;
            if (players_bed.contains(userId)) l=1;
            else l=0;
            if(l==1) {
                /*we delete the user from playerstemp arraylist*/
                players_bed.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from humidities");
            }
        }
        OurText.setText("You subscribed successfully. You can leave the app now.");
        BeforeSend();
        //timer.schedule(task, DELAY, DELAY);

        /*if(userId!=null) EsperTemperature.whenButtonClicked(userId, 1);
        else System.out.println("Null UserId. Can't send notification");*/
    }

    /*epistrefei tous paiktes pou ekanan subscribe sto temperature*/
    public static ArrayList<String> getPlayersTV() {
        return players_tv;
    }
    /*epistrefei tous paiktes pou ekanan subscribe sto temperature*/
    public static ArrayList<String> getPlayersLaptop() {
        return players_laptop;
    }
}

