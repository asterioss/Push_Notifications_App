package com.example.testapplication;

import android.os.StrictMode;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class Rabbit_Message  {
    private final static String QUEUE_NAME = "hello";
    private static final String TASK_QUEUE_NAME = "task_queue";

    private static Random generator=new Random();

    public static void sendMessage() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setHost("192.168.1.11");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
         /*Connection connection;
        Channel channel;*/
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            //channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            //String message = String.join(" ", argv);
            int i;
            for(i=0; i<100; i++) {
                int j = generator.nextInt(100);
                String message = "" + j;
                //System.out.println("Message value="+message);

                channel.basicPublish("", TASK_QUEUE_NAME,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes("UTF-8"));
                //channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent '" + message + "'");
            }
            /*channel.close();
            connection.close();*/

        }
    }

    public static void receiveMessage() throws Exception {
        List<Integer> temps = new ArrayList<>();   /*arraylist with the temperatures*/
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setHost("192.168.1.11");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //final
        final Connection connection = factory.newConnection();
        //final
        final Channel channel = connection.createChannel();

        //channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        channel.basicQos(1);

        AtomicInteger j = new AtomicInteger(1);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            if(temps.size()<=99) {
                System.out.println("Size: "+ temps.size());
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");

                try {
                    doWork(message);
                } finally {
                    System.out.println(" [x] Done");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    int mes = Integer.parseInt(message);
                    temps.add(mes);
                    j.getAndIncrement();

                    if (j.get() == 100) {
                        EsperTemperature.checkTemperatureEvents((ArrayList<Integer>) temps);
                        //System.out.println("Size: "+ temps.size());
                    /*try {
                        channel.close();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }*/
                        /*connection.close();*/

                    }
                }
            }



            /*try {
                doWork(message);
            } finally {
                System.out.println(" [x] Done");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                int mes=Integer.parseInt(message);
                //System.out.println("String to Integer="+mes);
                temps.add(mes);
            }*/
        };

        //channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
        channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> { });
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

