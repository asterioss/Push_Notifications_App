package com.example.testapplication;
import android.os.AsyncTask;
import android.os.StrictMode;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class Send {

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

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setHost("192.168.1.3");
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

}