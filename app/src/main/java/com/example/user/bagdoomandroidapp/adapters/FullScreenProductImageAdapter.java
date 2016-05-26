package com.example.user.bagdoomandroidapp.adapters;

import android.support.v4.view.PagerAdapter;

/**
 * Created by chandradasdipok on 5/15/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;
public class FullScreenProductImageAdapter extends PagerAdapter {
    Context context;
    List<String> imagePathStringList = new ArrayList<String>();
    public FullScreenProductImageAdapter(Context context, List<String> imagePathStringList){
        this.context = context;
        this.imagePathStringList = imagePathStringList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View viewItem = inflater.inflate(R.layout.full_screen_image, container, false);
        ImageView productImage = (ImageView) viewItem.findViewById(R.id.image_full_screen_product);
       Glide.with(context).load(ApplicationConstants.SERVER_PRODUCT_IMAGE_DIRECTORY_PATH+imagePathStringList.get(position).toString()).into(productImage);
        ((ViewPager) container).addView(viewItem);
        return viewItem;
    }
    @Override
    public int getCount() {
        return imagePathStringList.size();
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
