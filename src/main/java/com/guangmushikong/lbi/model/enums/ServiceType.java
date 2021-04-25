package com.guangmushikong.lbi.model.enums;

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
        for(ServiceType type :values()){
            if(type.getValue()==value){
                return type;
            }
        }
        return null;
    }
}
