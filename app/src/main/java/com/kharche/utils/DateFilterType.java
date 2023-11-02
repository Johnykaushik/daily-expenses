package com.kharche.utils;

public enum DateFilterType {
    SELECT_DATE_TYPE("Select Date Type"),
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    CURRENT_WEEK("Current Week"),
    LAST_WEEK("Last Week"),
    LAST_MONTH("Last Month"),
    CURRENT_MONTH("Current Month"),
    CUSTOM_DATE("Custom Date");

    private final String value;

    DateFilterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


