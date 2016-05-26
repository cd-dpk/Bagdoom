package com.example.user.bagdoomandroidapp.utils;

import android.content.ContentValues;
import android.content.Context;

import com.example.user.bagdoomandroidapp.data.db.DataBaseHelper;
import com.example.user.bagdoomandroidapp.datamodels.ITable;
import com.example.user.bagdoomandroidapp.datamodels.OrderTable;
import com.example.user.bagdoomandroidapp.datamodels.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandradasdipok on 5/14/2016.
 */
public class DataBaseOperator {
    Context context;
    private DataBaseHelper localDataBaseHelper;
    public DataBaseOperator(Context context) {
        this.context = context;
        this.localDataBaseHelper = new DataBaseHelper(context);
    }

    public boolean updateCartOfProduct(Product selectedProduct){
        Product product = (Product) selectedProduct.toClone();
        ContentValues updateContentValues= new ContentValues();
        updateContentValues.put(Product.Variable.INT_CART,product.cart);
        product.setUpdateContentValues(updateContentValues);
        product.getNewWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID,product.product_id);
        if (localDataBaseHelper.updateRow(product)){
            return true;
        }
        return false;
    }
    public boolean updateWishOfProduct(Product selectedProduct){
        Product product = (Product) selectedProduct.toClone();
        ContentValues updateContentValues= new ContentValues();
        updateContentValues.put(Product.Variable.INT_WISH,product.wish);
        product.setUpdateContentValues(updateContentValues);
        product.getNewWhereClause().addYESWhereClauseString(Product.Variable.INT_PRODUCT_ID,product.product_id);
        if (localDataBaseHelper.updateRow(product)){
            return true;
        }
        return false;
    }

    public boolean removeOrderItem(OrderTable selectedOrderItem){
        OrderTable orderTableItem = (OrderTable) selectedOrderItem.toClone();
        orderTableItem.getWhereClause().addYESWhereClauseString(OrderTable.Variable.PRODUCT_ID, orderTableItem.productID);
        if (localDataBaseHelper.deleteRow(selectedOrderItem)){
            return true;
        }
        else{
            return false;
        }
    }
    public boolean insertOrderRowsIntoLocalDBFromCartList(List<Product>products){
        boolean bool = false;
        List<ITable> orderTableRows = new ArrayList<ITable>();
        for (int i=0;i<products.size();i++){
            Product selectedProduct = (Product) products.get(i).toClone();
            selectedProduct.cart = 0;
            bool = updateCartOfProduct(selectedProduct);
            orderTableRows.add(new OrderTable(selectedProduct.product_id,selectedProduct.product_description,selectedProduct.cart,selectedProduct.special_price));
        }
        localDataBaseHelper.deleteRows(new OrderTable());
        if (localDataBaseHelper.insertRows(orderTableRows, new OrderTable())){
            bool = true;
        }else{
            bool = false;
        }
        return bool;
    }
    public boolean insertOrderRowIntoLocalDBFromCartList(Product product){
        boolean bool = false;
        List<ITable> orderTableRows = new ArrayList<ITable>();
        Product selectedProduct = (Product) product.toClone();
        selectedProduct.cart = 0;
        bool = updateCartOfProduct(selectedProduct);
        orderTableRows.add(new OrderTable(selectedProduct.product_id,selectedProduct.product_description,selectedProduct.cart,selectedProduct.special_price));
        localDataBaseHelper.deleteRows(new OrderTable());
        if (localDataBaseHelper.insertRows(orderTableRows, new OrderTable())){
            bool = true;
        }else{
            bool = false;
        }
        return bool;
    }

    public boolean insertOrderRowsIntoLocalDBFromWishList(List<Product>products){
        boolean bool = false;
        List<ITable> orderTableRows = new ArrayList<ITable>();
        for (int i=0;i<products.size();i++){
            Product selectedProduct = (Product) products.get(i).toClone();
            selectedProduct.wish = 0;
            bool = updateWishOfProduct(selectedProduct);
            orderTableRows.add(new OrderTable(selectedProduct.product_id,selectedProduct.product_description,selectedProduct.cart,selectedProduct.special_price));
        }
        localDataBaseHelper.deleteRows(new OrderTable());
        if (localDataBaseHelper.insertRows(orderTableRows, new OrderTable())){
            bool = true;
        }else{
            bool = false;
        }
        return bool;
    }
    public boolean insertOrderRowIntoLocalDBFromWishList(Product product){
        boolean bool = false;
        List<ITable> orderTableRows = new ArrayList<ITable>();
        Product selectedProduct = (Product) product.toClone();
        selectedProduct.wish = 0;
        bool = updateWishOfProduct(selectedProduct);
        orderTableRows.add (new OrderTable(selectedProduct.product_id,selectedProduct.product_description,selectedProduct.cart,selectedProduct.special_price));
        localDataBaseHelper.deleteRows(new OrderTable());
        if (localDataBaseHelper.insertRows(orderTableRows, new OrderTable())){
            bool = true;
        }else{
            bool = false;
        }
        return bool;
    }
    public boolean insertOrderRow(Product selectedProduct){
        localDataBaseHelper.deleteRows(new OrderTable());
        int quantity = selectedProduct.cart;
        if (quantity == 0) quantity =1;
        boolean bool = false;
        if (localDataBaseHelper.insertRow(new OrderTable(selectedProduct.product_id,selectedProduct.product_description,quantity,selectedProduct.special_price))){
            bool = true;
        }
        else{
            bool = false;
        }
        return bool;
    }

}
