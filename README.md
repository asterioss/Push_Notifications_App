## Push Notification App

Push notifications are short messages that pop up on the user’s mobile or desktop and they are designed to catch the user's attention by prompting him to take some action. In short, push notifications are alerts that are "pushed" to the devices by applications, even when those applications aren't open.

This project is the design and the implementation of an application that sends push notifications to an android device depending on the customer's position in a store. 
The user of the application has the ability to decide when he wants to receive a notification, by subscribing to the categories in which he is interested. 

For the practical part of the work, the application was developed in native Android using Android Studio and writing in Java code. Also, I used the OneSignal API for sending push notifications, the RabbitMQ service for receiving messages (critical events) from an another project and the MySQL database for storing and searching information.

# Τechnologies and services
* Android SDK: It includes the Android Java libraries that are used by the application, the emulator for the test run of the application, the compiler that generates the code and a variety of different libraries.
* OneSignal: It is a push notification service and it allows the application to send push notifications to any platform (Android, IOS, Web).
* RabbitMQ: It is a messaging software that enables the application to send and receive messages. That is, it allows the connection of applications to transfer one or more messages.
* Android Studio: It is a complete programming environment for developing applications on the Android platform.
* MySQL: It is a popular database management system for web applications and websites.

# How to run
1. Clone this repository locally
2. Start the RabbitMQ service
3. Start the MySQL server and import the above databases (ProximiotDB.sql, CMS.sql)
4. Clone this project locally (https://github.com/Kampadais/EsperProducts) and run it
5. Open and run the project in Android Studio

# Use of the application
The user just installs the application on his mobile phone and when the application is opened for the first time, he subscribes to the push notifications of OneSignal (ie he can now receive notifications on his smartphone). Then the user sees the home screen, where there are different product categories and he has the freedom to register in the categories that interest him and wants to receive a notification.

# Execution of the application
Below it is an illustration of the application model and it describes the procedure followed to send a notification to the user's mobile.
![model_app_ready](https://user-images.githubusercontent.com/57050529/174345775-5daeef1b-e403-4c79-a75a-9b0a4df71f37.jpg)
