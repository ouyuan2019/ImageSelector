package com.ozy.imageselector;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.ozy.imageselector.ui.ImageSelectorActivity;

public class ImageSelector {

    private static ImageSelector instance;

    private ImageSelectorConfig mImageSelectorConfig;

    public static ImageSelector getInstance() {
        if (instance == null) {
            synchronized (ImageSelector.class) {
                if (instance == null) {
                    instance = new ImageSelector();
                }
            }
        }
        return instance;
    }

    public ImageSelector setImageConfig(ImageSelectorConfig config){
            this.mImageSelectorConfig = config;
            return this;
    }

    public ImageSelectorConfig getImageSelectorConfig() {
        return mImageSelectorConfig;
    }

    public void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public void start(Fragment fragment,int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), ImageSelectorActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }
}
