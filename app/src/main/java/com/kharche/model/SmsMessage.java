package com.kharche.model;

public class SmsMessage {
    private String address; // Contact phone number
    private String body; // SMS message text
    private long date; // Timestamp of the message
    private int messageType; // 1 for received, 2 for sent

    public SmsMessage(String address, String body, long date, int messageType) {
        this.address = address;
        this.body = body;
        this.date = date;
        this.messageType = messageType;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    public long getDate() {
        return date;
    }

    public int getMessageType() {
        return messageType;
    }
}