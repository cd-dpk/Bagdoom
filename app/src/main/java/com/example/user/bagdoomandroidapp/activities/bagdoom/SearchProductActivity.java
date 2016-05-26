package com.example.user.bagdoomandroidapp.activities.bagdoom;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.template.TemplateActivity;
import com.example.user.bagdoomandroidapp.adapters.RecyclerViewListAdapter;
import com.example.user.bagdoomandroidapp.datamodels.OnRecyclerViewItemListener;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.datamodels.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchProductActivity extends TemplateActivity implements OnRecyclerViewItemListener{


    ImageButton arrowBackSearchButton;
    EditText searchText;
    List<Product> productList;
    RecyclerView searchedRecyclerView;
    @Override
    public void initView() {
        setContentView(R.layout.activity_search_product);
        arrowBackSearchButton = (ImageButton) findViewById(R.id.image_search_back);
        searchText = (EditText) findViewById(R.id.edit_text_search);
        productList = new ArrayList<Product>();
        searchedRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_products_search);
    }
    @Override
    public void loadData() {
        productList = new Product().toProducts(localDataBaseHelper.selectRows(new Product()));
    }

    @Override
    public void initializeViewByData() {
        searchedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchedRecyclerView.setAdapter(new RecyclerViewListAdapter(this,R.layout.card_product,productList.size()));
    }
    @Override
    public void listenView() {
        arrowBackSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable string) {
                String text = string.toString();
                Product sampleProduct = new Product();
                if (!text.equals("")) {
                    sampleProduct.getNewWhereClause().addLIKEWhereClauseString(Product.Variable.STRING_PRODUCT_NAME, text);
                }
                productList = sampleProduct.toProducts(localDataBaseHelper.selectRows(sampleProduct));
                searchedRecyclerView.setAdapter(new RecyclerViewListAdapter(SearchProductActivity.this,R.layout.card_product,productList.size()));
            }
        });
    }

    @Override
    public void listenItem(View view, final int position) {
        ImageView productImage = (ImageView) view.findViewById(R.id.product_photo_home);
        TextView productName = (TextView) view.findViewById(R.id.product_text_home);
        TextView priceText = (TextView) view.findViewById(R.id.price_text_home);
        Glide.with(this).load(ApplicationConstants.SERVER_PRODUCT_IMAGE_DIRECTORY_PATH+productList.get(position).decodePhotoUrl().get(0)).into(productImage);
        productName.setText(productList.get(position).product_name);
        priceText.setText("Special Price TK." + productList.get(position).special_price );
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationConstants.PRODUCT_ID = productList.get(position).product_id;
                Intent intent = new Intent(SearchProductActivity.this, ProductDetailsActivity.class);
                view.getContext().startActivity(intent);
            }
        });
        final ImageButton wishButton = (ImageButton) view.findViewById(R.id.button_product_favourite);
        final ImageButton cartButton = (ImageButton) view.findViewById(R.id.button_product_cart);

        if (productList.get(position).cart !=0){
            cartButton.setImageResource(R.drawable.cart_2_24);
        }else{
            cartButton.setImageResource(R.drawable.cart_1_24);
        }
        if (productList.get(position).wish !=0){
            wishButton.setImageResource(R.drawable.fav_2_24);
        }else{
            wishButton.setImageResource(R.drawable.fav_1_24);
        }

        wishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add product to wish list
                productList.get(position).wish = productList.get(position).wish%2;
                if (productList.get(position).wish == 0){
                    productList.get(position).wish = 1;
                    if (dataBaseOperator.updateWishOfProduct(productList.get(position))){
                        customToast.showShortToast("Item added to Wish");
                        wishButton.setImageResource(R.drawable.fav_2_24);
                    }
                    else{
                        productList.get(position).wish = 0;
                        customToast.showLongToast("Something wrong");
                    }
                } else if(productList.get(position).wish ==1){
                    productList.get(position).wish = 0;
                    if (dataBaseOperator.updateWishOfProduct(productList.get(position)))
                    {
                        customToast.showShortToast("Item removed from Wish");
                        wishButton.setImageResource(R.drawable.fav_1_24);
                    }
                    else{
                        productList.get(position).wish = 1;
                        customToast.showLongToast("Something wrong");
                    }
                }
            }
        });
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add product to cart_24 list
                productList.get(position).cart = productList.get(position).cart%2;
                if (productList.get(position).cart == 0){
                    productList.get(position).cart = 1;
                    if (dataBaseOperator.updateCartOfProduct(productList.get(position))){
                        customToast.showShortToast("Item added to Cart");
                        cartButton.setImageResource(R.drawable.cart_2_24);
                    }else{
                        productList.get(position).cart = 0;
                        customToast.showLongToast("Something wrong");
                    }
                } else if (productList.get(position).cart == 1){
                    productList.get(position).cart = 0;
                    if (dataBaseOperator.updateCartOfProduct(productList.get(position))){
                        customToast.showShortToast("Item removed from Cart");
                        cartButton.setImageResource(R.drawable.cart_1_24);
                    }
                    else{
                        productList.get(position).cart = 1;
                        customToast.showLongToast("Something wrong");
                    }
                }
            }
        });
    }

}
