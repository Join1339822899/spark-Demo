package com.example.pojo;

public class TestTxT {
    public String Key;

    public Integer Value;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public Integer getValue() {
        return Value;
    }

    public void setValue(Integer value) {
        Value = value;
    }

    @Override
    public String toString() {
        return "TestTxT{" +
                "Key='" + Key + '\'' +
                ", Value='" + Value + '\'' +
                '}';
    }
}
