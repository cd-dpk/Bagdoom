package com.example.user.bagdoomandroidapp.activities.bagdoom;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.template.TemplateActivity;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.data.constants.RegistrationConstants;
import com.example.user.bagdoomandroidapp.data.db.DataBaseHelper;
import com.example.user.bagdoomandroidapp.datamodels.User;

public class ProfileActivity extends TemplateActivity {

    TextView personNameText, personPhoneText, personEmailText, personAddressText;
    ImageView profileImageView;
    User registeredUser = new User();
    @Override
    public void initView() {
        setContentView(R.layout.activity_profile);
        templateToolbar = (Toolbar) findViewById(R.id.toolbar);
        personNameText = (TextView) findViewById(R.id.text_profile_name);
        personPhoneText = (TextView) findViewById(R.id.text_profile_phone);
        personEmailText = (TextView) findViewById(R.id.text_profile_email);
        personAddressText = (TextView) findViewById(R.id.text_profile_address);
        templateToolbar.setNavigationIcon(R.drawable.arrow_back_white_24x24);
        profileImageView = (ImageView) findViewById(R.id.image_profile);
    }

    @Override
    public void loadData() {
        loadProfile();
    }

    @Override
    public void initializeViewByData() {
        templateToolbar.setTitle("Profile");
        personNameText.setKeyListener(null);
        personPhoneText.setKeyListener(null);
        personEmailText.setKeyListener(null);
        personAddressText.setKeyListener(null);
        profileImageView.setImageBitmap(BitmapFactory.decodeFile(ApplicationConstants.EXTERNAL_STORAGE_FOLDER + "/" + registeredUser.photoID));
        templateToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void listenView() {

    }


    private void loadProfile(){
        DataBaseHelper localDataBaseHelper = new DataBaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences(RegistrationConstants.APPLICATION_PREFERENCE, MODE_PRIVATE);
        registeredUser = new User();
        registeredUser.phone = sharedPreferences.getString(RegistrationConstants.USER_PHONE, "-1");
        registeredUser = (User) localDataBaseHelper.selectRow(registeredUser);
        Log.d("ProfileActivity", registeredUser.toString() + registeredUser.phone + " " + registeredUser.name);
        personNameText.setText(registeredUser.name);
        personPhoneText.setText(registeredUser.phone);
        personEmailText.setText(registeredUser.email);
        personAddressText.setText(registeredUser.address);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
