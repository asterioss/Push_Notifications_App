package com.example.testapplication;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Rabbit_Message  {
    private final static String QUEUE_NAME = "hello";
    private static final String TASK_QUEUE_NAME = "task_queue";

    /*public static void rabbitSend() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        //factory.setUri("amqp://guest:guest@192.168.1.4:15672/");
        //factory.setAutomaticRecoveryEnabled(false);
        factory.setHost("localhost");
        //System.out.println("Host="+factory.getHost());

        Thread publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                      try (Connection connection = factory.newConnection();
                      Channel channel = connection.createChannel()) {
                      channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                      String message = "Hello World!";
                      channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                      System.out.println(" [x] Sent '" + message + "'");
                      } catch (Exception e) {
                          Log.d("", "Connection broken: " + e.getClass().getName());
                             try {
                                Thread.sleep(5000); //sleep and then try again
                             } catch (InterruptedException e1) {
                                    break;
                             }
                      }
                }
            }
       });
        publishThread.start();

    }*/
    private static Random generator=new Random();

    public static void sendMessage() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setHost("192.168.1.3");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

            //String message = String.join(" ", argv);
            int i;
            for(i=1; i<21; i++) {
                int j = generator.nextInt(60);
                String message = "" + j;
                System.out.println("Message value="+message);

                channel.basicPublish("", TASK_QUEUE_NAME,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
            }

            /*channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World!";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");*/
        }
    }

    public static void receiveMessage() throws Exception {
        List<Integer> temps = new ArrayList<Integer>();   /*arraylist with the temperatures*/
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setHost("192.168.1.3");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        //channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");

            try {
                doWork(message);
            } finally {
                System.out.println(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                int mes=Integer.parseInt(message);
                System.out.println("String to Integer="+mes);
                temps.add(mes);
            }
        };
        //channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
        channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        EsperTemperature.checkTemperatureEvents((ArrayList<Integer>) temps);
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

