package com.ozy.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.oy.imageselector.ImageSelector;
import com.oy.imageselector.ImageSelectorConfig;
import com.oy.imageselector.loader.ImageLoader;

import java.io.File;

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

    public void Multiselect(){
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
}
