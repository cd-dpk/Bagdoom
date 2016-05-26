package com.example.user.bagdoomandroidapp.activities.bagdoom;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.template.TemplateActivity;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.data.constants.RegistrationConstants;
import com.example.user.bagdoomandroidapp.data.db.DataBaseHelper;
import com.example.user.bagdoomandroidapp.datamodels.OrderTable;
import com.example.user.bagdoomandroidapp.datamodels.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BillingAddressActivity extends TemplateActivity {

    private static final String LOG="BillingAddressActivity";
    private Button makeOrderButton;
    EditText nameText, emailText, addressText, phoneText;
    User invoiceUser;

    @Override
    public void initView() {
        setContentView(R.layout.activity_billing_address);
        templateToolbar = (Toolbar) findViewById(R.id.toolbar);
        invoiceUser = new User();
        nameText = (EditText) findViewById(R.id.text_billing_name);
        emailText = (EditText) findViewById(R.id.text_billing_email);
        addressText = (EditText) findViewById(R.id.text_billing_address);
        phoneText = (EditText) findViewById(R.id.text_billing_phone);
        makeOrderButton = (Button) findViewById(R.id.button_billing_order);
        subtitleText = (TextView) findViewById(R.id.text_subtitle);
    }

    @Override
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(RegistrationConstants.APPLICATION_PREFERENCE, MODE_PRIVATE);
        invoiceUser.phone = sharedPreferences.getString(RegistrationConstants.USER_PHONE, "-1");
        invoiceUser = (User) localDataBaseHelper.selectRow(invoiceUser);
    }

    @Override
    public void initializeViewByData() {
        templateToolbar.setNavigationIcon(R.drawable.arrow_back_white_24x24);
        templateToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fillUpUserDetails();
        subtitleText.setText("Make an -Order - Step 2 of 2");
    }

    @Override
    public void listenView() {

        // TODO: 5/13/2016 enable internet operation
        makeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(BillingAddressActivity.this);
                progressDialog.setMessage("Loading");
                progressDialog.setCancelable(false);
                progressDialog.show();
                invoiceUser.name = nameText.getText().toString();
                invoiceUser.email = emailText.getText().toString();
                invoiceUser.address = addressText.getText().toString();
                invoiceUser.phone = phoneText.getText().toString();
                DataBaseHelper localDataBaseHelper = new DataBaseHelper(BillingAddressActivity.this);
                // TODO select rows from order table
                List<OrderTable> orderTableRows = new OrderTable().toOrders(localDataBaseHelper.selectRows(new OrderTable()));
                // TODO delete rows from order table
                localDataBaseHelper.deleteRows(new OrderTable());
                // TODO json string of invoice
                String invoiceJson = "\"invoice\":{" +
                        "\"name\":\"" + invoiceUser.name + "\"," +
                        "\"email\":\"" + invoiceUser.email + "\"," +
                        "\"phone\":\"" + invoiceUser.phone + "\"," +
                        "\"address\":\"" + invoiceUser.address + "\"," +
                        "\"number\":" + orderTableRows.size() + "" +
                        "}";
                String orderJson = "\"orders\":[";
                for (OrderTable orderTableRow : orderTableRows) {
                    if (!orderJson.equals("\"orders\":[")) {
                        orderJson += ",";
                    }
                    orderJson += orderTableRow.toJsonString();
                }
                orderJson += "]";
                final String paramInvoiceJSON = "{" + invoiceJson + "," + orderJson + "}";
                Log.d(BillingAddressActivity.LOG, paramInvoiceJSON);
                // TODO now pass the param and  call make_a_order.php file
                StringRequest makeAOrderRequest = new StringRequest(Request.Method.POST,
                        ApplicationConstants.PHP_MAKE_A_ORDER,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                Log.d(BillingAddressActivity.LOG, response);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BillingAddressActivity.this);
                                alertDialogBuilder.setMessage(response);
                                alertDialogBuilder.setTitle("Order Confirmation");
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Intent intent = new Intent(BillingAddressActivity.this, BagdoomHomeActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApplicationConstants.INVOICE_INFO, paramInvoiceJSON);
                        return params;
                    }
                };
                Volley.newRequestQueue(BillingAddressActivity.this).add(makeAOrderRequest);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void fillUpUserDetails(){
        nameText.setText(invoiceUser.name);
        emailText.setText(invoiceUser.email);
        addressText.setText(invoiceUser.address);
        phoneText.setText(invoiceUser.phone);
    }
}
