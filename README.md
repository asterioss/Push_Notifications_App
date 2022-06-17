# Push Notifications App

Push notifications are short messages that pop up on the user’s mobile or desktop and they are designed to catch the user's attention by prompting him to take some action. In short, push notifications are alerts that are "pushed" to the devices by applications, even when those applications aren't open.

This project is the design and the implementation of an application that sends push notifications to an android device depending on the customer's position in a store. The user of the application has the ability to decide when he wants to receive a notification, by subscribing to the categories in which he is interested. Finally, the project is a part of the work ‘ProximIoT’ (proximiot.com) from ICS-FORTH, which aims to design and develop an IoT platform for Proximity Marketing.

For the practical part of the work, the application was developed in native Android using Android Studio and writing in Java code. Also, I used the OneSignal API for sending push notifications, the RabbitMQ service for receiving messages (critical events) from an another project and the MySQL database for storing and searching information.

## Τechnologies and services
* Android SDK: It includes the Android Java libraries that are used by the application, the emulator for the test run of the application, the compiler that generates the code and a variety of different libraries.
* OneSignal: It is a push notification service and it allows the application to send push notifications to any platform (Android, IOS, Web).
* RabbitMQ: It is a messaging software that enables the application to send and receive messages. That is, it allows the connection of applications to transfer one or more messages.
* Android Studio: It is a complete programming environment for developing applications on the Android platform.
* MySQL: It is a popular database management system for web applications and websites.

## How to run
1. Clone this repository locally
2. Start the RabbitMQ service
3. Start the MySQL server and import the above databases (ProximiotDB.sql, CMS.sql)
4. Clone this project locally (https://github.com/Kampadais/EsperProducts) and run it. Μore specifically, I collaborated with a colleague on this project, where with the help of the RabbitMQ, the communication between our programs was achieved in order to transfer one or more messages. That is, once an important event is detected in the colleague's program (a customer of the store is close to a product for a long time), the application receives a message such as the following:

```[*] Waiting for messages. To exit press CTRL+C```

```[x] Received 22, 56, 22```

(Received client_id, category_id, product_id, where the specific client is interested for the specific product of the category)
6. Open and run the project in Android Studio

## Use of the application
The user just installs the application on his mobile phone and when the application is opened for the first time, he automatically subscribes to the push notifications of OneSignal (he can now receive notifications on his smartphone). Then the user sees the home screen, where there are different product categories and he has the freedom to subscribe in the categories that interest him and he wants to receive a notification.

## Execution of the application
Below it is an illustration of the application model and it describes the procedure followed to send a notification to the user's mobile.

<img width="700" src="https://user-images.githubusercontent.com/57050529/174345775-5daeef1b-e403-4c79-a75a-9b0a4df71f37.jpg">

Initially, in step 1 the system starts with the Main Activity that provides the initial screen of the application to the user and and it initializes the OneSignal, which is needed after for the push notifications. At the same time, it calls a RabbitMQ service and in step 2 the application starts waiting to receive messages using RabbitMQ. Then, after the program receives the message (critical event) from my colleague's project, a second RabbitMQ service is called in step 3, where it is checked if the user (client) is subscribed in the specific category of the product that he is interested and it sends the appropriate message to OneSignal. In step 4 using the OneSignal API, in case the user has subscribed in the category, he receives a notification on his mobile with information or discounts for the specific product. Otherwise, if the user is not subscribed in the specific category, he doesn't receive a notification and the application continues to wait for messages.
