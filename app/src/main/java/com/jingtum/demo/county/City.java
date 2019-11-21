package com.jingtum.demo.county;

import java.io.Serializable;

/**
 * Created by yalei on 2016/10/26.
 */

public class City implements Serializable {
    private String name;
    private String pinyin;
    private String area;

    public City(String name, String pinyin, String area) {
        this.pinyin = pinyin;
        this.name = name;
        this.area = area;
    }

    public City() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", area='" + area + '\'' +
                '}';
    }
}
