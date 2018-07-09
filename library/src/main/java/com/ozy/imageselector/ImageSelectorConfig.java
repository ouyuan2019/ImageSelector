package com.ozy.imageselector;

import android.os.Environment;

import com.ozy.imageselector.utils.FileUtils;

import java.io.Serializable;

public class ImageSelectorConfig implements Serializable {

    /**
     * 是否多选
     */
    public boolean multiSelect = false;

    /**
     * 是否需要裁剪
     */
    public boolean needCrop;

    /**
     * 最多选择图片数
     */
    public int maxNum = 9;

    /**
     * 第一个item是否显示相机
     */
    public boolean needCamera;

    public ImageLoader imageLoader;


    /**
     * 裁剪输出大小
     */
    public int aspectX = 1;
    public int aspectY = 1;
    public int outputX = 500;
    public int outputY = 500;

    public ImageSelectorConfig(Builder builder) {
        this.imageLoader = builder.imageLoader;
        this.maxNum = builder.maxNum;
    }


    public static class Builder implements Serializable {
        private boolean needCrop = false;
        private boolean multiSelect = true;
        private int maxNum = 9;
        private int aspectX = 1;
        private int aspectY = 1;
        private int outputX = 400;
        private int outputY = 400;
        private String filePath;
        private ImageLoader imageLoader;


        public Builder setMaxNum(int maxNum) {
            this.maxNum = maxNum;
            return this;
        }

        public Builder setImageLoader(ImageLoader imageLoader) {
            this.imageLoader = imageLoader;
            return this;
        }

        public Builder() {
            if (FileUtils.isSdCardAvailable())
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Camera";
            else
                filePath = Environment.getRootDirectory().getAbsolutePath() + "/Camera";

        }

        public ImageSelectorConfig build() {
            return new ImageSelectorConfig(this);
        }
    }


}
