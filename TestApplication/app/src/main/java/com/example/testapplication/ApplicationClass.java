package com.example.testapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;

import com.onesignal.OSInAppMessage;
import com.onesignal.OSInAppMessageLifecycleHandler;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

/**
 * The main application class where the app is starting.
 *
 */
public class ApplicationClass extends Application {
    private static final String ONESIGNAL_APP_ID = "1e9efea7-8568-4adb-acff-42527a5855bf";

    @Override
    public void onCreate() {
        super.onCreate();

        OSInAppMessageLifecycleHandler handler = new OSInAppMessageLifecycleHandler() {
            //for onesignal nitialization
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

        //Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        //OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        //linking notification to URL (when user clicks the notification, it will send him to a website.
        OneSignal.setNotificationOpenedHandler(
                result -> {
                    //String title = result.getNotification().getTitle();
                    //System.out.println("Title:" +title);

                    String launchURL = result.getNotification().getLaunchURL();
                    Log.i("OneSignalExample", "launchUrl set with value: " + launchURL);

                    if (launchURL != null) {
                        //The following can be used to open an Activity of your choice.
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse(launchURL));
                        Log.i("OneSignalExample", "openURL = " + launchURL);
                        startActivity(intent);
                    }
                });

        OneSignal.setNotificationWillShowInForegroundHandler(notificationReceivedEvent -> {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "NotificationWillShowInForegroundHandler fired!" +
                    " with notification event: " + notificationReceivedEvent.toString());

            OSNotification notification = notificationReceivedEvent.getNotification();
            notificationReceivedEvent.complete(notification);
        });

        //OneSignal.inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true);
        OneSignal.pauseInAppMessages(true);
        OneSignal.setLocationShared(false);

        OneSignal.setRequiresUserPrivacyConsent(true);

        //initialize the OneSignal SDK and enable push notifications
        OneSignal.provideUserConsent(true);

        //boolean locationShared = OneSignal.isLocationShared();

        Log.d("Debug", "ONESIGNAL_SDK_INIT");

        //call rabbitmq to receive the messages
        try {
            Rabbit_Message.receiveMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Rabbit_Message.sendMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Handler handle = new Handler();
        final int delay = 60000; // 1000 milliseconds == 1 second

        //get new messages every minute with this handler
        handle.postDelayed(new Runnable() {
            public void run() {
                System.out.println("myHandler: here!");
                System.out.println("Receiving new messages!");

                try {
                    Rabbit_Message.receiveMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Rabbit_Message.sendMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handle.postDelayed(this, delay);
            }
        }, delay);

    }

}
