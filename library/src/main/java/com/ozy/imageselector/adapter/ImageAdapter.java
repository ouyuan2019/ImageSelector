package com.ozy.imageselector.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ozy.imageselector.ImageSelectorConfig;
import com.ozy.imageselector.R;
import com.ozy.imageselector.bean.Image;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_PICTURE = 2;
    private Context mContext;
    private ImageSelectorConfig mConfig;
    private OnImageSelectChangedListener listener;
    private List<Image> mSelectedImageList = new ArrayList<>();

    public ImageAdapter(Context context, ImageSelectorConfig config) {
        this.mContext = context;
        this.mConfig = config;
    }

    private List<Image> mImageList = new ArrayList<>();


    public void setOnImageSelectChangedListener(OnImageSelectChangedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_CAMERA) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_camera, parent, false);
            return new CameraHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
            return new ImageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        int viewType = holder.getItemViewType();
        if (viewType == TYPE_CAMERA) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onTakePhoto();
                }
            });
        } else {
            final Image image = mImageList.get(mConfig.needCamera ? position - 1 : position);
            final ImageHolder imageHolder = (ImageHolder) holder;

            if (mConfig.imageLoader != null)
                mConfig.imageLoader.displayImage(mContext, image.path, imageHolder.imageView);
            boolean selected = isSelected(image);

            select(imageHolder, selected);

            imageHolder.ivCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean selected = imageHolder.ivCheck.isSelected();
                    int size = mSelectedImageList.size();

                    if (size >= mConfig.maxNum && !selected) {
                        Toast.makeText(mContext, "你最多可以选择" + size + "张图片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (selected) {
                        for (Image item : mSelectedImageList) {
                            if (item.getPath().equals(image.getPath())) {
                                mSelectedImageList.remove(item);
                                break;
                            }
                        }
                    } else {
                        mSelectedImageList.add(image);
                    }
                    select(imageHolder, !selected);
                    if (listener != null)
                        listener.onChange(mSelectedImageList);
                }
            });
            imageHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onPictureClick(image, position);
                }
            });
        }
    }

    private void select(ImageHolder holder, boolean isSelected) {
        holder.ivCheck.setSelected(isSelected);
        holder.imageView.setColorFilter(isSelected ?
                        mContext.getResources().getColor(R.color.image_overlay2) :
                        mContext.getResources().getColor(R.color.image_overlay),
                PorterDuff.Mode.SRC_ATOP);
    }

    private boolean isSelected(Image image) {
        for (Image item : mSelectedImageList) {
            if (item.getPath().equals(image.path)) {
                return true;
            }
        }
        return false;
    }

    public List<Image> getSelectedImageList() {
        return mSelectedImageList;
    }

    @Override
    public int getItemCount() {
        return mConfig.needCamera ? mImageList.size() + 1 : mImageList.size();
    }


    public void setAdapter(List<Image> list) {
        this.mImageList = list;
        notifyDataSetChanged();
    }

    static class ImageHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ImageView ivCheck;

        public ImageHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageselector_image);
            ivCheck = (ImageView) itemView.findViewById(R.id.imageselector_check);
        }

    }

    static class CameraHolder extends RecyclerView.ViewHolder {
        public CameraHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mConfig.needCamera && position == 0) {
            return TYPE_CAMERA;
        }
        return TYPE_PICTURE;
    }


    public interface OnImageSelectChangedListener {
        void onChange(List<Image> selectImages);

        void onTakePhoto();

        void onPictureClick(Image media, int position);
    }
}
