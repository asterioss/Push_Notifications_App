package com.example.testapplication;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

/**
 * This class is used for the deserialization of an object.
 *
 */
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