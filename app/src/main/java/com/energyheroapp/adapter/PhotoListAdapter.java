package com.energyheroapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.energyheroapp.helper.NetHelper;
import com.energyheroapp.model.PhotoDataList;

public class PhotoListAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<PhotoDataList> photoDataList;

    public PhotoListAdapter(Context c, ArrayList<PhotoDataList> list) {
        mContext = c;
        photoDataList = list;
    }
    public int getCount() {
        return photoDataList.size();
    }

    public Object getItem(int position){
        return photoDataList.get(position);
    }

    public long getItemId(int position){
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ImageView imageView;
        if(convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));

        }
        else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageDrawable(LoadImageFromURL(photoDataList.get(position).PhotoUrl));

        return imageView;
    }

    private Drawable LoadImageFromURL(String url) {
        try
        {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

}
