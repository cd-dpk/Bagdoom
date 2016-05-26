package com.example.user.bagdoomandroidapp.activities.template;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.bagdoom.ProductListActivity;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.data.constants.RegistrationConstants;
import com.example.user.bagdoomandroidapp.data.db.DataBaseHelper;
import com.example.user.bagdoomandroidapp.datamodels.Category;
import com.example.user.bagdoomandroidapp.datamodels.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandradasdipok on 4/19/2016.
 */
public abstract class DrawerTemplateActivity extends TemplateActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String LOG ="DrawerTemplateActivity";
    protected TextView personNameText, personPhoneText;
    protected NavigationView navigationView;
    protected List<Category> categoryList= new ArrayList<Category>();
    protected User registeredUser;
    protected DrawerLayout drawer;
    protected ActionBarDrawerToggle toggle;
    private ImageView personImageView;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        ApplicationConstants.CATEGORY_ID = item.getItemId();
        Intent intent = new Intent(DrawerTemplateActivity.this, ProductListActivity.class);
        startActivity(intent);
        return true;
    }
    protected void setupNavigationHeader() {
        View header = navigationView.getHeaderView(0);
        personImageView = (ImageView) header.findViewById(R.id.image_nav_header_person);
        personNameText = (TextView) header.findViewById(R.id.name_person);
        personPhoneText = (TextView) header. findViewById(R.id.phone_number_person);
        personNameText.setText(registeredUser.name);
        personPhoneText.setText(registeredUser.phone);
        personImageView.setImageBitmap(BitmapFactory.decodeFile(ApplicationConstants.EXTERNAL_STORAGE_FOLDER + "/" + registeredUser.photoID));
    }
    protected void setUpNavigationDrawerMenu(){
        Menu menu = navigationView.getMenu();
        menu.clear();
        int i=0;
        for (Category category: categoryList){
            MenuItem menuItem= menu.add(1, category.category_id, i, category.category_name);
            menuItem.setIcon(R.drawable.ic_menu_gallery);
            i++;
        }
    }
    protected void loadDataFromLocalDB(){
        DataBaseHelper localDataBaseHelper = new DataBaseHelper(DrawerTemplateActivity.this);
        categoryList = new Category().toCategories(localDataBaseHelper.selectRows(new Category()));
        SharedPreferences sharedPreferences = getSharedPreferences(RegistrationConstants.APPLICATION_PREFERENCE, MODE_PRIVATE);
        registeredUser = new User();
        registeredUser.phone = sharedPreferences.getString(RegistrationConstants.USER_PHONE, "-1");
        registeredUser = (User) new DataBaseHelper(DrawerTemplateActivity.this).selectRow(registeredUser);
        Log.d(DrawerTemplateActivity.LOG, registeredUser.toString() + registeredUser.phone + " " + registeredUser.name);
    }
}
