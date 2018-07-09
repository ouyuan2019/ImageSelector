package com.ozy.imageselector.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.ozy.imageselector.ImageSelector;
import com.ozy.imageselector.ImageSelectorConfig;
import com.ozy.imageselector.R;
import com.ozy.imageselector.adapter.ImageAdapter;
import com.ozy.imageselector.bean.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageSelectorActivity extends AppCompatActivity {

    public static final String RESULT = "RESULT";

    private static final String TAG = "ImageSelectorActivity";

    private ImageSelectorConfig mConfig;

    private RecyclerView mRecyclerView;

    private List<Image> mImageList = new ArrayList<>();
    private ImageAdapter mImageAdapter;

    private static final int STORAGE_REQUEST_CODE = 1;

    private static final int LOADER_ALL = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selector);

        mConfig = ImageSelector.getInstance().getImageSelectorConfig();
        //6.0权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_REQUEST_CODE);
        } else {
            initView();
        }
    }

    private void initView() {

        if (mConfig == null) {
            return;
        }
        mRecyclerView = findViewById(R.id.recyclerView);
        mImageAdapter = new ImageAdapter(this, mConfig);
        mRecyclerView.setAdapter(mImageAdapter);
        this.getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {


        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media._ID};

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            return new CursorLoader(ImageSelectorActivity.this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            if (data == null) {
                return;
            }
            int count = data.getCount();
            if (count > 0) {
                data.moveToFirst();
                while (data.moveToNext()) {
                    String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                    Image image = new Image(path, name);
                    mImageList.add(image);
                }
                if (mConfig.needCamera) {
                    mImageList.add(0, new Image());
                }
                Log.w(TAG, "====>>" + mImageList.size());
                mImageAdapter.setAdapter(mImageList);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    };
}
