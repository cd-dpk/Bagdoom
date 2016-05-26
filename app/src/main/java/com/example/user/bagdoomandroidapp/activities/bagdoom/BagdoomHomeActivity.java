package com.example.user.bagdoomandroidapp.activities.bagdoom;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
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
import com.example.user.bagdoomandroidapp.datamodels.Category;
import com.example.user.bagdoomandroidapp.datamodels.ITable;
import com.example.user.bagdoomandroidapp.datamodels.OnRecyclerViewItemListener;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.data.db.DataBaseHelper;
import com.example.user.bagdoomandroidapp.datamodels.Product;
import com.example.user.bagdoomandroidapp.parser.JSONHandler;
import com.example.user.bagdoomandroidapp.utils.CustomToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BagdoomHomeActivity extends DrawerTemplateActivity implements OnRecyclerViewItemListener {

    private static String LOG="BagdoomHomeActivity";
    SwipeRefreshLayout productListSwipeRefreshLayout ;
    List<Product> productList;
    RecyclerView myRecyclerView;
    TextView noProductTextView;
    TextView subTitleTextView;

    @Override
    public void loadDataFromLocalDB(){
        super.loadDataFromLocalDB();
        Product sampleProduct = new Product();
        productList = new Product().toProducts(localDataBaseHelper.selectRows(sampleProduct));
    }
    @Override
    public void onBackPressed() {

    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_bagdoom_home);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        noProductTextView = (TextView) findViewById(R.id.text_no_product);
        myRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_home);
        productListSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_home);
        subTitleTextView = (TextView) findViewById(R.id.text_subtitle);
    }

    @Override
    public void loadData() {
        loadDataFromLocalDB();
    }

    @Override
    public void initializeViewByData() {
        templateToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(templateToolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawer, templateToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        templateToolbar.setNavigationIcon(R.drawable.drawer_16);
        templateToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupNavigationHeader();
                setUpNavigationDrawerMenu();
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        setupNavigationHeader();
        setUpNavigationDrawerMenu();
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.setAdapter(new RecyclerViewListAdapter(BagdoomHomeActivity.this,R.layout.card_product,productList.size()));
        if (productList.size() != 0) {
            noProductTextView.setVisibility(View.INVISIBLE);
            myRecyclerView.setVisibility(View.VISIBLE);
        } else {
            noProductTextView.setVisibility(View.VISIBLE);
            myRecyclerView.setVisibility(View.INVISIBLE);
        }
        productListSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.button_background);
        if (subTitleTextView != null){
            subTitleTextView.setText("Just Arrived");
        }
    }

    @Override
    public void listenView() {

        productListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productListSwipeRefreshLayout.setRefreshing(false);
                StringRequest dataRequest = new StringRequest(Request.Method.GET,
                        ApplicationConstants.PHP_DATA,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                productListSwipeRefreshLayout.setRefreshing(false);
                                // get data from just code
                                // String response = TempDataProvider.dataResponse();
                                JSONHandler jsonHandler = new JSONHandler();
                                List<ITable> iTables = new ArrayList<ITable>();
                                try {
                                    JSONObject responseJSJsonObject = new JSONObject(response);
                                    Log.d(BagdoomHomeActivity.LOG, response);
                                    // get selectedCategory json
                                    JSONArray categoryJSONArray = responseJSJsonObject.optJSONArray(JSONConstants.OBJECT_CATEGORY);
                                    // get selectedCategory list from selectedCategory json
                                    List<ITable> categoryList = jsonHandler.getRowsFromJSONArray(categoryJSONArray, new Category());
                                    // insert selectedCategory into Local DB
                                    localDataBaseHelper.insertRowsFromServer(categoryList, new Category());
                                    // get local categories from local DB
                                    List<ITable> localCategoryList = localDataBaseHelper.selectRows(new Category());
                                    BagdoomHomeActivity.this.categoryList = new Category().toCategories(localCategoryList);
                                    setUpNavigationDrawerMenu();
                                    // get product json
                                    JSONArray productJSONArray = responseJSJsonObject.optJSONArray(JSONConstants.OBJECT_PRODUCT);
                                    // get product list from product json
                                    List<ITable> productList = jsonHandler.getRowsFromJSONArray(productJSONArray, new Product());
                                    // insert products into Local DB
                                    localDataBaseHelper.insertRowsFromServer(productList, new Product());
                                    // load products from local DB
                                    BagdoomHomeActivity.this.productList = new Product().toProducts(localDataBaseHelper.selectRows(new Product()));
                                    Collections.reverse(productList);
                                    myRecyclerView.setAdapter(new RecyclerViewListAdapter(BagdoomHomeActivity.this,R.layout.card_product,BagdoomHomeActivity.this.productList.size()));
                                    if (productList.size() != 0) {
                                        noProductTextView.setVisibility(View.GONE);
                                        myRecyclerView.setVisibility(View.VISIBLE);
                                    } else {
                                        noProductTextView.setVisibility(View.VISIBLE);
                                        myRecyclerView.setVisibility(View.GONE);
                                    }
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
                Volley.newRequestQueue(BagdoomHomeActivity.this).add(dataRequest);
            }
        });
        myRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topRowVertical = (myRecyclerView == null || myRecyclerView.getChildCount() == 0) ? 0 : myRecyclerView.getChildAt(0).getTop();
                productListSwipeRefreshLayout.setEnabled(topRowVertical >= 0);
            }
        });
    }
    @Override
    public void listenItem(View view, final int position) {
        ImageView productImage = (ImageView) view.findViewById(R.id.product_photo_home);
        TextView productName = (TextView) view.findViewById(R.id.product_text_home);
        TextView priceText = (TextView) view.findViewById(R.id.price_text_home);
        Glide.with(this).load(ApplicationConstants.SERVER_PRODUCT_IMAGE_DIRECTORY_PATH + productList.get(position).decodePhotoUrl().get(0))
                .error(R.drawable.sample_product)
                .into(productImage);
        productName.setText(productList.get(position).product_name);
        priceText.setText("Special Price TK." + productList.get(position).special_price );
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationConstants.PRODUCT_ID = productList.get(position).product_id;
                Intent intent = new Intent(BagdoomHomeActivity.this, ProductDetailsActivity.class);
                view.getContext().startActivity(intent);
            }
        });
        final ImageButton wishButton = (ImageButton) view.findViewById(R.id.button_product_favourite);
        final ImageButton cartButton = (ImageButton) view.findViewById(R.id.button_product_cart);

        if (productList.get(position).cart != 0){
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
                CustomToast customToast = new CustomToast(view.getContext());
                // add product to wish list
                productList.get(position).wish = productList.get(position).wish % 2;
                if (productList.get(position).wish == 0) {
                    productList.get(position).wish = 1;
                    if (dataBaseOperator.updateWishOfProduct(productList.get(position))) {
                        customToast.showShortToast("Item added to Wish");
                        wishButton.setImageResource(R.drawable.fav_2_24);
                    } else {
                        productList.get(position).wish = 0;
                        customToast.showLongToast("Something wrong");
                    }
                } else if (productList.get(position).wish == 1) {
                    productList.get(position).wish = 0;
                    if (dataBaseOperator.updateWishOfProduct(productList.get(position))) {
                        customToast.showShortToast("Item removed from Wish");
                        wishButton.setImageResource(R.drawable.fav_1_24);
                    } else {
                        productList.get(position).wish = 1;
                        customToast.showLongToast("Something wrong");
                    }
                }
            }
        });
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHelper dataBaseHelper = new DataBaseHelper(view.getContext());
                CustomToast customToast = new CustomToast(view.getContext());
                // add product to cart_24 list
                if (productList.get(position).cart == 0) {
                    productList.get(position).cart = 1;
                    if (dataBaseOperator.updateCartOfProduct(productList.get(position))) {
                        Log.d("Cart", productList.get(position).toString());
                        customToast.showShortToast("Item added to Cart");
                        cartButton.setImageResource(R.drawable.cart_2_24);
                    } else {
                        productList.get(position).cart = 0;
                        customToast.showLongToast("Something wrong");
                    }
                } else if (productList.get(position).cart > 0) {
                    productList.get(position).cart = 0;
                    if (dataBaseOperator.updateCartOfProduct(productList.get(position))) {
                        Log.d("Cart", productList.get(position).toString());
                        customToast.showShortToast("Item removed from Cart");
                        cartButton.setImageResource(R.drawable.cart_1_24);
                    } else {
                        productList.get(position).cart = 1;
                        customToast.showLongToast("Something wrong");
                    }
                }
            }
        });
    }
}