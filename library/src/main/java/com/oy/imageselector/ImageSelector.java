package com.oy.imageselector;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.oy.imageselector.ui.ImagePreivewActivity;
import com.oy.imageselector.ui.ImageSelectorActivity;

import java.util.ArrayList;
import java.util.List;

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

    public ImageSelector setImageConfig(ImageSelectorConfig config) {
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

    public void start(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), ImageSelectorActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }


    public void preview(Activity activity, List<String> selectedList, int requestCode) {
        Intent intent = new Intent(activity, ImagePreivewActivity.class);
        intent.putExtra(ImagePreivewActivity.EXTRA_PREVIEW_SELECT_LIST, (ArrayList) selectedList);
        activity.startActivityForResult(intent, requestCode);
    }
}
