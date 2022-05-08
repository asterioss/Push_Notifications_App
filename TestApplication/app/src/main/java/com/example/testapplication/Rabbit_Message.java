package com.example.testapplication;

import android.os.Build;
import android.os.StrictMode;

import com.espertech.esper.client.EPRuntime;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The first RabbitMQ class, which receives the events from an another project
 * and it sends the messages (client_id, product_id, category_id) to Main Activity.
 *
 */
public class Rabbit_Message  {
    private final static String QUEUE_NAME = "Notification_queue";
    public static EPRuntime runtime;

    private static Random generator=new Random();

    public Rabbit_Message() {}

    //@RequiresApi(api = Build.VERSION_CODES.O)  //for date
    //this method is used to take the events from an another project
    public static void receiveMessage() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setVirtualHost("/");
        factory.setHost("192.168.1.3");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            //deserialize the object
            Deserializer deserializer= new Deserializer();
            Object tmpev = deserializer.deserialize(delivery.getBody());

            System.out.println(" [x] Received " + tmpev + "");
            //convert object to string
            String convertedToString = String.valueOf(tmpev);
            //split by comma
            String[] NumberArray = convertedToString.split(",");

            int clientId, categoryId, productId;
            clientId = Integer.parseInt(NumberArray[0]);
            productId = Integer.parseInt(NumberArray[1]);
            categoryId = Integer.parseInt(NumberArray[2]);
            //System.out.println(clientId);
            MainActivity.BeforeSend(clientId, productId, categoryId);
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }

    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

