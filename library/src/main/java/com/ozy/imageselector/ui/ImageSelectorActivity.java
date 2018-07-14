package com.ozy.imageselector.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ozy.imageselector.ImageSelector;
import com.ozy.imageselector.ImageSelectorConfig;
import com.ozy.imageselector.R;
import com.ozy.imageselector.adapter.ImageAdapter;
import com.ozy.imageselector.adapter.ImageFolderAdapter;
import com.ozy.imageselector.bean.Folder;
import com.ozy.imageselector.bean.Image;
import com.ozy.imageselector.loader.LocalMediaLoader;
import com.ozy.imageselector.utils.FileUtils;
import com.ozy.imageselector.utils.GridSpacingItemDecoration;
import com.ozy.imageselector.utils.UiUtils;
import com.ozy.imageselector.widget.FolderWindow;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class ImageSelectorActivity extends AppCompatActivity {

    public static final String RESULT = "RESULT";

    private static final String TAG = "ImageSelectorActivity";
    private static final int REQUEST_CAMERA = 3;
    private static final String REQUEST_OUTPUT = "REQUEST_OUTPUT";

    private ImageSelectorConfig mConfig;

    private RecyclerView mRecyclerView;

    private TextView mTvDone;

    private TextView mTvPreview;

    private TextView mTvFolderName;

    private ImageView mBtnBack;

    private ImageAdapter mImageAdapter;

    private FrameLayout mTitleView;

    private static final int STORAGE_REQUEST_CODE = 1;

    private static final int CAMERA_REQUEST_CODE = 2;

    private FolderWindow mFolderWindow;

    private String mCameraPath;

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
        mFolderWindow = new FolderWindow(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mTvFolderName = (TextView) findViewById(R.id.folder_name);
        mTitleView = (FrameLayout) findViewById(R.id.toolbar);
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mTvDone = (TextView) findViewById(R.id.tv_done);
        mTvPreview = (TextView) findViewById(R.id.tv_preview);
        mImageAdapter = new ImageAdapter(this, mConfig);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mRecyclerView.setAdapter(mImageAdapter);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, UiUtils.dip2px(this, 2), false));
        new LocalMediaLoader(this).loadAllMedia(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<Folder> folders) {
                if (folders == null || folders.isEmpty()) {
                    return;
                }
                mFolderWindow.setData(folders);
                mImageAdapter.setAdapter(folders.get(0).getImageList());
            }
        });
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mImageAdapter.setOnImageSelectChangedListener(new ImageAdapter.OnImageSelectChangedListener() {
            @Override
            public void onChange(List<Image> selectImages) {
                int size = selectImages.size();
                boolean enable = size != 0;
                mTvDone.setEnabled(enable);
                mTvPreview.setEnabled(enable);
                if (enable) {
                    mTvDone.setText(getString(R.string.selected_done_text, String.valueOf(size), String.valueOf(mConfig.maxNum)));
                    mTvPreview.setText(getString(R.string.selected_preview_text, String.valueOf(size)));
                } else {
                    mTvDone.setText(getString(R.string.done_text));
                    mTvPreview.setText(getString(R.string.preview_text));
                }
            }

            @Override
            public void onTakePhoto() {
                //6.0权限
                if (ContextCompat.checkSelfPermission(ImageSelectorActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ImageSelectorActivity.this, new String[]{Manifest.permission.CAMERA},
                            CAMERA_REQUEST_CODE);
                } else {
                    startCamera();
                }
            }

            @Override
            public void onPictureClick(Image media, int position) {

            }
        });

        mTvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDone();
            }
        });

        mFolderWindow.setOnItemClickListener(new ImageFolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String folderName, List<Image> images) {
                mFolderWindow.dismiss();
                mImageAdapter.setAdapter(images);
                mTvFolderName.setText(folderName);
            }
        });

        findViewById(R.id.folder_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFolderWindow.isShowing()) {
                    mFolderWindow.dismiss();
                } else {
                    mFolderWindow.showAsDropDown(mTitleView);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mCameraPath))));
                Image image = new Image();
                image.setPath(mCameraPath);
                mImageAdapter.getSelectedImageList().add(image);
                onSelectDone();
            }
        }
    }

    private void onSelectDone() {
        List<Image> selectedImageList = mImageAdapter.getSelectedImageList();
        setResult(RESULT_OK, new Intent().putExtra(REQUEST_OUTPUT, (Serializable) selectedImageList));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      /*  if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == STORAGE_REQUEST_CODE) {
                initView();
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                startCamera();
            }
        } else {
            Toast.makeText(this, "获取权限失败~", Toast.LENGTH_SHORT).show();
            finish();
        }*/
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File cameraFile = FileUtils.createCameraFile(this);
            mCameraPath = cameraFile.getAbsolutePath();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }
}
