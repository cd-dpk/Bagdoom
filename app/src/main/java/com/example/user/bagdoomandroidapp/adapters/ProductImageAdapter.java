package com.example.user.bagdoomandroidapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.user.bagdoomandroidapp.activities.bagdoom.FullScreenImageViewActivity;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandradasdipok on 4/3/2016.
 */
public class ProductImageAdapter extends PagerAdapter {
    Context context;
    List<String> imagePathStringList = new ArrayList<String>();
    public ProductImageAdapter(Context context, List<String> imagePathStringList){
        this.context = context;
        this.imagePathStringList = imagePathStringList;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View viewItem = inflater.inflate(R.layout.image_item, container, false);
        ImageView productImage = (ImageView) viewItem.findViewById(R.id.image_product);
        Glide.with(context).load(ApplicationConstants.SERVER_PRODUCT_IMAGE_DIRECTORY_PATH + imagePathStringList.get(position).toString()).into(productImage);
        ((ViewPager) container).addView(viewItem);
        viewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,FullScreenImageViewActivity.class);
                context.startActivity(intent);
            }
        });
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
