package com.example.user.bagdoomandroidapp.activities.installation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.utils.CustomToast;

public class PhoneVerificationStep2Activity extends AppCompatActivity {
    Button verifyButton;
    EditText typedCode;
    CustomToast customToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification_step2);

        verifyButton = (Button) findViewById(R.id.button_verify);
        typedCode = (EditText) findViewById(R.id.text_code);
        typedCode.setText(ApplicationConstants.SENT_CODE);
        customToast = new CustomToast(PhoneVerificationStep2Activity.this);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationConstants.TYPED_CODE = typedCode.getText().toString();
                ProgressDialog progressDialog = new ProgressDialog(PhoneVerificationStep2Activity.this);
                progressDialog.setMessage("Verifying Code");
                progressDialog.show();
                if (isCodeOkay()) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(PhoneVerificationStep2Activity.this, ProfileInfoActivity.class);
                    startActivity(intent);
                } else {
                    progressDialog.dismiss();
                    customToast.showLongToast("Sorry!Code Does not Match");
                }
            }
        });
    }

    /**
     * This method checks the typed_code with the sent_code
     * It returns true if matches
     * otherwise returns false
     *
     * @return
     */
    private boolean isCodeOkay() {
        if (ApplicationConstants.SENT_CODE.equals(ApplicationConstants.TYPED_CODE)) {
            return true;
        } else {
            return false;
        }
    }
}
