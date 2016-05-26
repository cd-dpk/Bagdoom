package com.example.user.bagdoomandroidapp.activities.bagdoom;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class WishListActivity extends TemplateActivity implements OnRecyclerViewItemListener {

    RecyclerView myWishRecyclerView;
    List<Product> wishProducts;
    Button orderAllButton ;
    @Override
    public void initView(){
        setContentView(R.layout.activity_wish_list);
        templateToolbar = (Toolbar) findViewById(R.id.toolbar);
        templateToolbar.setNavigationIcon(R.drawable.arrow_back_white_24x24);
        myWishRecyclerView = (RecyclerView) findViewById(R.id.wish_recycler_view_products);
        subtitleText = (TextView) findViewById(R.id.text_subtitle);
        wishProducts = new ArrayList<Product>();
        orderAllButton = (Button) findViewById(R.id.button_wish_order_all_now);
    }

    @Override
    public void loadData() {
        wishProducts = getWishProducts();
    }

    @Override
    public void initializeViewByData() {
        templateToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        myWishRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myWishRecyclerView.setAdapter(new RecyclerViewListAdapter(WishListActivity.this,R.layout.card_wish, wishProducts.size()));
        if (subtitleText != null){
            subtitleText.setText("WishList");
        }
    }

    @Override
    public void listenView() {
        orderAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataBaseOperator.insertOrderRowsIntoLocalDBFromWishList(wishProducts)) {
                    Intent intent = new Intent(WishListActivity.this, OrderReviewPageActivity.class);
                    startActivity(intent);
                } else {
                    customToast.showLongToast("Something wrong");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private List<Product> getWishProducts(){
        Product sampleProduct = new Product();
        sampleProduct.getWhereClause().addGREATERWhereClauseString(Product.Variable.INT_WISH, 0);
        return sampleProduct.toProducts(localDataBaseHelper.selectRows(sampleProduct));
    }

    @Override
    public void listenItem(View view, final int position) {
        ImageView productImageView = (ImageView) view.findViewById(R.id.card_wish_image_product);
        TextView productNameText = (TextView) view.findViewById(R.id.card_wish_text_product_name);
        TextView productPriceText = (TextView) view.findViewById(R.id.card_wish_text_product_price);
        Glide.with(this).load(ApplicationConstants.SERVER_PRODUCT_IMAGE_DIRECTORY_PATH+wishProducts.get(position).decodePhotoUrl().get(0)).into(productImageView);
        productNameText.setText(wishProducts.get(position).product_name);
        productNameText.setKeyListener(null);
        productPriceText.setText(wishProducts.get(position).special_price + "");
        productPriceText.setKeyListener(null);

        Button removeButton = (Button) view.findViewById(R.id.button_card_wish_remove);
        Button orderNowButton = (Button) view.findViewById(R.id.button_card_wish_order_now);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 5/13/2016 remove item from cart
                new AsyncTask<String, String, String>() {
                    ProgressDialog progressDialog = new ProgressDialog(WishListActivity.this);
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
                        wishProducts.get(position).wish =0;
                        if (dataBaseOperator.updateWishOfProduct(wishProducts.get(position))){
                            wishProducts.remove(position);
                            return "OK";
                        }else{
                            return "";
                        }
                    }
                    @Override
                    protected void onPostExecute(String response) {
                        super.onPostExecute(response);
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        resetWishRecyclerViewAdapter();
                    }
                }.execute();
            }
        });


        orderNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataBaseOperator.insertOrderRowIntoLocalDBFromWishList(wishProducts.get(position))){
                    Intent intent = new Intent(WishListActivity.this, OrderReviewPageActivity.class);
                    startActivity(intent);
                }else{
                    customToast.showLongToast("Something went wrong");
                }
            }
        });
    }
    private void resetWishRecyclerViewAdapter() {
        myWishRecyclerView.setAdapter(new RecyclerViewListAdapter(WishListActivity.this,R.layout.card_wish, wishProducts.size()));
        if (wishProducts.size() == 0){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WishListActivity.this);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setMessage("Wish List is Empty");
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(WishListActivity.this,BagdoomHomeActivity.class);
                    startActivity(intent);
                }
            });
            alertDialogBuilder.create().show();
        }
    }
}