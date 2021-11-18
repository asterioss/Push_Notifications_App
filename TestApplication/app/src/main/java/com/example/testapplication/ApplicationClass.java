package com.example.testapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;

import com.onesignal.OSInAppMessage;
import com.onesignal.OSInAppMessageLifecycleHandler;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ApplicationClass extends Application {
    private static final String ONESIGNAL_APP_ID = "1e9efea7-8568-4adb-acff-42527a5855bf";
    private boolean checking = false;

    @Override
    public void onCreate() {
        super.onCreate();

        OSInAppMessageLifecycleHandler handler = new OSInAppMessageLifecycleHandler() {
            @Override
            public void onWillDisplayInAppMessage(OSInAppMessage message) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "MainApplication onWillDisplayInAppMessage");
            }
            @Override
            public void onDidDisplayInAppMessage(OSInAppMessage message) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "MainApplication onDidDisplayInAppMessage");
            }
            @Override
            public void onWillDismissInAppMessage(OSInAppMessage message) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "MainApplication onWillDismissInAppMessage");
            }
            @Override
            public void onDidDismissInAppMessage(OSInAppMessage message) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "MainApplication onDidDismissInAppMessage");
            }


        };

        OneSignal.setInAppMessageLifecycleHandler(handler);

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        //SendNotification.sendDeviceNotification();


        //linking notification to URL
        OneSignal.setNotificationOpenedHandler(
                result -> {
                    //String title = result.getNotification().getTitle();
                    //System.out.println("Title:" +title);
                    //OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "OSNotificationOpenedResult result: " + result.toString());

                    String launchURL = result.getNotification().getLaunchURL();
                    Log.i("OneSignalExample", "launchUrl set with value: " + launchURL);
                    //JSONObject data = result.getNotification().getAdditionalData();
                    //String customKey;
                    //Log.i("OSNotification", "data set with value: " + data);
                    if (launchURL != null) {
                        //customKey = data.optString("customkey", null);
                        // The following can be used to open an Activity of your choice.
                        // Replace - getApplicationContext() - with any Android Context.
                        // Replace - YOURACTIVITY.class with your activity to deep link
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                        //intent.putExtra("openURL", launchURL);
                        intent.setData(Uri.parse(launchURL));
                        Log.i("OneSignalExample", "openURL = " + launchURL);
                        startActivity(intent);
                    }
                });

        OneSignal.setNotificationWillShowInForegroundHandler(notificationReceivedEvent -> {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "NotificationWillShowInForegroundHandler fired!" +
                    " with notification event: " + notificationReceivedEvent.toString());

            OSNotification notification = notificationReceivedEvent.getNotification();
            JSONObject data = notification.getAdditionalData();
            //System.out.println(notification.getTitle());

            notificationReceivedEvent.complete(notification);
            //System.out.println(notification.getTitle());
        });

        OneSignal.unsubscribeWhenNotificationsAreDisabled(true);
        OneSignal.pauseInAppMessages(true);
        OneSignal.setLocationShared(false);

        OneSignal.setRequiresUserPrivacyConsent(true);
        //public void onUserTappedProvidePrivacyConsent(View v) {
        //will initialize the OneSignal SDK and enable push notifications
        OneSignal.provideUserConsent(true);
        //}
        //boolean locationShared = OneSignal.isLocationShared();

        Log.d("Debug", "ONESIGNAL_SDK_INIT");
        //System.out.println("EKEIEIIEIE\n");
        //SendNotification.sendDeviceNotification();
        //EsperTemperature.checkTemperatureEvents();

       /* try {
            Rabbit_Message.receiveMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //if(checking==false) {
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
       // }
        checking = true;


    }
}
