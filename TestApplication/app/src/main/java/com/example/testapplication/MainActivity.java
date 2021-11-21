package com.example.testapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements OSPermissionObserver,OSSubscriptionObserver {

    //private static final String ONESIGNAL_APP_ID = "1e9efea7-8568-4adb-acff-42527a5855bf";
    static boolean checkFirst=false;
    static int first=0;
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

    public void onClick(View view) {
        //Intent intent = new Intent(Intent.ACTION_DIAL);
        OSDeviceState device = OneSignal.getDeviceState();

        //get player_id, who press the button
        String userId = device.getUserId();
        //System.out.println("PlayerID:"+ userId);
        //if(first==0) {
         /*  try {
                Rabbit_Message.receiveMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
           // first++;
        //}*/
        /*try {
            Rabbit_Message.sendMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //parameters player_id kai arithmos button
        if(userId!=null) EsperTemperature.whenButtonClicked(userId, 1);
        else System.out.println("Null UserId. Can't send notification");
        //startActivity(intent);
    }

    public void onClickButton2(View view) {
        OSDeviceState device = OneSignal.getDeviceState();

        //get player_id, who press the button
        String userId = device.getUserId();
        //System.out.println("PlayerID:"+ userId);

        //if(first==0) {
          /*  try {
                Rabbit_Message.receiveMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            first++;*/
        //}
       /*try {
            Rabbit_Message.sendMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        if(userId!=null) EsperTemperature.whenButtonClicked(userId, 2);
        else System.out.println("Null UserId. Can't send notification");
    }
}

