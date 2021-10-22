package com.example.testapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Parcelable;
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

public class ApplicationClass extends Application {
    private static final String ONESIGNAL_APP_ID = "1e9efea7-8568-4adb-acff-42527a5855bf";

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


        //NotificationServiceExtension.remote
        OneSignal.setNotificationOpenedHandler(
                result -> {
                    String actionId = result.getAction().getActionId();
                    OSNotificationAction.ActionType type = result.getAction().getType(); // "ActionTaken" | "Opened"

                    String title = result.getNotification().getTitle();
                    System.out.println("Title:" +title);
                    OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "OSNotificationOpenedResult result: " + result.toString());
                });

        OneSignal.setNotificationWillShowInForegroundHandler(notificationReceivedEvent -> {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "NotificationWillShowInForegroundHandler fired!" +
                    " with notification event: " + notificationReceivedEvent.toString());

            OSNotification notification = notificationReceivedEvent.getNotification();
            JSONObject data = notification.getAdditionalData();

            notificationReceivedEvent.complete(notification);
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
        String player1_id = "68756dac-67dd-4de3-99bf-f7cba2a99c5e";
        String player2_id = "7158c2c4-cfd6-4ab6-8549-1936b4de68d2";
        SendNotification.sendDeviceNotification(player1_id);
        SendNotification.sendDeviceNotification(player2_id);
        //NotificationServiceExtension.remoteNotificationReceived();


        /*OneSignal.setNotificationOpenedHandler(result ->
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "OSNotificationOpenedResult result: " + result.toString()));

        OneSignal.setNotificationWillShowInForegroundHandler(notificationReceivedEvent -> {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "NotificationWillShowInForegroundHandler fired!" +
                    " with notification event: " + notificationReceivedEvent.toString());

            OSNotification notification = notificationReceivedEvent.getNotification();
            JSONObject data = notification.getAdditionalData();

            System.out.println("DATA:" +data.toString());
            //notificationReceivedEvent.complete(notification);
        });*/


        // Android SDK 4.x.x
        /*OneSignal.setNotificationOpenedHandler(
                result -> {
                    // Capture Launch URL (App URL) here
                    JSONObject data = result.getNotification().getAdditionalData();
                    Log.i("OSNotification", "data set with value: " + data);
                    if (data != null) {
                        // The following can be used to open an Activity of your choice.
                        // Replace - getApplicationContext() - with any Android Context.
                        // Replace - YOURACTIVITY.class with your activity to deep link
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("data", (Parcelable) data);
                        startActivity(intent);
                    }
                });*/

    }
}
