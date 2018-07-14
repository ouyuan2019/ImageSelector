package com.ozy.imageselector.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.ozy.imageselector.R;
import com.ozy.imageselector.adapter.ImageFolderAdapter;
import com.ozy.imageselector.bean.Folder;
import com.ozy.imageselector.utils.UiUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by ouyuan on 2018/7/10
 */
public class FolderWindow extends PopupWindow {

    private Context mContext;
    private View mContentView;
    private RecyclerView mRecyclerView;
    private ImageFolderAdapter mAdapter;

    public FolderWindow(Context context) {
        this.mContext = context;
        this.mContentView = LayoutInflater.from(mContext).inflate(R.layout.window_folder, null);
        this.setContentView(mContentView);
        this.setWidth(UiUtils.getScreenWidth(mContext));
        this.setHeight(UiUtils.getScreenHeight(mContext) - UiUtils.dip2px(mContext, 96));
        this.setAnimationStyle(R.style.WindowStyle);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable(Color.argb(153, 0, 0, 0)));
        initView();
        setPopupWindowTouchModal(this, false);
    }

    private void setPopupWindowTouchModal(FolderWindow folderWindow, boolean touchModal) {
        if (null == folderWindow) {
            return;
        }
        Method method;
        try {
            method = PopupWindow.class.getDeclaredMethod("setTouchModal", boolean.class);
            method.setAccessible(true);
            method.invoke(folderWindow, touchModal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new ItemDivider());
        mAdapter = new ImageFolderAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });
    }

    public void setData(List<Folder> folderList) {
        mAdapter.setData(folderList);
    }

    public void setOnItemClickListener(ImageFolderAdapter.OnItemClickListener onItemClickListener) {
        mAdapter.setOnItemClickListener(onItemClickListener);
    }


    public class ItemDivider extends RecyclerView.ItemDecoration {
        private Drawable mDrawable;

        public ItemDivider() {
            mDrawable = mContentView.getResources().getDrawable(R.drawable.item_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent) {
            final int left = UiUtils.dip2px(parent.getContext(), 16);
            final int right = parent.getWidth() - left;

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDrawable.getIntrinsicHeight();
                mDrawable.setBounds(left, top, right, bottom);
                mDrawable.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, int position, RecyclerView parent) {
            outRect.set(0, 0, 0, mDrawable.getIntrinsicWidth());
        }

    }


    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.up_in);
        mRecyclerView.startAnimation(animation);
    }

    boolean isDismiss;

    @Override
    public void dismiss() {
        if (isDismiss) {
            return;
        }
        isDismiss = true;
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.down_out);
        mRecyclerView.startAnimation(animation);
        dismiss();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isDismiss = false;
                FolderWindow.super.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}