package com.example.testapplication;


import android.app.Notification;
import android.content.Intent;
import android.nfc.Tag;
import android.os.StrictMode;
import android.util.Log;

import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SendNotification {

    public static void sendDeviceNotification() {
        //post a notification
            /*try {
                JSONObject notificationContent = new JSONObject("{'include_player_ids': ['" + user_id + "']," +
                        "'headings': {'en': 'TEST DEVICE'}," +
                        "'contents': {'en': 'Hello my friend!'}," +
                        //"'android_background_layout': {'headings_color': 'FFFF0000', 'contents_color': 'FF00FF00'}," +
                        "'android_led_color': 'FF3700B3'," +
                        //"'url': 'https://onesignal.com'," +
                        "'android_accent_color': 'FFE9444E'," +
                        "'android_sound': 'nil'}");
                System.out.println("LEGEEE:"+notificationContent.toString());
                OneSignal.userProvidedPrivacyConsent();

                OneSignal.postNotification(notificationContent, new OneSignal.PostNotificationResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                    }

                    @Override
                    public void onFailure(JSONObject response) {
                        Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                    }
                    //return notificationContent;
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }*/


        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic YWU5ODI4YzUtNTU0Ni00M2ZiLWFjY2ItZWI3NWE4ZjUyNDll");
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                    +   "\"app_id\": \"1e9efea7-8568-4adb-acff-42527a5855bf\","
                    +   "\"android_accent_color\": \"FFE9444E\","
                    +   "\"url\": \"https://www.youtube.com\","
                    +   "\"included_segments\": [\"Subscribed Users\"],"
                    +   "\"data\": {\"customKey\": \"https://www.youtube.com\"},"
                    +   "\"headings\": {\"en\": \"TEMPERATURE ALERT\"},"
                    +   "\"contents\": {\"en\": \"Be careful. Very high temperature!\"}"
                    + "}";


            System.out.println("strJsonBody:\n" + strJsonBody);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);

            if (  httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);

        } catch(Throwable t) {
            t.printStackTrace();
        }
    }


}