package com.example.testapplication;

import android.os.StrictMode;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The second rabbitmq class, which is called when user click the button and it calls the sendNotification class.
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
        factory.setHost("192.168.1.8");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            String message = null;
            if(player==null || category==null) System.out.println("No Notifications");
            else {
                message = category;
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
                //for(String player : players) {
                SendNotification.sendNotification(player, category, product, product_url, date);
                //}
            }
            /*o user den exei kanei check tipota, ara den stelnei notification*/
            /*if(temp==0 && hum==0) System.out.println("No Notifications");
            //o user exei kanei check kai ta 2
            else if(temp!=0 && hum!=0) {
                int i;
                for(i=0; i<2; i++) {
                    if(i==0) {
                        message = "" +temp;
                    }
                    if(i==1) {
                        message = "" +hum;
                    }

                    channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                    System.out.println(" [x] Sent '" + message + "'");
                }
                SendNotification.sendTempetatureNotification(player, temp, date);
                SendNotification.sendHumidityNotification(player, hum, date);
            }
            //o user exei kanei check se ena apo ta 2
            else {
                if(temp!=0) {
                    message = "" +temp;
                }
                if(hum!=0) {
                    message = "" +hum;
                }

                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
                if(temp!=0) SendNotification.sendTempetatureNotification(player, temp, date);
                if(hum!=0) SendNotification.sendHumidityNotification(player, hum, date);
            }*/
            //channel.close();
            //connection.close();

        }
    }

    public static void ReceiveEvents(String player, int temp, int hum, String date) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        //set the heartbeat timeout to 60 seconds
        factory.setRequestedHeartbeat(60);
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setVirtualHost("/");
        factory.setHost("192.168.1.8");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        AtomicInteger i= new AtomicInteger(1);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message;
            if(temp==0 && hum==0) System.out.println("No Notifications");
            else if(temp!=0 && hum!=0) {
                System.out.println("First Receive.");

                if(i.get() == 1) SendNotification.sendTempetatureNotification(player, temp, date);
                if(i.get() == 2) {
                    SendNotification.sendHumidityNotification(player, hum, date);
                    i.set(0);
                }
                System.out.println("i=" +i);
                i.getAndIncrement();
                message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
            else {
                System.out.println("Second Receive.");
                message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                if(temp!=0) {
                    SendNotification.sendTempetatureNotification(player, temp, date);
                }
                if(hum!=0) {
                    SendNotification.sendHumidityNotification(player, hum, date);
                }
            }
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        //channel.close();
        //connection.close();
    }

}