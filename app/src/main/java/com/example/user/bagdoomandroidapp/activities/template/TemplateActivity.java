package com.example.user.bagdoomandroidapp.activities.template;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.bagdoom.CartListActivity;
import com.example.user.bagdoomandroidapp.activities.bagdoom.InvoiceListActivity;
import com.example.user.bagdoomandroidapp.activities.bagdoom.ProfileActivity;
import com.example.user.bagdoomandroidapp.activities.bagdoom.SearchProductActivity;
import com.example.user.bagdoomandroidapp.activities.bagdoom.WishListActivity;
import com.example.user.bagdoomandroidapp.data.db.DataBaseHelper;
import com.example.user.bagdoomandroidapp.datamodels.Product;
import com.example.user.bagdoomandroidapp.utils.CustomToast;
import com.example.user.bagdoomandroidapp.utils.DataBaseOperator;

public abstract class TemplateActivity extends AppCompatActivity {

    protected Toolbar templateToolbar;
    protected TextView subtitleText;

    protected CustomToast customToast = new CustomToast(this);
    protected DataBaseHelper localDataBaseHelper = new DataBaseHelper(this);
    protected DataBaseOperator dataBaseOperator = new DataBaseOperator(this);

    public abstract void initView();
    public abstract void loadData();
    public abstract void initializeViewByData();
    public abstract void listenView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
        initializeViewByData();
        listenView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_cart) {
            Product sampleProduct = new Product();
            sampleProduct.getNewWhereClause().addGREATERWhereClauseString(Product.Variable.INT_CART,0);
            int count = localDataBaseHelper.countRows(sampleProduct);
            if (count == -1){
                customToast.showLongToast("Something is wrong");
            } else if (count == 0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TemplateActivity.this);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialogBuilder.setTitle("");
                alertDialogBuilder.setMessage("No item in Cart !");
                alertDialogBuilder.create().show();
            } else {
                Intent intent = new Intent(this, CartListActivity.class);
                startActivity(intent);
            }
        } else if (item.getItemId() == R.id.toolbar_search) {
//            customToast.showLongToast("Search Function is under construction");
            Intent intent = new Intent(TemplateActivity.this,SearchProductActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.toolbar_profile) {
            Intent intent = new Intent(TemplateActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.toolbar_wishlist) {
            Product sampleProduct = new Product();
            sampleProduct.getNewWhereClause().addGREATERWhereClauseString(Product.Variable.INT_WISH,0);
            int count = localDataBaseHelper.countRows(sampleProduct);
            if (count == -1){
                customToast.showLongToast("Something is wrong");
            }else if(count ==0){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TemplateActivity.this);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialogBuilder.setMessage("No item in Wish list!");
                alertDialogBuilder.create().show();
            }else{
                Intent intent = new Intent(TemplateActivity.this, WishListActivity.class);
                startActivity(intent);
            }
        } else if (item.getItemId() == R.id.toolbar_action_settings) {
            customToast.showLongToast("Settings Function is under construction");
        }
        else if (item.getItemId() == R.id.toolbar_invoice){
            Intent intent = new Intent(TemplateActivity.this, InvoiceListActivity.class);
            startActivity(intent);
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}