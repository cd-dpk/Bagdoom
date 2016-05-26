
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.template.TemplateActivity;
import com.example.user.bagdoomandroidapp.adapters.RecyclerViewListAdapter;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.datamodels.OnRecyclerViewItemListener;
import com.example.user.bagdoomandroidapp.datamodels.Product;

import java.util.ArrayList;
import java.util.List;

public class CartListActivity extends TemplateActivity  implements OnRecyclerViewItemListener {

    public static final String LOG ="MyCartListActivity";
    RecyclerView myCartRecyclerView;
    List<Product> myCartProducts;
    Button orderAllButton;
    @Override
    public void initView() {
        setContentView(R.layout.activity_cart_list);
        templateToolbar = (Toolbar) findViewById(R.id.toolbar);
        myCartProducts = new ArrayList<>();
        myCartRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_cart_products);
        myCartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAllButton = (Button) findViewById(R.id.button_cart_order_all_now);
        subtitleText = (TextView) findViewById(R.id.text_subtitle);
    }

    @Override
    public void loadData() {
        loadCartProductsFromLocalDB();
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
        myCartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myCartRecyclerView.setAdapter(new RecyclerViewListAdapter(this,R.layout.card_cart,myCartProducts.size()));
        if (subtitleText != null){
            subtitleText.setText("My Cart");
        }
    }

    @Override
    public void listenView() {
        orderAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataBaseOperator.insertOrderRowsIntoLocalDBFromCartList(myCartProducts)) {
                    Intent intent = new Intent(CartListActivity.this, OrderReviewPageActivity.class);
                    startActivity(intent);
                } else {
                    customToast.showLongToast("Something is wrong");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void loadCartProductsFromLocalDB() {
        Product sampleProduct = new Product();
        sampleProduct.getWhereClause().addGREATERWhereClauseString(Product.Variable.INT_CART, 0);
        myCartProducts = sampleProduct.toProducts(localDataBaseHelper.selectRows(sampleProduct));
    }

    @Override
    public void listenItem(View view, final int position) {
        ImageView productImageView= (ImageView) view.findViewById(R.id.card_cart_image_product);
        TextView productNameText = (TextView) view.findViewById(R.id.card_cart_text_product_name);
        TextView productPriceText = (TextView) view.findViewById(R.id.card_cart_text_product_price);
        TextView quantityText = (TextView) view.findViewById(R.id.card_cart_text_product_quantity);
        Glide.with(this).load(ApplicationConstants.SERVER_PRODUCT_IMAGE_DIRECTORY_PATH+myCartProducts.get(position).decodePhotoUrl().get(0)).into(productImageView);
        productNameText.setText(myCartProducts.get(position).product_name);
        productNameText.setKeyListener(null);
        productPriceText.setText(myCartProducts.get(position).special_price + "");
        productPriceText.setKeyListener(null);
        quantityText.setText(myCartProducts.get(position).cart+"");

        quantityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.equals("")){
                    myCartProducts.get(position).cart = 0;
                }
                else {
                    myCartProducts.get(position).cart = Integer.parseInt((String) text);
                }
            }
        });
        Button removeButton = (Button) view.findViewById(R.id.button_card_cart_remove);
        Button orderNow = (Button) view.findViewById(R.id.button_card_cart_order_now);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 5/13/2016 remove item from cart
                new AsyncTask<String, String, String>() {
                    ProgressDialog progressDialog = new ProgressDialog(CartListActivity.this);
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
                        while(System.currentTimeMillis()<(time+2*1000)){}
                        myCartProducts.get(position).cart =0;
                        if (dataBaseOperator.updateCartOfProduct(myCartProducts.get(position))){
                            myCartProducts.remove(position);
                            return "OK";
                        }else {
                            return "NOTOK";
                        }
                    }
                    @Override
                    protected void onPostExecute(String response) {
                        super.onPostExecute(response);
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        resetCartRecyclerViewAdapter(response);
                    }
                }.execute();
            }
        });
        orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataBaseOperator.insertOrderRowIntoLocalDBFromCartList(myCartProducts.get(position))){
                    Intent intent = new Intent(CartListActivity.this, OrderReviewPageActivity.class);
                    startActivity(intent);
                }else{
                    customToast.showLongToast("Something went wrong");
                }

            }
        });
    }

    private void resetCartRecyclerViewAdapter(String response) {
        if (response.equals("OK")) {
            myCartRecyclerView.setAdapter(new RecyclerViewListAdapter(this,R.layout.card_cart,myCartProducts.size()));
            if (myCartProducts.size() == 0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CartListActivity.this);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setMessage("Cart is Empty");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CartListActivity.this, BagdoomHomeActivity.class);
                        startActivity(intent);
                    }
                });
                alertDialogBuilder.create().show();
            }
        }
    }
}