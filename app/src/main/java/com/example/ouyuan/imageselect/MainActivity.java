package com.example.ouyuan.imageselect;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ozy.imageselector.ImageLoader;
import com.ozy.imageselector.ImageSelector;
import com.ozy.imageselector.ImageSelectorConfig;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageSelectorConfig config = new ImageSelectorConfig.Builder().setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Context mContext, String path, ImageView imageView) {
                        imageView.setImageURI(Uri.fromFile(new File(path)));
                    }
                }).build();
                ImageSelector.getInstance().setImageConfig(config).start(MainActivity.this,111);
            }
        });
    }
}
