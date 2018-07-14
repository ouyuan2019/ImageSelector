package com.ozy.imageselector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ozy.imageselector.ImageSelector;
import com.ozy.imageselector.R;
import com.ozy.imageselector.bean.Folder;
import com.ozy.imageselector.bean.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 欧源 on 2017/4/23.
 */

public class ImageFolderAdapter extends RecyclerView.Adapter<ImageFolderAdapter.FolderViewHolder> {

    private Context mContext;
    private List<Folder> mFolderList = new ArrayList<>();
    private int mCheckedIndex = 0;

    private OnItemClickListener mOnItemClickListener;


    public ImageFolderAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<Folder> localMediaFolderList) {
        this.mFolderList = localMediaFolderList;
        notifyDataSetChanged();
    }


    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, final int position) {

        final Folder folder = mFolderList.get(position);

        String path = folder.getImageList().get(0).getPath();

        ImageSelector.getInstance().getImageSelectorConfig().imageLoader
                .displayImage(mContext, path, holder.mFirstImage);

        holder.mFolderName.setText(folder.getName());
        holder.mImageNum.setText(folder.getImageList().size() + "");
        holder.isSelected.setVisibility(mCheckedIndex == position ? View.VISIBLE : View.GONE);
        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mCheckedIndex = position;
                    notifyDataSetChanged();
                    mOnItemClickListener.onItemClick(folder.getName(), folder.getImageList());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFolderList.size();
    }


    static class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView mFirstImage;
        TextView mFolderName;
        TextView mImageNum;
        ImageView isSelected;

        View contentView;

        public FolderViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            mFirstImage = (ImageView) itemView.findViewById(R.id.first_image);
            mFolderName = (TextView) itemView.findViewById(R.id.folder_name);
            mImageNum = (TextView) itemView.findViewById(R.id.image_num);
            isSelected = (ImageView) itemView.findViewById(R.id.is_selected);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String folderName, List<Image> images);
    }
}
