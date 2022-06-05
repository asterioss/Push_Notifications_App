package com.example.testapplication;

import android.os.StrictMode;

import com.espertech.esper.client.EPRuntime;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * The first RabbitMQ class, which receives the events from an another project
 * and it sends the messages (client_id, product_id, category_id) to a second RabbitMQ class.
 *
 */
public class Rabbit_Message  {
    private final static String QUEUE_NAME = "Notification_queue";
    private static EPRuntime runtime;
    //for MySQL database
    public static final String DB_URL = "jdbc:mysql://192.168.1.7:3306/Cms?autoReconnect=true&useSSL=false";
    public static final String USER = "root";
    public static final String PASS = "root";

    //map the users with the available devices
    static HashMap<Integer, String> usersmapping = new HashMap<>();

    public Rabbit_Message() {}

    //@RequiresApi(api = Build.VERSION_CODES.O)  //for the date
    //this method is used to receive the events from an another project
    public static void receiveMessage() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setVirtualHost("/");
        factory.setHost("192.168.1.7");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            //deserialize the object
            Object tmpev = Deserializer.deserialize(delivery.getBody());

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
            BeforeSend(clientId, productId, categoryId);
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)  //for the date
    //this method gets the message from this RabbitMQ and and it calls the 2nd RabbitMQ to send the notification to the user
    public static void BeforeSend(int clientID, int product_id, int category_id) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String product_name = null, category_name = null, product_url = null;
        java.sql.Connection conn;
        Statement stmt;
        //connect to MySQL database
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                    DB_URL, USER, PASS);
            System.out.println("Connected to database successfully.");
            stmt = conn.createStatement();
            String sql = "SELECT * FROM cms.product_categories where id="+category_id+"";
            ResultSet result = stmt.executeQuery(sql);

            while(result.next()) {
                category_name = result.getString("name");
                System.out.println("Category: "+category_name);
            }

            sql = "SELECT * FROM cms.products where id="+product_id+"";
            result = stmt.executeQuery(sql);

            while(result.next()) {
                product_name = result.getString("sku");
                product_url = result.getString("productUrl");
                System.out.println("Product: "+product_name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } /*finally {
            conn.close();
        }*/

        String player = null, send_player = null;
        //get the devices
        ArrayList<String> devices = MainActivity.getDevices();

        if(devices.size()>0) {
            //Get the iterator over the HashMap
            Iterator<Map.Entry<Integer, String> >
                    iterator = usersmapping.entrySet().iterator();
            //flag to store result
            boolean keyExists = false;

            //Iterate over the HashMap
            while (iterator.hasNext()) {
                //Get the entry at this iteration
                Map.Entry<Integer, String>
                        entry
                        = iterator.next();

                //Check if this key is the required key
                if (clientID == entry.getKey()) {
                    keyExists = true;
                }
            }

            if(keyExists) {
                player=usersmapping.get(clientID);
                System.out.println("Player exists. "+player);
            }
            else {
                //save a new player to hashmap
                Random rand = new Random();
                int random = rand.nextInt(devices.size());

                usersmapping.put(clientID, devices.get(random));
                player=usersmapping.get(clientID);
                System.out.println("New Player. "+player);
            }
        }

        if(player!=null) {
            //match the category and check if the player is subscibed
            if (category_id == 1 && MainActivity.getPlayersTV().contains(player)) send_player = player;
            else if (category_id == 15 && MainActivity.getPlayersFridge().contains(player)) send_player = player;
            else if (category_id == 16 && MainActivity.getPlayersWashMachine().contains(player)) send_player = player;
            else if (category_id == 17 && MainActivity.getPlayersSofa().contains(player)) send_player = player;
            else if (category_id == 18 && MainActivity.getPlayersWardrobe().contains(player)) send_player = player;
            else if (category_id == 19 && MainActivity.getPlayersBed().contains(player)) send_player = player;
            else if (category_id == 20 && MainActivity.getPlayersAirCondition().contains(player)) send_player = player;
            else if (category_id == 21 && MainActivity.getPlayersCellPhone().contains(player)) send_player = player;
            else if (category_id == 22 && MainActivity.getPlayersLaptop().contains(player)) send_player = player;
            else if (category_id == 23 && MainActivity.getPlayersOven().contains(player)) send_player = player;
            else if (category_id == 24 && MainActivity.getPlayersPhone().contains(player)) send_player = player;
        }

        //get the current date (if you want it for the notification)
        /*String date;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        //System.out.println(dtf.format(now));
        date = dtf.format(now);*/

        try {
            //send the events to the second RabbitMQ service
            Rabbit_SendEvents.SendEvents(send_player, category_name, product_name, product_url);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

