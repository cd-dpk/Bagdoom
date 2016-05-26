package com.example.user.bagdoomandroidapp.activities.bagdoom;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.template.TemplateActivity;
import com.example.user.bagdoomandroidapp.adapters.FullScreenProductImageAdapter;
import com.example.user.bagdoomandroidapp.adapters.ProductImageAdapter;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.datamodels.Product;

public class FullScreenImageViewActivity extends TemplateActivity {
    Product selectedProduct;
    ViewPager myFullScreenProductImageViewPager;

    @Override
    public void initView() {
        setContentView(R.layout.activity_full_screen_image_view);
        selectedProduct = new Product();
        selectedProduct.product_id = ApplicationConstants.PRODUCT_ID;
        myFullScreenProductImageViewPager = (ViewPager) findViewById(R.id.view_pager_image_full_screen_product);
    }

    @Override
    public void loadData() {
        selectedProduct.getNewWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID,selectedProduct.product_id);
        selectedProduct = (Product) localDataBaseHelper.selectRow(selectedProduct);
    }

    @Override
    public void initializeViewByData() {
        myFullScreenProductImageViewPager.setAdapter(new FullScreenProductImageAdapter(FullScreenImageViewActivity.this, selectedProduct.decodePhotoUrl()));
    }

    @Override
    public void listenView() {}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
