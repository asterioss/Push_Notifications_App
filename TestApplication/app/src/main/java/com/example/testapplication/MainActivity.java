package com.example.testapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.onesignal.OSEmailSubscriptionObserver;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OSPermissionObserver;
import com.onesignal.OSPermissionStateChanges;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OSPermissionObserver,OSSubscriptionObserver {

    //private static final String ONESIGNAL_APP_ID = "1e9efea7-8568-4adb-acff-42527a5855bf";
    static boolean checkFirst=false;
    static int first=0;
    CheckBox temp, hum;
    boolean checked1, checked2;
    TextView OurText;
    SharedPreferences Preference;
    static ArrayList<String> playerstemp = new ArrayList<String>(); // Create an ArrayList object
    static ArrayList<String> playershum = new ArrayList<String>(); // Create an ArrayList object

    private final static int DELAY = 25000;
    private final Handler handler = new Handler();
    private final Timer timer = new Timer();
    private final TimerTask task = new TimerTask() {
        private int counter = 0;
        public void run() {
            //System.out.println("mphka re11");
            handler.post(new Runnable() {
                public void run() {
                   /* if(counter==4) {
                        timer.cancel();
                        return;
                    }*/
                    System.out.println("mphka re");
                    EsperTemperature.sendNotificationbyEsper();
                    timer.cancel();
                    //counter++;
                    //Toast.makeText(MainActivity.this, "test", Toast.LENGTH_SHORT).show();
                }
            });
            /*if(++counter == 4) {
                System.out.println("telos thread");
                timer.cancel();
                call_again(timer, task);
            }*/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OneSignal.addPermissionObserver(this);
        OneSignal.addSubscriptionObserver(this);



       /* if(checkFirst==true) System.out.println("already run");
        else runRabbit();*/

        /*final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
           // if(i==0) runRabbit();
            public void onClick(View v) {

                OSDeviceState device = OneSignal.getDeviceState();

                //get player_id, who press the button
                String userId = device.getUserId();
                System.out.println("PlayerID:"+ userId);

                //prepei na stelnw notification sto player_id pou pataei to koumpi
                //thelei parametro to player_id (NA GINEI)
                try {
                    Rabbit_Message.sendMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(userId!=null) EsperTemperature.whenButtonClicked(userId);
                else System.out.println("Null UserId. Can't send notification");
            }
        });*/
    }


    /*class RetrieveFeedTask extends AsyncTask<String, Void, RSSFeed> {

        private Exception exception;

        protected RSSFeed doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                XMLReader xmlreader = parser.getXMLReader();
                RssHandler theRSSHandler = new RssHandler();
                xmlreader.setContentHandler(theRSSHandler);
                InputSource is = new InputSource(url.openStream());
                xmlreader.parse(is);

                return theRSSHandler.getFeed();
            } catch (Exception e) {
                this.exception = e;

                return null;
            } finally {
                is.close();
            }
        }

        protected void onPostExecute(RSSFeed feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }*/

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

    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        if (!stateChanges.getFrom().isSubscribed() &&
                stateChanges.getTo().isSubscribed()) {
            new AlertDialog.Builder(this)
                    .setMessage("You've successfully subscribed to push notifications!")
                    .show();
            // get player ID
            stateChanges.getTo().getUserId();
        }

        Log.i("Debug", "onOSSubscriptionChanged: " + stateChanges);
    }


    @Override
    public void onStart() {
        super.onStart();
        System.out.println("pipees");
        temp=(CheckBox)findViewById(R.id.checkbox_temp);
        hum = (CheckBox)findViewById(R.id.checkbox_hum);
        /*checked = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("temp", false);*/

       // assertTrue(Preference.getBoolean("temp",false));

            //boolean checkedFlag = Preference.getBoolean("temp",false);
           // temp.setChecked(checkedFlag);
           // System.out.println("checked1:"+checkedFlag);
        boolean checked1 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("temp", false);
        temp.setChecked(checked1);

        boolean checked2 = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("hum", false);
        hum.setChecked(checked2);

        //if(checked1==true) temp.setChecked(true);
        //else temp.setChecked(false);
        //ActiveActivitiesTracker.activityStarted();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Preference = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        //SharedPreferences.Editor editor = Preference.edit();
        if(temp.isChecked()) checked1=true;
        else checked1=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("temp", checked1).commit();

        if(hum.isChecked()) checked2=true;
        else checked2=false;
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("hum", checked2).commit();
        //editor.putBoolean("temp", checked1);
        //editor.commit();
        System.out.println("skataa");
        /*PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean("temp", checked).commit();*/
        //ActiveActivitiesTracker.activityStopped();
    }

    //SharedPreferences prefs = this.getSharedPreferences("com.example.myapp", Context.MODE_PRIVATE);

    public void onClick(View view) {
        //Intent intent = new Intent(Intent.ACTION_DIAL);
        OSDeviceState device = OneSignal.getDeviceState();

        //get player_id, who press the button
        String userId = device.getUserId();

        OurText = findViewById(R.id.textView);
        // Is the view now checked?
        //temp=(CheckBox)findViewById(R.id.checkbox_temp);
        //hum=(CheckBox)findViewById(R.id.checkbox_hum);
        /*boolean checked = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("temp", false);
        temp.setChecked(checked);*/

        //OurText = findViewById(R.id.textView);

        int k=0, l=0;
        if(temp.isChecked()){
            temp.setChecked(true);
            if (playerstemp.contains(userId)) k=1;
            if(k==0) {
                playerstemp.add(userId);
                System.out.println("Player " +userId+ " subscribed to temperatures");
                //OurText.setText("You subscribed to temperatures");
            }

        }
        else {
            // Remove the meat
            if (playerstemp.contains(userId)) k=1;
            else k=0;
            if(k==1) {
                playerstemp.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from temperatures");
                //OurText.setText("You unsubscribed from temperatures");
            }
        }
        if(hum.isChecked()){
            if (playershum.contains(userId)) l=1;
            if(l==0) {
                playershum.add(userId);
                System.out.println("Player " +userId+ " subscribed to humidities");
                //OurText.setText("You subscribed to humidities");

            }
        }
        else {
            // I'm lactose intolerant
            if (playershum.contains(userId)) l=1;
            else l=0;
            if(l==1) {
                playershum.remove(userId);
                System.out.println("Player " +userId+ " unsubscribed from humidities");
                //OurText.setText("You unsubscribed from humidities");
            }
        }
        OurText.setText("You subscribed successfully. You can leave the app now.");
        /*try {
            Rabbit_Message.receiveMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Rabbit_Message.sendMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        timer.schedule(task, DELAY, DELAY);
        //timer.cancel();

        //EsperTemperature.sendNotificationbyEsper();

        //parameters player_id kai arithmos button
        /*if(userId!=null) EsperTemperature.whenButtonClicked(userId, 1);
        else System.out.println("Null UserId. Can't send notification");*/
        //startActivity(intent);
    }

   /* public void onClickButton2(View view) {
        OSDeviceState device = OneSignal.getDeviceState();

        //get player_id, who press the button
        String userId = device.getUserId();

        if(userId!=null) EsperTemperature.whenButtonClicked(userId, 2);
        else System.out.println("Null UserId. Can't send notification");
    }*/
    /*epistrefei tous paiktes pou ekanan subscribe sto temperature*/
    public static ArrayList<String> getPlayersTemp() {
        return playerstemp;
    }
    /*epistrefei tous paiktes pou ekanan subscribe sto temperature*/
    public static ArrayList<String> getPlayersHum() {
        return playershum;
    }

   /* public void onCheckboxClicked(View view) {
        OSDeviceState device = OneSignal.getDeviceState();

        //get player_id, who press the button
        String userId = device.getUserId();

        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        int k=0;
        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_temp:
                if (checked) {
                    // Put some meat on the sandwich
                    if (playerstemp.contains(userId)) k=1;
                    if(k==0) {
                        playerstemp.add(userId);
                        System.out.println("Player " +userId+ " subscribed to temperatures");
                    }

                    EsperTemperature.sendNotificationbyEsper();
                }

                else {
                    // Remove the meat
                    playerstemp.remove(userId);
                    System.out.println("Player " +userId+ " unsubscribed from temperatures");
                    for(String temp_player : playerstemp) {
                        System.out.print("Player: "+temp_player + ", ");
                        //SendNotification.sendTempetatureNotification(temp_player, temperature);
                    }
                }
                break;
            case R.id.checkbox_hum:
                if (checked) {
                    // Cheese me
                    if (playershum.contains(userId)) k=1;
                    if(k==0) {
                        playershum.add(userId);
                        System.out.println("Player " +userId+ " subscribed to humidities");

                    }
                    EsperTemperature.sendNotificationbyEsper();
                }

                else {
                    // I'm lactose intolerant
                    playershum.remove(userId);
                    System.out.println("Player " +userId+ " unsubscribed from humidities");
                    for(String temp_player : playershum) {
                        System.out.print("Player: "+temp_player + ", ");
                        //SendNotification.sendTempetatureNotification(temp_player, temperature);
                    }
                }

                break;
            // TODO: Veggie sandwich
        }
    }*/
}

