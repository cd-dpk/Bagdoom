package com.example.user.bagdoomandroidapp.activities.installation;
//accessed from dipok-pc

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.bagdoom.BagdoomHomeActivity;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.data.constants.RegistrationConstants;
import com.example.user.bagdoomandroidapp.utils.CustomToast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhoneVerificationStep1Activity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String LOG ="PhoneVerificationStep1" ;
    Button sendVerifyButton;
    EditText phoneNumberText;
    private TextView mInformationTextView;
    CustomToast customToast;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification_step1);
        sendVerifyButton = (Button) findViewById(R.id.send_verify_button);
        phoneNumberText = (EditText) findViewById(R.id.phone);
        customToast = new CustomToast(PhoneVerificationStep1Activity.this);
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        sharedPreferences = getSharedPreferences(RegistrationConstants.APPLICATION_PREFERENCE, Context.MODE_PRIVATE);
        String registrationStatus = sharedPreferences.getString(RegistrationConstants.REGISTRATION_STATUS,RegistrationConstants.REGISTRATION_NOT_COMPLETED);
        if (registrationStatus.equals(RegistrationConstants.REGISTRATION_COMPLETED)){
            Intent intent = new Intent(this, BagdoomHomeActivity.class);
            startActivity(intent);
        }
        sendVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberText.getText().toString();
                if (phoneNumber.length() == 0) {
                    customToast.showShortToast("Please Enter your code");
                    phoneNumberText.requestFocus();
                } else {
                    if (isValidPhoneNumber(phoneNumber)) {
                        ApplicationConstants.PHONE_NUMBER = phoneNumber;
                        ApplicationConstants.SENT_CODE = getRandomCode();
                        new VerificationCodeSentTask().execute();
                    } else {
                        showPhoneNumberTips();
                        phoneNumberText.requestFocus();
                    }
                }
            }
        });

    }

    /**
     * This checks whether a phone number is valid or not
     * if the phone number contains 11 digit then returns true
     * otherwise false
     *
     * @param phoneNumber
     * @return
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        boolean validNumber = false;
        if (phoneNumber.length() < 11 || phoneNumber.length() > 11) {
            validNumber = false;
        } else {
            validNumber = true;
        }
        return validNumber;
    }

    private void showPhoneNumberTips(){
        customToast.showLongToast("Sorry!Phone Number must contain 11 digits.");
    }

    /**
     * this checks whethere a mobile device supports google play services or not
     *
     * @return
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * It generates random code for phone number verification
     * //TODO it will be modified next
     *
     * @return
     */
    private String getRandomCode() {
        String randomCode = "1111";
        return randomCode;
    }

    private class VerificationCodeSentTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog = new ProgressDialog(PhoneVerificationStep1Activity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Sending Message");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
           String resp = "?";
             try {
                // Prepare JSON containing the GCM message content. What to send and where to send.
                JSONObject jGcmData = new JSONObject();
                JSONObject jData = new JSONObject();
                try {
                    jData.put(ApplicationConstants.PHONE_NUMBER_STRING, ApplicationConstants.PHONE_NUMBER);
                    jData.put(ApplicationConstants.MESSAGE_CODE_STRING, ApplicationConstants.SENT_CODE);
                    jGcmData.put("to", "/topics/global");
                    jGcmData.put("data", jData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Create connection to send GCM Message request.
                URL url = new URL("https://android.googleapis.com/gcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", "key=" + ApplicationConstants.API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Send GCM message content.
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(jGcmData.toString().getBytes());

                // Read GCM response.
                InputStream inputStream = conn.getInputStream();
                resp = IOUtils.toString(inputStream);
                System.out.println(resp);
                System.out.println("Check your device/emulator for notification or logcat for " +
                        "confirmation of the receipt of the GCM message.");
            } catch (IOException e) {
                System.out.println("Unable to send GCM message.");
                System.out.println("Please ensure that API_KEY has been replaced by the server " +
                        "API key, and that the device's registration token is correct (if specified).");
                e.printStackTrace();
            }
            return resp;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(String response) {
            progressDialog.dismiss();
            Intent intent = new Intent(PhoneVerificationStep1Activity.this, PhoneVerificationStep2Activity.class);
            startActivity(intent);
        }
    }
}

