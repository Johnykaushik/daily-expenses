package com.kharche.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpeechParser {
    String userInput;

    private final String currencySim = "₹";

    public SpeechParser(String userInput) {
        this.userInput = userInput;
    }


    public String getAmount() {
        String out = "";

        Pattern pattern = Pattern.compile("₹\\S+");
        Matcher matcher = pattern.matcher(userInput);
        Log.d("TAG", "getAmount: " + matcher.find() + " user input " + userInput);
        if (matcher.find()) {
            String match = matcher.group();
            Log.d("TAG", "getAmount:  11" + matcher.group() + " user input " + userInput);

            out = match;
        }
        return out;
    }

    public Map<String, String> proccessUserVoiceText() {
        Map<String, String> output = new HashMap<>();

        String amount = getAmount();
        Log.d("TAG", "proccessUserVoiceText: " + amount);
        return output;
    }
}
