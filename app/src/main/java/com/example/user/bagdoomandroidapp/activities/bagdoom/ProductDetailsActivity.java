package com.example.user.bagdoomandroidapp.activities.bagdoom;

import android.content.ContentValues;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.template.DrawerTemplateActivity;
import com.example.user.bagdoomandroidapp.adapters.DescriptionReviewAdapter;
import com.example.user.bagdoomandroidapp.adapters.RecyclerViewListAdapter;
import com.example.user.bagdoomandroidapp.datamodels.OnRecyclerViewItemListener;
import com.example.user.bagdoomandroidapp.adapters.ProductImageAdapter;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.example.user.bagdoomandroidapp.data.db.DataBaseHelper;
import com.example.user.bagdoomandroidapp.datamodels.ITable;
import com.example.user.bagdoomandroidapp.datamodels.Product;
import com.example.user.bagdoomandroidapp.fragments.DescriptionFragment;
import com.example.user.bagdoomandroidapp.fragments.ReviewFragment;
import com.example.user.bagdoomandroidapp.utils.CustomToast;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends DrawerTemplateActivity implements OnRecyclerViewItemListener {

    public static final String LOG ="ProductDetailsActivity";
    TextView productNameText, priceText ;
    ImageView productImage;
    Product selectedProduct;
    ViewPager myProductImageViewPager, myDescriptionReviewPager;
    TabLayout myTabLayout;
    RecyclerView relatedProductsRecycler;
    List<Product> relatedProducts;
    Button addCartButton, removeCartButton, addWishButton, removeWishButton, orderButton;
    ScrollView myScrollView;

    @Override
    public void initView() {
        setContentView(R.layout.activity_product_details);
        templateToolbar = (Toolbar) findViewById(R.id.toolbar);
        myScrollView = (ScrollView) findViewById(R.id.scrollView_product_details);
        selectedProduct = new Product();
        myTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        addCartButton = (Button) findViewById(R.id.button_add_cart);
        removeCartButton = (Button) findViewById(R.id.button_remove_cart);
        myDescriptionReviewPager = (ViewPager) findViewById(R.id.description_review_pager);
        addWishButton = (Button) findViewById(R.id.button_add_favourite);
        removeWishButton = (Button) findViewById(R.id.button_remove_favourite);
        orderButton = (Button) findViewById(R.id.button_order_now);
        relatedProductsRecycler = (RecyclerView) findViewById(R.id.related_product_recyler_view);
        productNameText = (TextView) findViewById(R.id.product_name_details);
        priceText = (TextView) findViewById(R.id.price_text_details);
        productImage = (ImageView) findViewById(R.id.product_image_details);
        myProductImageViewPager = (ViewPager) findViewById(R.id.product_image_pager);
    }

    @Override
    public void loadData() {
        loadProductDetailsFromLocalDB();
        loadRelatedProductFromLocalDB();
    }

    @Override
    public void initializeViewByData() {
        setSupportActionBar(templateToolbar);
        templateToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        myScrollView.post(new Runnable() {
            @Override
            public void run() {
                myScrollView.scrollTo(0, 0);
            }
        });;
        templateToolbar.setNavigationIcon(R.drawable.arrow_back_white_24x24);
        setupDescriptionReviewViewPager();
        setUpProductDetails();
        myProductImageViewPager.setAdapter(new ProductImageAdapter(ProductDetailsActivity.this, selectedProduct.decodePhotoUrl()));
        relatedProductsRecycler.setLayoutManager(new LinearLayoutManager(this));
        relatedProductsRecycler.setHasFixedSize(true);
    }

    @Override
    public void listenView() {
        addCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO insert product to cart_24
                selectedProduct.cart = 1;
                if (dataBaseOperator.updateCartOfProduct(selectedProduct)) {
                    customToast.showShortToast("Item Added to Cart");
                    addCartButton.setVisibility(View.GONE);
                    removeCartButton.setVisibility(View.VISIBLE);
                } else {
                    customToast.showShortToast("Something Wrong");
                    selectedProduct.cart = 0;
                }
            }
        });
        addWishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO insert product to wish
                selectedProduct.wish = 1;
                if (dataBaseOperator.updateWishOfProduct(selectedProduct)) {
                    customToast.showShortToast("Item Added to Wish");
                    addWishButton.setVisibility(View.GONE);
                    removeWishButton.setVisibility(View.VISIBLE);
                } else {
                    selectedProduct.wish = 0;
                    customToast.showShortToast("Something Wrong");
                }
            }
        });

        removeCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO remove product from cart_24
                selectedProduct.cart = 0;
                if (dataBaseOperator.updateCartOfProduct(selectedProduct)) {
                    customToast.showShortToast("Item Removed From Cart");
                    addCartButton.setVisibility(View.VISIBLE);
                    removeCartButton.setVisibility(View.GONE);
                } else {
                    selectedProduct.cart = 1;
                    customToast.showShortToast("Something Wrong");
                }
            }
        });

        removeWishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO remove product from wish
                selectedProduct.wish = 0;
                if (dataBaseOperator.updateWishOfProduct(selectedProduct)) {
                    customToast.showShortToast("Item Removed From Wish");
                    removeWishButton.setVisibility(View.GONE);
                    addWishButton.setVisibility(View.VISIBLE);
                } else {
                    selectedProduct.wish = 1;
                    customToast.showShortToast("Something Wrong");
                }
            }
        });
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO insert the product as order into local db
                if (dataBaseOperator.insertOrderRow(selectedProduct)) {
                    Intent intent = new Intent(ProductDetailsActivity.this, OrderReviewPageActivity.class);
                    startActivity(intent);
                } else {
                    customToast.showShortToast("Something is wrong");
                }
            }
        });
    }

    private void setupDescriptionReviewViewPager() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new DescriptionFragment());
        fragmentList.add(new ReviewFragment());
        for (Fragment fragment:fragmentList){
            myTabLayout.addTab(myTabLayout.newTab().setText(fragment.toString()));
        }
        myTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        myDescriptionReviewPager.setAdapter(new DescriptionReviewAdapter(getSupportFragmentManager(), myTabLayout.getTabCount()));
        myDescriptionReviewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(myTabLayout));
        myTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                myDescriptionReviewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    /**
     *  load related products and
     *  set the product list to related products recycler view
     */
    private void loadRelatedProductFromLocalDB() {
        selectedProduct.getWhereClause().addYESWhereClauseString(Product.Variable.INT_CATEGORY_ID, selectedProduct.category_id);
        selectedProduct.getWhereClause().addNOTWhereClauseString(Product.Variable.INT_PRODUCT_ID, selectedProduct.product_id);
        List<ITable> iTableList = localDataBaseHelper.selectRows(selectedProduct);
        relatedProducts = selectedProduct.toProducts(iTableList);
        relatedProductsRecycler.setAdapter(new RecyclerViewListAdapter(ProductDetailsActivity.this,R.layout.card_product ,relatedProducts.size() ));
    }

    /**
     *  load product details from local db
     */
    private void loadProductDetailsFromLocalDB() {
        selectedProduct.product_id = ApplicationConstants.PRODUCT_ID;
        selectedProduct.getWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID, selectedProduct.product_id);
        selectedProduct = (Product) localDataBaseHelper.selectRow(selectedProduct);
        Log.d(ProductDetailsActivity.LOG, selectedProduct.toString());

    }
    /**
     *  setup product details
     */
    private void setUpProductDetails(){
        Log.d("Product Details",selectedProduct.wish+" "+ selectedProduct.cart);
        productNameText .setText(selectedProduct.product_name);
        priceText.setText("Price TK." + selectedProduct.price + " Special Price TK." + selectedProduct.special_price);
        Glide.with(this).load(ApplicationConstants.SERVER_PRODUCT_IMAGE_DIRECTORY_PATH+selectedProduct.product_photo_url).into(productImage);
        if (selectedProduct.wish==0){
            addWishButton.setVisibility(View.VISIBLE);
            removeWishButton.setVisibility(View.GONE);
        }else {
            addWishButton.setVisibility(View.GONE);
            removeWishButton.setVisibility(View.VISIBLE);
        }
        if (selectedProduct.cart==0){
            addCartButton.setVisibility(View.VISIBLE);
            removeCartButton.setVisibility(View.GONE);
        }else{
            addCartButton.setVisibility(View.GONE);
            removeCartButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void listenItem(View view, final int position) {
        ImageView productImage = (ImageView) view.findViewById(R.id.product_photo_home);
        TextView productName = (TextView) view.findViewById(R.id.product_text_home);
        TextView priceText = (TextView) view.findViewById(R.id.price_text_home);
        Glide.with(this).load(ApplicationConstants.SERVER_PRODUCT_IMAGE_DIRECTORY_PATH+relatedProducts.get(position).decodePhotoUrl().get(0)).into(productImage);
        productName.setText(relatedProducts.get(position).product_name);
        priceText.setText("Special Price TK." + relatedProducts.get(position).special_price );
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplicationConstants.PRODUCT_ID = relatedProducts.get(position).product_id;
                Intent intent = new Intent(ProductDetailsActivity.this, ProductDetailsActivity.class);
                view.getContext().startActivity(intent);
            }
        });
        final ImageButton wishButton = (ImageButton) view.findViewById(R.id.button_product_favourite);
        final ImageButton cartButton = (ImageButton) view.findViewById(R.id.button_product_cart);

        if (relatedProducts.get(position).cart !=0){
            cartButton.setImageResource(R.drawable.cart_2_24);
        }else{
            cartButton.setImageResource(R.drawable.cart_1_24);
        }
        if (relatedProducts.get(position).wish !=0){
            wishButton.setImageResource(R.drawable.fav_2_24);
        }else{
            wishButton.setImageResource(R.drawable.fav_1_24);
        }

        wishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add product to wish list
                relatedProducts.get(position).wish = relatedProducts.get(position).wish%2;
                if (relatedProducts.get(position).wish == 0){
                    relatedProducts.get(position).wish = 1;
                    ContentValues updateContentValues = new ContentValues();
                    updateContentValues.put(Product.Variable.INT_WISH, relatedProducts.get(position).wish);
                    relatedProducts.get(position).setUpdateContentValues(updateContentValues);
                    relatedProducts.get(position).getWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID, relatedProducts.get(position).product_id);
                    if (localDataBaseHelper.updateRow(relatedProducts.get(position)))
                    {
                        customToast.showShortToast("Item added to Wish");
                        wishButton.setImageResource(R.drawable.fav_2_24);
                    }
                    else{
                        relatedProducts.get(position).wish = 0;
                        customToast.showLongToast("Something wrong");
                    }
                } else if(relatedProducts.get(position).wish ==1){
                    relatedProducts.get(position).wish = 0;
                    ContentValues updateContentValues = new ContentValues();
                    updateContentValues.put(Product.Variable.INT_WISH, relatedProducts.get(position).wish);
                    relatedProducts.get(position).setUpdateContentValues(updateContentValues);
                    relatedProducts.get(position).getWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID, relatedProducts.get(position).product_id);
                    if (localDataBaseHelper.updateRow(relatedProducts.get(position)))
                    {
                        customToast.showShortToast("Item removed from Wish");
                        wishButton.setImageResource(R.drawable.fav_1_24);
                    }
                    else{
                        relatedProducts.get(position).wish = 1;
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
                relatedProducts.get(position).cart = relatedProducts.get(position).cart%2;
                if (relatedProducts.get(position).cart == 0){
                    relatedProducts.get(position).cart = 1;
                    ContentValues updateContentValues = new ContentValues();
                    updateContentValues.put(Product.Variable.INT_CART, relatedProducts.get(position).cart);
                    relatedProducts.get(position).setUpdateContentValues(updateContentValues);
                    relatedProducts.get(position).getWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID, relatedProducts.get(position).product_id);
                    if (localDataBaseHelper.updateRow(relatedProducts.get(position))){
                        customToast.showShortToast("Item added to Cart");
                        cartButton.setImageResource(R.drawable.cart_2_24);
                    }else{
                        relatedProducts.get(position).cart = 0;
                        customToast.showLongToast("Something wrong");
                    }
                } else if (relatedProducts.get(position).cart == 1){
                    relatedProducts.get(position).cart = 0;
                    ContentValues updateContentValues = new ContentValues();
                    updateContentValues.put(Product.Variable.INT_CART, relatedProducts.get(position).cart);
                    relatedProducts.get(position).setUpdateContentValues(updateContentValues);
                    relatedProducts.get(position).getWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID, relatedProducts.get(position).product_id);
                    if (localDataBaseHelper.updateRow(relatedProducts.get(position))){
                        customToast.showShortToast("Item removed from Cart");
                        cartButton.setImageResource(R.drawable.cart_1_24);
                    }
                    else{
                        relatedProducts.get(position).cart = 1;
                        customToast.showLongToast("Something wrong");
                    }
                }
            }
        });
    }
}