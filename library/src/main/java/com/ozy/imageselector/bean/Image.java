package com.ozy.imageselector.bean;

import java.io.Serializable;

public class Image implements Serializable{
    public String path;
    public String name;

    public Image(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public Image() {
    }
}
