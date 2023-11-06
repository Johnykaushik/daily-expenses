package com.kharche.model;

import java.util.ArrayList;
import java.util.List;

public class SmsThread {
    private String address; // Contact phone number
    private List<SmsMessage> messages; // List of SMS messages

    public SmsThread(String address) {
        this.address = address;
        this.messages = new ArrayList<>();
    }

    public String getAddress() {
        return address;
    }

    public List<SmsMessage> getMessages() {
        return messages;
    }

    public void addMessage(SmsMessage message) {
        messages.add(message);
    }
}
