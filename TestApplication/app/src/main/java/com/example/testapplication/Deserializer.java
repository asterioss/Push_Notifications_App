package com.example.testapplication;

import com.fasterxml.jackson.databind.ObjectMapper;
//import kampia.esperLocation.EventTypes.ClientCloseEvent;
//import kampia.esperLocation.EventTypes.Location;

import java.io.Serializable;
import java.util.Map;

//This class is used for the deserialization of the object
public class Deserializer<T extends Serializable>  {

    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final String VALUE_CLASS_NAME_CONFIG = "value.class.name";

    public static Object deserialize(byte[] data) {
        ObjectMapper mapper = new ObjectMapper();
        NotificationObject notif = null;
        try {
            notif = mapper.readValue(data, NotificationObject.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  notif;
    }

}