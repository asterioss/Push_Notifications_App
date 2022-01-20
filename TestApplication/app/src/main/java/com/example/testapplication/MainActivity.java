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
    CheckBox temp, hum;
    boolean checked1, checked2;   /*xrisimopoiountai gia to save twn timwn tou checkbox*/
    boolean temper, humid;    /*analoga to subscribe, ginetai true to antistoixo*/
    String player;    //player_id
    TextView OurText;
    static ArrayList<String> playerstemp = new ArrayList<String>(); // Create an ArrayList with player subscribes to temperatures
    static ArrayList<String> playershum = new ArrayList<String>(); // Create an ArrayList object with player subscribes to humidities

    //send the notification to the user after a delay of 15 seconds
    private final static int DELAY = 15000;
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

                    if(temper==false) temp=0;
                    else temp = EsperTemperature.get_Temperature();
                    if(humid==false) hum=0;
                    else hum = EsperTemperature.get_Humidity();
                    date = EsperTemperature.get_Date();

                    /*try {
                        Rabbit_SendEvents.ReceiveEvents(player, temp, hum, date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
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
    };

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
        temp = (CheckBox)findViewById(R.id.checkbox_temp);
        hum = (CheckBox)findViewById(R.id.checkbox_hum);

        //we check if they are checked or not from the saving value in PreferenceManager
        boolean checked1 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("temp", false);
        temp.setChecked(checked1);

        boolean checked2 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("hum", false);
        hum.setChecked(checked2);
    }

    @Override
    /*auto ginetai otan kleisei to app*/
    public void onStop() {
        super.onStop();
        //we check the values of checkbox and we save the value in PreferenceManager
        if(temp.isChecked()) checked1=true;
        else checked1=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("temp", checked1).commit();

        if(hum.isChecked()) checked2=true;
        else checked2=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("hum", checked2).commit();

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
        if(temp.isChecked()){
            temper = true;
            if (playerstemp.contains(userId)) k=1;
            if(k==0) {
                /*we save the user to playerstemp arraylist*/
                playerstemp.add(userId);
                System.out.println("Player " +userId+ " subscribed to temperatures");
            }
        }
        else {
            temper = false;
            if (playerstemp.contains(userId)) k=1;
            else k=0;
            if(k==1) {
                /*we delete the user from playerstemp arraylist*/
                playerstemp.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from temperatures");
            }
        }
        if(hum.isChecked()){
            humid = true;
            if (playershum.contains(userId)) l=1;
            if(l==0) {
                /*we save the user to playershum arraylist*/
                playershum.add(userId);
                System.out.println("Player " +userId+ " subscribed to humidities");
            }
        }
        else {
            humid = false;
            if (playershum.contains(userId)) l=1;
            else l=0;
            if(l==1) {
                /*we delete the user from playerstemp arraylist*/
                playershum.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from humidities");
            }
        }
        OurText.setText("You subscribed successfully. You can leave the app now.");
        timer.schedule(task, DELAY, DELAY);

        /*if(userId!=null) EsperTemperature.whenButtonClicked(userId, 1);
        else System.out.println("Null UserId. Can't send notification");*/
    }

    /*epistrefei tous paiktes pou ekanan subscribe sto temperature*/
    public static ArrayList<String> getPlayersTemp() {
        return playerstemp;
    }
    /*epistrefei tous paiktes pou ekanan subscribe sto temperature*/
    public static ArrayList<String> getPlayersHum() {
        return playershum;
    }
}

