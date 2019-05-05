package com.guangmushikong.lbi.model;

public enum  ServiceType {
    XYZ(1),TMS(2);

    int value;
    String code;

    ServiceType(int val){
        this.value=val;
    }
    public int getValue(){
        return this.value;
    }
    public static ServiceType getByValue(int value) {
        for(ServiceType val :values()){
            return val;
        }
        return null;
    }
}
