package com.oy.imageselector.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ouyuan on 2018/7/10
 */
public class Folder {
    private String name;
    private String path;
    private String firstImagePath;
    private int imageNum;


    private List<Image> imageList = new ArrayList<>();

    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
