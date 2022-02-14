package com.example.testapplication;

import android.os.StrictMode;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * The sendNotification class, which sends the notification to user's mobile phone.
 *
 */
public class SendNotification {

    public static void sendTempetatureNotification(String player_id, int temperature, String date) {
        //send a temperature notification
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic MTE3ZDI2YTMtMTRiYi00Nzg1LTkwNDAtZmY5MjdkYzJlMTA2");
            con.setRequestMethod("POST");
            String strJsonBody = "{"
                    +   "\"app_id\": \"1e9efea7-8568-4adb-acff-42527a5855bf\","
                    +   "\"include_player_ids\": [\""+player_id+"\"],"
                    +   "\"android_accent_color\": \"FFE9444E\","
                    +   "\"url\": \"https://www.meteo.gr/cf.cfm?city_id=23\","
                   // +   "\"included_segments\": [\"Subscribed Users\"],"
                   // +   "\"data\": {\"customKey\": \"https://www.youtube.com\"},"
                    +   "\"headings\": {\"en\": \"TEMPERATURE "+temperature+"°C at Heraklion,Crete\"},"
                    +   "\"contents\": {\"en\": \"Be careful. Very high temperature at "+date+".\"}"
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

            if (httpResponse >= HttpURLConnection.HTTP_OK
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

    public static void sendHumidityNotification(String player_id, int humidity, String date) {
        //send a humidity notification
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic MTE3ZDI2YTMtMTRiYi00Nzg1LTkwNDAtZmY5MjdkYzJlMTA2");
            con.setRequestMethod("POST");
            String strJsonBody = "{"
                    +   "\"app_id\": \"1e9efea7-8568-4adb-acff-42527a5855bf\","
                    +   "\"include_player_ids\": [\""+player_id+"\"],"
                    +   "\"android_accent_color\": \"FFE9444E\","
                    +   "\"url\": \"https://www.meteo.gr/cf.cfm?city_id=23\","
                    // +   "\"included_segments\": [\"Subscribed Users\"],"
                    // +   "\"data\": {\"customKey\": \"https://www.youtube.com\"},"
                    +   "\"headings\": {\"en\": \"HUMIDITY "+humidity+"% at Heraklion,Crete\"},"
                    +   "\"contents\": {\"en\": \"Be careful. Very high humidity at "+date+".\"}"
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

            if (httpResponse >= HttpURLConnection.HTTP_OK
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