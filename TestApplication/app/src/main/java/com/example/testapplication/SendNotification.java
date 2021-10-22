package com.example.testapplication;


import android.app.Notification;
import android.content.Intent;
import android.nfc.Tag;
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

    public static void sendDeviceNotification(String user_id) {
        //post a notification
            try {
                JSONObject notificationContent = new JSONObject("{'include_player_ids': ['" + user_id + "']," +
                        "'title': 'TEST DEVICE'," +
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
            }


       /* try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic NGEwMGZmMjItY2NkNy0xMWUzLTk5ZDUtMDAwYzI5NDBlNjJj");
            con.setRequestMethod("POST");

            String strJsonBody = "{"
                    +   "\"app_id\": \"5eb5a37e-b458-11e3-ac11-000c2940e62c\","
                    +   "\"included_segments\": [\"Subscribed Users\"],"
                    +   "\"data\": {\"foo\": \"bar\"},"
                    +   "\"contents\": {\"en\": \"English Message\"}"
                    + "}";


            System.out.println("strJsonBody:\n" + strJsonBody);

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
        }*/
    }


}