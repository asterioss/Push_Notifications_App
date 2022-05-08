package com.example.testapplication;

import android.os.StrictMode;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The second RabbitMQ class, which is called when we have a message
 * and it calls the sendNotification class to send the notification to the user.
 *
 */
public class Rabbit_SendEvents {
    private static final String EXCHANGE_NAME = "logs";

    public static void SendEvents(String player, String category, String product, String product_url, String date) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        //set the heartbeat timeout to 60 seconds
        factory.setRequestedHeartbeat(60);
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setVirtualHost("/");
        factory.setHost("192.168.1.3");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            String message = null;
            /*o player h to category einai kena, ara den stelnei notification*/
            if(player==null || category==null) System.out.println("No Notifications");
            else {
                message = category;
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
                SendNotification.sendNotification(player, category, product, product_url, date);
            }
            //channel.close();
            //connection.close();

        }
    }

}