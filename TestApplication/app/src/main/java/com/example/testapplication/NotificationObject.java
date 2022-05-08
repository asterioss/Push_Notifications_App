package com.example.testapplication;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class takes the notification from the RabbitMQ service and it analyzes it.
 *
 */
public class NotificationObject {
    private int clientID;
    private int productID;
    private int productCategoryID;

    public NotificationObject(@JsonProperty("clientID")int clientID, @JsonProperty("productID")int productID, @JsonProperty("productCategoryID")int productCategoryID){
        this.clientID=clientID;
        this.productID=productID;
        this.productCategoryID=productCategoryID;
    }

    public int getClientID() {
        return clientID;
    }

    public int getProductCategoryID() {
        return productCategoryID;
    }

    public int getProductID() {
        return productID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public void setProductCategoryID(int productCategoryID) {
        this.productCategoryID = productCategoryID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    @Override
    public String toString() {
        return ""+this.clientID+","+this.productID+","+this.productCategoryID+"";
    }

    public byte[] serialize(Object arg1) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(arg1).getBytes();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }
}