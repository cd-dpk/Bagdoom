package com.example.user.bagdoomandroidapp.datamodels;


import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by User on 3/3/2016.
 */
public class Product  implements ITable{
    public int product_id;
    public String product_photo_url;
    public int category_id;
    public String product_name;
    public String product_description;
    public int price;
    public int special_price;
    public int quantity;
    public String entry_time;
    public int cart=0;
    public int wish=0;
    private WhereClause whereClause = new WhereClause();
    private ContentValues updateContentValues;

    public WhereClause getWhereClause(){
        return whereClause;
    }
    public WhereClause getNewWhereClause(){
        whereClause = new WhereClause();
        return whereClause;
    }
    @Override
    public String getWhereClauseString() {
        return whereClause.toString();
    }
    public Product() {
        category_id = -1;
    }

    public Product(int product_id, String product_photo_url, int category_id, String product_name, String product_description, int price, int special_price, int quantity, String entry_time, int cart, int wish) {
        this.product_id = product_id;
        this.product_photo_url = product_photo_url;
        this.category_id = category_id;
        this.product_name = product_name;
        this.product_description = product_description;
        this.price = price;
        this.special_price = special_price;
        this.quantity = quantity;
        this.entry_time = entry_time;
        this.cart = cart;
        this.wish = wish;
    }

    public Product(int product_id, String product_photo_url, int category_id, String product_name, String product_description, int price, int special_price, int quantity, String entry_time) {
        this.product_id = product_id;
        this.product_photo_url = product_photo_url;
        this.category_id = category_id;
        this.product_name = product_name;
        this.product_description = product_description;
        this.price = price;
        this.special_price = special_price;
        this.quantity = quantity;
        this.entry_time = entry_time;
    }
    @Override
    public String toInsertString() {
        String insertString="";
        insertString= "insert into "+tableName()+" values( "+product_id+",'"+product_photo_url+"',"+category_id+",'"+product_name+"','"+product_description+"',"+ price +","+special_price+","+quantity+")";
        return insertString;
    }

    @Override
    public String toSelectString() {
        return "select * from  "+ tableName()+" where " +whereClause.toString();
    }

    @Override
    public String toSelectSingleRowString() {
        return "select * from Product where "+ whereClause.toString();
    }

    @Override
    public ITable toITableFromJSON(JSONObject jsonObject) {
        Product product = new Product();
        try {
            if (jsonObject.has(Variable.INT_PRODUCT_ID)) product.product_id = (int) jsonObject.get(Variable.INT_PRODUCT_ID);
            if (jsonObject.has(Variable.STRING_PRODUCT_PHOTO_URL))product.product_photo_url = (String) jsonObject.get(Variable.STRING_PRODUCT_PHOTO_URL);
            if (jsonObject.has(Variable.INT_PRODUCT_ID))product.category_id = (int) jsonObject.get(Variable.INT_CATEGORY_ID);
            if (jsonObject.has(Variable.STRING_PRODUCT_NAME))product.product_name = (String) jsonObject.get(Variable.STRING_PRODUCT_NAME);
            if (jsonObject.has(Variable.STRING_PRODUCT_DESCRIPTION))product.product_description = (String) jsonObject.get(Variable.STRING_PRODUCT_DESCRIPTION);
            if (jsonObject.has(Variable.INT_PRICE))product.price = (int) jsonObject.get(Variable.INT_PRICE);
            if (jsonObject.has(Variable.INT_SPECIAL_PRICE))product.special_price = (int) jsonObject.get(Variable.INT_SPECIAL_PRICE);
            if (jsonObject.has(Variable.INT_QUANTITY))product.quantity = (int) jsonObject.get(Variable.INT_QUANTITY);
            if (jsonObject.has(Variable.STRING_ENTRY_TIME)) product.entry_time = (String) jsonObject.get(Variable.STRING_ENTRY_TIME);
            if (jsonObject.has(Variable.INT_WISH)) product.wish = (int) jsonObject.get(Variable.INT_WISH);
            if (jsonObject.has(Variable.INT_CART)) product.cart = (int) jsonObject.get(Variable.INT_CART);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return product;
    }

    @Override
    public ITable toITableFromCursor(Cursor cursor) {
            Product product = new Product();
        if (cursor.getColumnIndex(Variable.INT_PRODUCT_ID) != -1) {
            product.product_id = cursor.getInt(cursor.getColumnIndex(Variable.INT_PRODUCT_ID));
        }
        if (cursor.getColumnIndex(Variable.INT_CATEGORY_ID) != -1) {
            product.category_id = cursor.getInt(cursor.getColumnIndex(Variable.INT_CATEGORY_ID));
        }
        if (cursor.getColumnIndex(Variable.INT_QUANTITY) != -1) {
            product.quantity = cursor.getInt(cursor.getColumnIndex(Variable.INT_QUANTITY));
        }
        if (cursor.getColumnIndex(Variable.STRING_PRODUCT_NAME) != -1) {
            product.product_name = cursor.getString(cursor.getColumnIndex(Variable.STRING_PRODUCT_NAME));
        }
        if (cursor.getColumnIndex(Variable.STRING_PRODUCT_PHOTO_URL) != -1) {
            product.product_photo_url = cursor.getString(cursor.getColumnIndex(Variable.STRING_PRODUCT_PHOTO_URL));
        }
        if (cursor.getColumnIndex(Variable.STRING_PRODUCT_DESCRIPTION) != -1) {
            product.product_description = cursor.getString(cursor.getColumnIndex(Variable.STRING_PRODUCT_DESCRIPTION));
        }
        if (cursor.getColumnIndex(Variable.INT_PRICE) != -1) {
            product.price = cursor.getInt(cursor.getColumnIndex(Variable.INT_PRICE));
        }
        if (cursor.getColumnIndex(Variable.INT_SPECIAL_PRICE) != -1) {
            product.special_price = cursor.getInt(cursor.getColumnIndex(Variable.INT_SPECIAL_PRICE));
        }
        if (cursor.getColumnIndex(Variable.INT_WISH)!=-1){
            product.wish = cursor.getInt(cursor.getColumnIndex(Variable.INT_WISH));
        }
        if (cursor.getColumnIndex(Variable.INT_CART)!=-1){
            product.cart = cursor.getInt(cursor.getColumnIndex(Variable.INT_CART));
        }
        return  product;
    }

    @Override
    public boolean isCloned(ITable iTable) {
        return false;
    }

    @Override
    public String toJsonString() {
        return null;
    }

    @Override
    public ITable toClone() {
        return new Product(product_id, product_photo_url,category_id, product_name, product_description, price, special_price, quantity, entry_time, cart, wish);
    }

    @Override
    public ContentValues getInsertContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Variable.INT_PRODUCT_ID,product_id);
        contentValues.put(Variable.STRING_PRODUCT_PHOTO_URL,product_photo_url);
        contentValues.put(Variable.INT_CATEGORY_ID, category_id);
        contentValues.put(Variable.STRING_PRODUCT_NAME, product_name);
        contentValues.put(Variable.STRING_PRODUCT_DESCRIPTION, product_description);
        contentValues.put(Variable.INT_PRICE, price);
        contentValues.put(Variable.INT_SPECIAL_PRICE, special_price);
        contentValues.put(Variable.INT_QUANTITY, quantity);
        contentValues.put(Variable.STRING_ENTRY_TIME,entry_time);
        contentValues.put(Variable.INT_WISH , wish);
        contentValues.put(Variable.INT_CART , cart);
        return contentValues;
    }

    @Override
    public void setUpdateContentValues(ContentValues updateContentValues) {
        this.updateContentValues = updateContentValues;
    }

    @Override
    public ContentValues getUpdateContentValues() {
        return updateContentValues;
    }

    @Override
    public String tableName() {
        return "Product";
    }

    @Override
    public String toCreateTableString() {
        String createTableString="";
        createTableString="create table IF not exists Product ( " +
                "product_id integer primary key," +
                "product_photo_url text," +
                "category_id integer," +
                "product_name text," +
                "product_description text," +
                "price integer," +
                "special_price integer," +
                "quantity integer," +
                "entry_time text," +
                "wish integer," +
                "cart integer," +
                "foreign key (category_id) references Category (category_id)" +
                ")";
        return createTableString;
    }
    @Override
    public String toDeleteSingleRowString() {
        return Variable.INT_PRODUCT_ID+" = "+ product_id;
    }

    @Override
    public String toDeleteRows() {
        return "DELETE FROM "+ tableName()+" WHERE 1=1";
    }
    public static class Variable{
            public static final String INT_PRODUCT_ID ="product_id";
            public static final String STRING_PRODUCT_PHOTO_URL ="product_photo_url";
            public static final String INT_CATEGORY_ID ="category_id";
            public static final String STRING_PRODUCT_NAME ="product_name";
            public static final String STRING_PRODUCT_DESCRIPTION ="product_description";
            public static final String INT_PRICE ="price";
            public static final String INT_SPECIAL_PRICE ="special_price";
            public static final String INT_QUANTITY = "quantity";
            public static final String STRING_ENTRY_TIME="entry_time";
            public static final String INT_WISH = "wish";
            public static final String INT_CART = "cart";
    }
    @Override
    public String toString() {
        return "("+ product_id+","+
                product_photo_url+","+
                category_id+","+
                product_name+","+
                product_description+","+
                price +","+
                special_price+","+
                quantity+"," +
                wish+"," +
                cart+"," +
                entry_time+")";
    }
    public List<Product> toProducts(List<ITable> iTableList){
        List<Product> productList =  new ArrayList<Product>();
        for (ITable iTable:iTableList) {
            productList.add((Product) iTable);
        }
        return productList;
    }
    public List<String > decodePhotoUrl(){
        List<String > photoList = new ArrayList<String>();
        StringTokenizer stringTokenizer = new StringTokenizer(product_photo_url,";",false);
        while (stringTokenizer.hasMoreTokens()){
            photoList.add(stringTokenizer.nextToken());
        }
        return  photoList;
    }
}