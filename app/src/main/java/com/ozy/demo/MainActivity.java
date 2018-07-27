package com.ozy.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.oy.imageselector.ImageSelector;
import com.oy.imageselector.ImageSelectorConfig;
import com.oy.imageselector.bean.Image;
import com.oy.imageselector.loader.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Multiselect();
            }
        });


    }

    public void Multiselect() {
        ImageSelectorConfig config = new ImageSelectorConfig.Builder().imageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context.getApplicationContext())
                        .load(new File(path))
                        .thumbnail(0.7f)
                        .dontAnimate()
                        .centerCrop()
                        .into(imageView);
            }
        }).needCamera(true).build();
        ImageSelector.getInstance().setImageConfig(config).start(MainActivity.this, 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 111) {

            ArrayList<Image> list = (ArrayList<Image>) data.getSerializableExtra("REQUEST_OUTPUT");

            List<String>  imageList = new ArrayList<>();

            for (Image image : list) {
                imageList.add(image.getPath());
            }


            ImageSelectorConfig config = new ImageSelectorConfig.Builder().imageLoader(new ImageLoader() {
                @Override
                public void displayImage(Context context, String path, ImageView imageView) {
                    Glide.with(context.getApplicationContext())
                            .load(new File(path))
                            .thumbnail(0.7f)
                            .dontAnimate()
                            .centerCrop()
                            .into(imageView);
                }
            }).needCamera(false).build();

            ImageSelector.getInstance().setImageConfig(config).preview(MainActivity.this, imageList,111);
        }
    }
}

