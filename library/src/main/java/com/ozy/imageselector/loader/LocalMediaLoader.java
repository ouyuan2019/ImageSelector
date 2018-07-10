package com.ozy.imageselector.loader;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.ozy.imageselector.bean.Folder;
import com.ozy.imageselector.bean.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ouyuan on 2018/7/10
 */
public class LocalMediaLoader {
    public static final int TYPE_IMAGE = 1;

    private final static String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID};

    private FragmentActivity mActivity;

    private int mType = TYPE_IMAGE;

    public LocalMediaLoader(FragmentActivity activity) {
        this.mActivity = activity;
    }

    public void loadAllMedia(final LocalMediaLoadListener listener) {

        mActivity.getSupportLoaderManager().initLoader(mType, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
                CursorLoader cursorLoader = null;
                switch (id) {
                    case TYPE_IMAGE:
                        cursorLoader = new CursorLoader(
                                mActivity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                IMAGE_PROJECTION, MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                                new String[]{"image/jpeg", "image/png"}, IMAGE_PROJECTION[2] + " DESC");

                        break;
                }
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

                List<Folder> imageFolders = new ArrayList<>();
                Folder allImageFolder = new Folder();
                List<Image> latelyImages = new ArrayList<>();


                if (data == null || data.getCount() == 0) {
                    return;
                }
                while (data.moveToNext()) {
                    String path = data.getString
                            (data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String name = data.getString
                            (data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                    Image image = new Image();
                    image.setName(name);
                    image.setPath(path);

                    //获取当前图片对应的文件夹
                    Folder folder = getImageFolder(path, imageFolders);

                    //把图片添加到该文件夹的image集合中
                    List<Image> imageList = folder.getImageList();
                    imageList.add(image);
                    latelyImages.add(image);
                }

                if (latelyImages.size() > 0) {
                    sortFolder(imageFolders);
                    allImageFolder.setName("所有图片");
                    allImageFolder.setImageList(latelyImages);
                    imageFolders.add(0, allImageFolder);
                }
                listener.loadComplete(imageFolders);
//                data.close();
                loader.cancelLoad();
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {

            }
        });

    }

    private void sortFolder(List<Folder> imageFolders) {
        // 文件夹按图片数量排序
        Collections.sort(imageFolders, new Comparator<Folder>() {
            @Override
            public int compare(Folder lhs, Folder rhs) {
                if (lhs.getImageList() == null || rhs.getImageList() == null) {
                    return 0;
                }
                int lsize = lhs.getImageList().size();
                int rsize = rhs.getImageList().size();
                return lsize == rsize ? 0 : (lsize < rsize ? 1 : -1);
            }
        });
    }

    private Folder getImageFolder(String path, List<Folder> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        for (Folder folder : imageFolders) {
            //已存在
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        Folder folder = new Folder();
        folder.setName(folderFile.getName());
        folder.setPath(folderFile.getAbsolutePath());
        imageFolders.add(folder);
        return folder;
    }


    public interface LocalMediaLoadListener {
        void loadComplete(List<Folder> folders);
    }
}
