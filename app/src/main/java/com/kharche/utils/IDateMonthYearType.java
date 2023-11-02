package com.kharche.utils;
public enum IDateMonthYearType {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year");

    private final String value;

    IDateMonthYearType(String value) {

        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


