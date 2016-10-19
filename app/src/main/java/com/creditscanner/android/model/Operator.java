package com.creditscanner.android.model;

public class Operator {

    String name;
    int code;
    int imageRes;

    public Operator(String name, int code, int imageRes) {
        this.name = name;
        this.code = code;
        this.imageRes = imageRes;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public int getImageRes() {
        return imageRes;
    }

    @Override
    public String toString() {
        return getName();
    }
}
