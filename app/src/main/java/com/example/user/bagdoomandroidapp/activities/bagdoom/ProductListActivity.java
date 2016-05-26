package com.example.user.bagdoomandroidapp.activities.bagdoom;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.template.DrawerTemplateActivity;
import com.example.user.bagdoomandroidapp.adapters.RecyclerViewListAdapter;
import com.example.user.bagdoomandroidapp.data.constants.JSONConstants;
import com.example.user.bagdoomandroidapp.datamodels.ITable;
import com.example.user.bagdoomandroidapp.datamodels.OnRecyclerViewItemListener;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.datamodels.Category;
import com.example.user.bagdoomandroidapp.datamodels.Product;
import com.example.user.bagdoomandroidapp.parser.JSONHandler;
import com.example.user.bagdoomandroidapp.utils.CustomToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends DrawerTemplateActivity implements OnRecyclerViewItemListener {

    private static String LOG="ProductListActivity";
    SwipeRefreshLayout swipeRefreshLayout;
    List<Product> localProducts;
    RecyclerView myRecyclerView;
    Category selectedCategory;
    TextView noProductTextView;

    @Override
    public void initView() {
        setContentView(R.layout.activity_bagdoom_home);
        templateToolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        noProductTextView = (TextView) findViewById(R.id.text_no_product);
        subtitleText = (TextView) findViewById(R.id.text_subtitle);
        myRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_home);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_home);
        localProducts  = new ArrayList<Product>();
    }

    @Override
    public void loadData() {
        loadDataFromLocalDB();
    }

    @Override
    public void initializeViewByData() {
        setSupportActionBar(templateToolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawer, templateToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        templateToolbar.setNavigationIcon(R.drawable.ic_drawer);
        templateToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        templateToolbar.findViewById(R.id.toolbar_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductListActivity.this, BagdoomHomeActivity.class);
                startActivity(intent);
            }
        });
        setupNavigationHeader();
        setUpNavigationDrawerMenu();
        navigationView.setNavigationItemSelectedListener(this);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.setAdapter(new RecyclerViewListAdapter(ProductListActivity.this,R.layout.card_product,localProducts.size() ));
        setToggleBetweenNoProductsAndRecycler();
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.button_background);
    }

    @Override
    public void listenView() {
        // TODO: 5/13/2016 enable internet operation 
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                StringRequest dataRequest = new StringRequest(Request.Method.GET,
                        ApplicationConstants.PHP_DATA,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // get data from just code
                                JSONHandler jsonHandler = new JSONHandler();
                                List<ITable> iTables = new ArrayList<ITable>();
                                try {
                                    JSONObject responseJSJsonObject = new JSONObject(response);
                                    Log.d(ProductListActivity.LOG, response);
                                    // get selectedCategory json
                                    JSONArray categoryJSONArray = responseJSJsonObject.optJSONArray(JSONConstants.OBJECT_CATEGORY);
                                    // get selectedCategory list from selectedCategory json
                                    List <ITable> categoryList = jsonHandler.getRowsFromJSONArray(categoryJSONArray, new Category());
                                    // insert selectedCategory into Local DB
                                    localDataBaseHelper.insertRowsFromServer(categoryList, new Category());
                                    // get product json
                                    JSONArray productJSONArray = responseJSJsonObject.optJSONArray(JSONConstants.OBJECT_PRODUCT);
                                    // get product list from product json
                                    List <ITable> productList = jsonHandler.getRowsFromJSONArray(productJSONArray, new Product());
                                    // insert products into Local DB
                                    localDataBaseHelper.insertRowsFromServer(productList, new Product());
                                    // load products from local DB
                                    loadDataFromLocalDB();
                                    myRecyclerView.setAdapter(new RecyclerViewListAdapter(ProductListActivity.this,R.layout.card_product,localProducts.size() ));
                                    setToggleBetweenNoProductsAndRecycler();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                            }
                        }
                );
                Volley.newRequestQueue(ProductListActivity.this).add(dataRequest);
            }
        });
    }

    @Override
    public void loadDataFromLocalDB(){
        super.loadDataFromLocalDB();
        selectedCategory = new Category();
        selectedCategory.category_id = ApplicationConstants.CATEGORY_ID;
        selectedCategory = (Category) localDataBaseHelper.selectRow(selectedCategory);
        subtitleText.setText(selectedCategory.category_name);
        Product product = new Product();
        product.category_id = ApplicationConstants.CATEGORY_ID;
        product.getWhereClause().addYESWhereClauseString(Product.Variable.INT_CATEGORY_ID, product.category_id);
        localProducts = new Product().toProducts(localDataBaseHelper.selectRows(product));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        ApplicationConstants.CATEGORY_ID = item.getItemId();
        loadDataFromLocalDB();
        myRecyclerView.setAdapter(new RecyclerViewListAdapter(ProductListActivity.this,R.layout.card_product,localProducts.size() ));
        setToggleBetweenNoProductsAndRecycler();
        drawer.closeDrawer(Gravity.LEFT);
        return true;
    }

    private void setToggleBetweenNoProductsAndRecycler(){
        if (localProducts.size() != 0 ){
            noProductTextView.setVisibility(View.GONE);
            myRecyclerView.setVisibility(View.VISIBLE);
        }else{
            noProductTextView.setVisibility(View.VISIBLE);
            myRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void listenItem(View view, final int position) {
        ImageView productImage = (ImageView) view.findViewById(R.id.product_photo_home);
        TextView productName = (TextView) view.findViewById(R.id.product_text_home);
        TextView priceText = (TextView) view.findViewById(R.id.price_text_home);
        Glide.with(this).load(ApplicationConstants.SERVER_PRODUCT_IMAGE_DIRECTORY_PATH+localProducts.get(position).decodePhotoUrl().get(0)).into(productImage);
        productName.setText(localProducts.get(position).product_name);
        priceText.setText("Special Price TK." + localProducts.get(position).special_price );
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationConstants.PRODUCT_ID = localProducts.get(position).product_id;
                Intent intent = new Intent(ProductListActivity.this, ProductDetailsActivity.class);
                startActivity(intent);
            }
        });
        final ImageButton wishButton = (ImageButton) view.findViewById(R.id.button_product_favourite);
        final ImageButton cartButton = (ImageButton) view.findViewById(R.id.button_product_cart);

        if (localProducts.get(position).cart !=0){
            cartButton.setImageResource(R.drawable.cart_2_24);
        }else{
            cartButton.setImageResource(R.drawable.cart_1_24);
        }
        if (localProducts.get(position).wish !=0){
            wishButton.setImageResource(R.drawable.fav_2_24);
        }else{
            wishButton.setImageResource(R.drawable.fav_1_24);
        }

        wishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomToast customToast =new CustomToast(view.getContext());
                // add product to wish list
                localProducts.get(position).wish = localProducts.get(position).wish%2;
                if (localProducts.get(position).wish == 0){
                    localProducts.get(position).wish = 1;
                    ContentValues updateContentValues = new ContentValues();
                    updateContentValues.put(Product.Variable.INT_WISH, localProducts.get(position).wish);
                    localProducts.get(position).setUpdateContentValues(updateContentValues);
                    localProducts.get(position).getWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID, localProducts.get(position).product_id);
                    if (localDataBaseHelper.updateRow(localProducts.get(position)))
                    {
                        customToast.showShortToast("Item added to Wish");
                        wishButton.setImageResource(R.drawable.fav_2_24);
                    }
                    else{
                        localProducts.get(position).wish = 0;
                        customToast.showLongToast("Something wrong");
                    }
                } else if(localProducts.get(position).wish ==1){
                    localProducts.get(position).wish = 0;
                    ContentValues updateContentValues = new ContentValues();
                    updateContentValues.put(Product.Variable.INT_WISH, localProducts.get(position).wish);
                    localProducts.get(position).setUpdateContentValues(updateContentValues);
                    localProducts.get(position).getWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID, localProducts.get(position).product_id);
                    if (localDataBaseHelper.updateRow(localProducts.get(position)))
                    {
                        customToast.showShortToast("Item removed from Wish");
                        wishButton.setImageResource(R.drawable.fav_1_24);
                    }
                    else{
                        localProducts.get(position).wish = 1;
                        customToast.showLongToast("Something wrong");
                    }
                }
            }
        });
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add product to cart_24 list
                localProducts.get(position).cart = localProducts.get(position).cart%2;
                if (localProducts.get(position).cart == 0){
                    localProducts.get(position).cart = 1;
                    ContentValues updateContentValues = new ContentValues();
                    updateContentValues.put(Product.Variable.INT_CART, localProducts.get(position).cart);
                    localProducts.get(position).setUpdateContentValues(updateContentValues);
                    localProducts.get(position).getWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID, localProducts.get(position).product_id);
                    if (localDataBaseHelper.updateRow(localProducts.get(position))){
                        customToast.showShortToast("Item added to Cart");
                        cartButton.setImageResource(R.drawable.cart_2_24);
                    }else{
                        localProducts.get(position).cart = 0;
                        customToast.showLongToast("Something wrong");
                    }
                } else if (localProducts.get(position).cart == 1){
                    localProducts.get(position).cart = 0;
                    ContentValues updateContentValues = new ContentValues();
                    updateContentValues.put(Product.Variable.INT_CART, localProducts.get(position).cart);
                    localProducts.get(position).setUpdateContentValues(updateContentValues);
                    localProducts.get(position).getWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID, localProducts.get(position).product_id);
                    if (localDataBaseHelper.updateRow(localProducts.get(position))){
                        customToast.showShortToast("Item removed from Cart");
                        cartButton.setImageResource(R.drawable.cart_1_24);
                    }
                    else{
                        localProducts.get(position).cart = 1;
                        customToast.showLongToast("Something wrong");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}