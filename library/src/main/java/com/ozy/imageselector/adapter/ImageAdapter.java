package com.ozy.imageselector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ozy.imageselector.ImageSelectorConfig;
import com.ozy.imageselector.R;
import com.ozy.imageselector.bean.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {


    private Context mContext;
    private ImageSelectorConfig mConfig;

    public ImageAdapter(Context context, ImageSelectorConfig config) {
        this.mContext = context;
        this.mConfig = config;
    }

    private List<Image> mImageList = new ArrayList<>();


    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        Image image = mImageList.get(position);
        if (mConfig.imageLoader != null)
            mConfig.imageLoader.displayImage(mContext,image.path,holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }


    public void setAdapter(List<Image> list) {
        this.mImageList = list;
        notifyDataSetChanged();
    }

    static class ImageHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ImageHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image);
        }

    }
}
