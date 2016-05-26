package com.example.user.bagdoomandroidapp.activities.bagdoom;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.template.TemplateActivity;
import com.example.user.bagdoomandroidapp.adapters.RecyclerViewListAdapter;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.datamodels.OnRecyclerViewItemListener;
import com.example.user.bagdoomandroidapp.datamodels.OrderTable;
import com.example.user.bagdoomandroidapp.datamodels.Product;
import com.example.user.bagdoomandroidapp.utils.StringManager;

import java.util.ArrayList;
import java.util.List;


public class OrderReviewPageActivity extends TemplateActivity implements OnRecyclerViewItemListener {

    private Button orderNextButton;
    RecyclerView myOrderReviewRecyclerView;
    List<OrderTable> orderTableRows;
    ScrollView scrollView;

    @Override
    public void initView() {
        setContentView(R.layout.activity_order_review_page);
        templateToolbar = (Toolbar) findViewById(R.id.toolbar);
        orderNextButton = (Button) findViewById(R.id.button_order_view_next);
        myOrderReviewRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_products_order_view);
        orderTableRows = new ArrayList<OrderTable>();
        scrollView = (ScrollView) findViewById(R.id.scrollbar_order);
        subtitleText = (TextView) findViewById(R.id.text_subtitle);
    }

    @Override
    public void loadData() {
        setUpOrders();
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
        myOrderReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myOrderReviewRecyclerView.setAdapter(new RecyclerViewListAdapter(OrderReviewPageActivity.this, R.layout.card_order,orderTableRows.size()));
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0,0);
            }
        });
        subtitleText.setText("Make An Order - Step 1 of 2");
    }

    @Override
    public void listenView() {
        orderNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderReviewPageActivity.this, BillingAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    public  void setUpOrders(){
        // TODO select all orders from local database
        // TODO set the order list into orderTableList
        orderTableRows = new OrderTable().toOrders(localDataBaseHelper.selectRows(new OrderTable()));
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Cancel Confirmation");
        alertDialogBuilder.setMessage("Do you want cancel order?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                localDataBaseHelper.deleteRows(new OrderTable());
                OrderReviewPageActivity.super.onBackPressed();
            }
        });
        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.create().show();
    }


    @Override
    public void listenItem(View view, final int position) {
        // get the view
        ImageView productImage = (ImageView) view.findViewById(R.id.image_card_order_product);
        EditText productText = (EditText) view.findViewById(R.id.text_card_order_product_name);
        EditText orderDesText = (EditText) view.findViewById(R.id.text_card_order_order_des);
        EditText quantityText = (EditText) view.findViewById(R.id.text_card_order_product_quantity);
        EditText unitPriceText = (EditText) view.findViewById(R.id.text_card_order_product_price);
        // load data
        Product sampleProduct = new Product();
        sampleProduct.getWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID, orderTableRows.get(position).productID);
        sampleProduct = (Product) localDataBaseHelper.selectRow(sampleProduct);
        Glide.with(this).load(ApplicationConstants.SERVER_PRODUCT_IMAGE_DIRECTORY_PATH+sampleProduct.decodePhotoUrl().get(0)).into(productImage);
        productText.setText(sampleProduct.product_name);
        productText.setKeyListener(null);
        orderDesText.setText(new StringManager().getAbsoluteString(orderTableRows.get(position).orderDescription));
        orderDesText.setKeyListener(null);
        quantityText.setText(orderTableRows.get(position).quantity + "");
        unitPriceText.setText(orderTableRows.get(position).unit_price + "");
        unitPriceText.setKeyListener(null);
        Button removeButton = (Button) view.findViewById(R.id.button_card_order_remove);
        quantityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.length()!=0){
                    orderTableRows.get(position).quantity = Integer.parseInt(text);
                }
                else{
                    orderTableRows.get(position).quantity = 1;
                }
            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<String, String, String>() {
                    ProgressDialog progressDialog = new ProgressDialog(OrderReviewPageActivity.this);
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog.setMessage("Removing Item");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                    @Override
                    protected String doInBackground(String... params) {
                        long time = System.currentTimeMillis();
                        while(System.currentTimeMillis()<(time+1*1000)){}
                        if (dataBaseOperator.removeOrderItem(orderTableRows.get(position))){
                            orderTableRows.remove(position);
                            return "OK";
                        }else{
                            return "NOTOK";
                        }
                    }
                    @Override
                    protected void onPostExecute(String response) {
                        super.onPostExecute(response);
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        resetOrderRecyclerViewAdapter(response);
                    }
                }.execute();
            }
        });
    }

    private void resetOrderRecyclerViewAdapter(String response) {
        if (response.equals("OK")) {
            myOrderReviewRecyclerView.setAdapter(new RecyclerViewListAdapter(this, R.layout.card_order,orderTableRows.size()));
            if (orderTableRows.size() == 0) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setMessage("Nothing to Order");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(OrderReviewPageActivity.this, BagdoomHomeActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialogBuilder.create().show();
            }
        }
    }
}
