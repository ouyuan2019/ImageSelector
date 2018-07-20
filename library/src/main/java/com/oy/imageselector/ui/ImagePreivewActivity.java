package com.oy.imageselector.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.oy.imageselector.ImageSelector;
import com.oy.imageselector.ImageSelectorConfig;
import com.ozy.imageselector.R;

import java.util.ArrayList;
import java.util.List;

public class ImagePreivewActivity extends AppCompatActivity {


    private ImageView mIvDelete;

    private ImageView mIvBack;

    private ViewPager mViewPager;
    private TextView mTvTitle;
    private Toolbar mToolbar;

    private List<String> mSelectedImageList = new ArrayList();
    private int mPosition = 0;
    public static final String OUTPUT_LIST = "outputList";
    public static final String EXTRA_PREVIEW_LIST = "previewList";
    public static final String EXTRA_PREVIEW_SELECT_LIST = "previewSelectList";
    public static final String EXTRA_PREVIEW_SELECT_POSITION = "EXTRA_PREVIEW_SELECT_POSITION";
    private SimpleFragmentAdapter mAdapter;
    private ImageSelectorConfig mConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_preview);
        mConfig = ImageSelector.getInstance().getImageSelectorConfig();
        mAdapter = new SimpleFragmentAdapter();
        mSelectedImageList = getIntent().getStringArrayListExtra(EXTRA_PREVIEW_SELECT_LIST);
        mPosition = getIntent().getIntExtra(EXTRA_PREVIEW_SELECT_POSITION, 0);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mIvDelete = (ImageView) findViewById(R.id.btn_del);
        mIvBack = (ImageView) findViewById(R.id.btn_back);
        mTvTitle = (TextView) findViewById(R.id.txt_title);
        mViewPager = (ViewPager) findViewById(R.id.previewViewPager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mTvTitle.setText(position + 1 + "/" + mSelectedImageList.size());
                mPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });
    }


    public class SimpleFragmentAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mSelectedImageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View contentView = LayoutInflater.from(ImagePreivewActivity.this).inflate(R.layout.fragment_image_preview, container, false);
            final ImageView imageView = (ImageView) contentView.findViewById(R.id.preview_image);
            mConfig.imageLoader.displayImage(ImagePreivewActivity.this, mSelectedImageList.get(position), imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchBarVisibility();
                }
            });
            container.addView(contentView);
            return contentView;
        }
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("要删除这张照片吗？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //移除当前图片刷新界面
                mSelectedImageList.remove(mViewPager.getCurrentItem());

                if (mSelectedImageList.size() > 0) {
                    mAdapter.notifyDataSetChanged();
                    mTvTitle.setText((mPosition + 1) + "/" + mSelectedImageList.size());
                } else {
                    onBackPressed();
                }
            }
        });
        builder.show();
    }

    public void switchBarVisibility() {
        boolean isShowBar = mToolbar.getVisibility() == View.VISIBLE;
        mToolbar.setVisibility(isShowBar ? View.GONE : View.VISIBLE);

        if (isShowBar) {
            hideStatusBar();
        } else {
            showStatusBar();
        }
    }

    private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    @Override
    public void onBackPressed() {
        onDone();
        super.onBackPressed();
    }

    private void onDone() {
        Intent intent = new Intent();
        intent.putExtra(OUTPUT_LIST, (ArrayList) mSelectedImageList);
        setResult(RESULT_OK, intent);
        finish();
    }
}
