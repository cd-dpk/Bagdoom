package com.example.user.bagdoomandroidapp.activities.installation;

// accessed by adil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.bagdoomandroidapp.R;

import com.example.user.bagdoomandroidapp.activities.bagdoom.BagdoomHomeActivity;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.data.constants.JSONConstants;
import com.example.user.bagdoomandroidapp.data.constants.RegistrationConstants;
import com.example.user.bagdoomandroidapp.datamodels.Category;
import com.example.user.bagdoomandroidapp.datamodels.Product;
import com.example.user.bagdoomandroidapp.parser.JSONHandler;
import com.example.user.bagdoomandroidapp.data.db.DataBaseHelper;
import com.example.user.bagdoomandroidapp.datamodels.ITable;
import com.example.user.bagdoomandroidapp.datamodels.User;
import com.example.user.bagdoomandroidapp.utils.CustomToast;
import com.example.user.bagdoomandroidapp.utils.ImageHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileInfoActivity extends AppCompatActivity {

    public final int CAPTURE_PHOTO = 100;
    public final int SELECT_PHOTO = 101;
    ProgressDialog myProgressDialog;

    public final static String LOG = "ProfileInfoActivity";

    ImageView profileImage;
    Button submitButton;
    EditText nameText, emailText, addressText;
    boolean isRegistrationCompleted = false;
    CustomToast customToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ImageHandler().copyImageOnSDCard(BitmapFactory.decodeResource(getResources(), R.drawable.image_demo_profile));
        setContentView(R.layout.activity_profile_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profileImage = (ImageView) findViewById(R.id.profile_image);
        submitButton = (Button) findViewById(R.id.button_submit);
        nameText = (EditText) findViewById(R.id.text_name);
        emailText = (EditText) findViewById(R.id.text_email);
        addressText = (EditText) findViewById(R.id.text_address);
        myProgressDialog = new ProgressDialog(getApplicationContext());
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(ProfileInfoActivity.this);
                progressDialog.setMessage("Registering...");
                progressDialog.show();
                String name, email, address;
                name = nameText.getText().toString();
                email = emailText.getText().toString();
                address = addressText.getText().toString();
                customToast = new CustomToast(ProfileInfoActivity.this);
                if (name.equals("")) {
                    customToast.showShortToast("Please Enter Your Name");
                    nameText.requestFocus();
                } else if (email.equals("")) {
                    customToast.showShortToast("Please Enter Your Email");
                    emailText.requestFocus();
                } else if (address.equals("")) {
                    customToast.showShortToast("Please Enter Your Address");
                    addressText.requestFocus();
                } else {
                    if (!isValidEmail(email)) {
                        customToast.showLongToast("Sorry, Your Email is not valid");
                    } else {
                        //TODO insert phone from user registration
                        //TODO insert photoID from user registration
                        User candidateUser = new User(ApplicationConstants.PHONE_NUMBER, name, email, address, ApplicationConstants.PROFILE_IMAGE_FILE_NAME);
                        final User clonedCandidateUser = (User) candidateUser.toClone();
                        StringRequest insertRequest = new StringRequest(
                                Request.Method.POST,
                                ApplicationConstants.PHP_REGISTER_USER,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d(ProfileInfoActivity.LOG, response);
                                        ApplicationConstants.iTable = new User();
                                        DataBaseHelper localDatabaseHelper = new DataBaseHelper(getApplicationContext());
                                        try {
                                            JSONObject responseJSONObject = new JSONObject(response);
                                            JSONObject communicationJSONObject = responseJSONObject.optJSONObject(JSONConstants.OBJECT_COMMUNICATION);
                                            //TODO do something with communication message
                                            JSONObject databaseJSONObject = responseJSONObject.optJSONObject(JSONConstants.OBJECT_DATABASE);
                                            boolean isInserted = false, isSelected = false;
                                            if (databaseJSONObject.has(JSONConstants.NAME_INSERT) && databaseJSONObject.has(JSONConstants.NAME_SELECT)) {
                                                if (databaseJSONObject.get(JSONConstants.NAME_INSERT).toString().equals(JSONConstants.VALUE_YES) && databaseJSONObject.get(JSONConstants.NAME_SELECT).toString().equals(JSONConstants.VALUE_YES)) {
                                                    ApplicationConstants.USER_ID = (int) databaseJSONObject.get(JSONConstants.NAME_LAST_ID);
                                                    isInserted = true;
                                                    isSelected = true;
                                                }
                                            }
                                            if (isInserted && isSelected) {
                                                JSONArray tableJSONArray = responseJSONObject.optJSONArray(JSONConstants.OBJECT_TABLE);
                                                JSONHandler remoteResponseHandler = new JSONHandler();
                                                if (localDatabaseHelper.insertRowsFromServer(remoteResponseHandler.getRowsFromJSONArray(tableJSONArray,new User()), new User())) {
                                                    List<ITable> iTables = localDatabaseHelper.selectRows(new User());
                                                    for (ITable iTable : iTables) {
                                                        Log.d(ProfileInfoActivity.LOG, iTable.toString());
                                                    }

                                                    User usr = new User();
                                                    usr.phone= ApplicationConstants.PHONE_NUMBER;
                                                    usr = (User) new DataBaseHelper(ProfileInfoActivity.this).selectRow(usr);
                                                    Log.d(ProfileInfoActivity.LOG,usr.toString());

                                                    isRegistrationCompleted = true;
                                                } else {
                                                    Log.d(ProfileInfoActivity.LOG, "Registration Fail");
                                                    isRegistrationCompleted = false;
                                                }
                                            } else {
                                                Log.d(ProfileInfoActivity.LOG, "Communication Error");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        long time = System.currentTimeMillis();
                                        while (System.currentTimeMillis() < (time + 3 * 1000)) {}
                                        progressDialog.dismiss();
                                        if (isRegistrationCompleted) {
                                            //TODO
                                            SharedPreferences sharedPreferences = getSharedPreferences(RegistrationConstants.APPLICATION_PREFERENCE, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            //TODO
                                            editor.putInt(ApplicationConstants.NEXT_ORDER_STRING,0);
                                            editor.putString(RegistrationConstants.REGISTRATION_STATUS, RegistrationConstants.REGISTRATION_COMPLETED);
                                            editor.putString(RegistrationConstants.USER_PHONE, ApplicationConstants.PHONE_NUMBER);
                                            editor.commit();
                                            Intent intent = new Intent(ProfileInfoActivity.this, BagdoomHomeActivity.class);
                                            startActivity(intent);
                                        } else {
                                            customToast.showLongToast("Registration Failed");
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        volleyError.printStackTrace();
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put(User.Variable.STRING_PHONE, clonedCandidateUser.phone);
                                params.put(User.Variable.STRING_NAME, clonedCandidateUser.name);
                                params.put(User.Variable.STRING_EMAIL, clonedCandidateUser.email);
                                params.put(User.Variable.STRING_ADDRESS, clonedCandidateUser.address);
                                params.put(User.Variable.STRING_PHOTOID, clonedCandidateUser.photoID);
                                return params;
                            }
                        };
                        Volley.newRequestQueue(ProfileInfoActivity.this).add(insertRequest);
                    }
                }
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ProfileInfoActivity.this, profileImage);
                Menu menu = popupMenu.getMenu();

                List<String> imageUploadStrings = getImageUploadChoices();
                for (String imageUploadString : imageUploadStrings) {
                    MenuItem menuItem = menu.add(imageUploadString).setIcon(R.drawable.baby);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String itemSelectedString = item.getTitle().toString();
                        if (itemSelectedString.equals(ApplicationConstants.CAPTURE_VIA_CAMERA)) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CAPTURE_PHOTO);
                        } else if (itemSelectedString.equals(ApplicationConstants.UPLOAD_FROM_GALLERY)) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                            pickPhoto.setType("image/*");
                            startActivityForResult(pickPhoto, SELECT_PHOTO);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    /**
     * It checks the given email is valid or invalid
     * @param givenEmailString
     * @return
     */
    private static boolean isValidEmail(CharSequence givenEmailString) {
        boolean isValidEmail = false;
        isValidEmail = Patterns.EMAIL_ADDRESS.matcher(givenEmailString).matches();
        return isValidEmail;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap userImageBitmap=null;
        switch (requestCode) {
            case CAPTURE_PHOTO:
                userImageBitmap = (Bitmap) data.getExtras().get("data");
                userImageBitmap= Bitmap.createScaledBitmap(userImageBitmap, 250, 250, true);
                break;
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        userImageBitmap = BitmapFactory.decodeStream(imageStream);
                        userImageBitmap = Bitmap.createScaledBitmap(userImageBitmap, 250, 250, true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        profileImage.setImageBitmap(userImageBitmap);
        new ImageHandler().copyImageOnSDCard(userImageBitmap);
    }

    private List<String> getImageUploadChoices() {
        List<String> imageUploadChoices = new ArrayList<String>();
        imageUploadChoices.add(ApplicationConstants.UPLOAD_FROM_GALLERY);
        imageUploadChoices.add(ApplicationConstants.CAPTURE_VIA_CAMERA);
        return imageUploadChoices;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    /**
     *
     */
    public void insertDataForNoInternetOperation(){
        List<ITable> productList = new ArrayList<ITable>();
        productList.add(new Product(101, "pic.PNG;ac_96.png", 1, "T-Shirt", "This is good T-Shirt", 250, 200, 2,"2016-04-28 11:01:36"));
        productList.add(new Product(102, "pic.PNG;ac_96.png", 3, "Shoes", "This is good Shoes", 150, 100, 1, "2016-04-28 11:02:20"));
        productList.add(new Product(201, "ac_96.png;pic.PNG", 4, "Shirt", "This is good Shirt", 50, 20, 2, "2016-04-28 11:03:07"));
        productList.add(new Product(301, "pic.PNG;ac_96.png", 1, "Sunglass", "This is good Sunglass", 25, 10, 2, "2016-04-28 11:01:48"));
        productList.add(new Product(401, "ac_96.png", 2, "Bag", "This is good Bag", 500, 400, 2, "2016-04-28 11:02:38"));
        productList.add(new Product(601, "pic.PNG", 2, "Try once more", "A book by MCC LTD.", 500, 400, 2, "2016-04-28 11:02:48"));
        productList.add(new Product(602, "ac_96.png;pic.PNG", 1, "Ahsan", "Robot", 2500, 2000, 3, "2016-04-28 11:03:25"));
        productList.add(new Product(603, "ac_96.png;pic.PNG", 1, "Ahsan", "Robot", 2500, 2000, 3, "2016-04-28 11:03:44"));
        productList.add(new Product(604, "pic.PNG;ac_96.png", 1, "Dipok", "Mcc ltd", 200000, 150000, 2, "2016-04-28 11:02:20"));
        productList.add(new Product(605, "pic.PNG", 4, "Sneakers", "It is a foreign product", 200, 180, 4, "2016-05-02 05:31:04"));

        List<ITable> categoryList = new ArrayList<ITable>();
        categoryList.add( new Category(1, "Men"));
        categoryList.add( new Category (2, "Women"));
        categoryList.add( new Category (3, "Home"));
        categoryList.add( new Category (4, "Abroad"));
        categoryList.add( new Category (5, "Village"));
        categoryList.add( new Category (6, "Others"));
        categoryList.add( new Category (7, "SPL"));

        User user = new User("01743972128","Dipok","dipok@gmail.com","Farmgate","IMAGE_PATH");
        ApplicationConstants.PHONE_NUMBER = user.phone;
        SharedPreferences sharedPreferences = getSharedPreferences(RegistrationConstants.APPLICATION_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(ApplicationConstants.NEXT_ORDER_STRING, 0);
        editor.putString(RegistrationConstants.REGISTRATION_STATUS, RegistrationConstants.REGISTRATION_COMPLETED);
        editor.putString(RegistrationConstants.USER_PHONE, ApplicationConstants.PHONE_NUMBER);
        editor.commit();
        DataBaseHelper localDatabaseHelper = new DataBaseHelper(this);
        localDatabaseHelper.insertRow(user);
        localDatabaseHelper.insertRows(categoryList, new Category());
        localDatabaseHelper.insertRows(productList, new Product());
    }

}