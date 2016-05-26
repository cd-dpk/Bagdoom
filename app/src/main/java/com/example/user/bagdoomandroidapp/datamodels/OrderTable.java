package com.example.user.bagdoomandroidapp.datamodels;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandradasdipok on 4/17/2016.
 */
public class OrderTable implements ITable{

    public String orderDescription;
    public int orderID;
    public int productID;
    public int quantity;
    public int unit_price;
    public int subTotalPrice;

    public WhereClause getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(WhereClause whereClause) {
        this.whereClause = whereClause;
    }

    private WhereClause whereClause = new WhereClause();

    public OrderTable() {}

    public OrderTable( int productID, String orderDescription, int quantity, int unit_price) {
        this.orderDescription = orderDescription;
        this.productID = productID;
        this.quantity = quantity;
        this.unit_price = unit_price;
    }
    public OrderTable(int orderID,  int productID, String orderDescription, int unit_price,int quantity) {
        this.orderDescription = orderDescription;
        this.orderID = orderID;
        this.productID = productID;
        this.unit_price = unit_price;
        this.quantity = quantity;
    }


    @Override
    public String toString() {
        return orderID+","+productID+" , "+orderDescription+ " , "+unit_price+","+quantity;
    }

    @Override
    public String tableName() {
        return "OrderTable";
    }

    @Override
    public String toCreateTableString() {
        return "create table if not exists OrderTable (" +
                "order_id  integer," +
                "product_id integer," +
                "order_des text," +
                "quantity integer," +
                "unit_price integer," +
                "primary key (order_id, product_id)," +
                "foreign key (product_id) references Product (product_id) ) ";
    }

    @Override
    public String toInsertString() {
        return getInsertContentValues().toString();
    }

    @Override
    public String toSelectString() {
        return "select * from "+tableName();
    }

    @Override
    public String toDeleteSingleRowString() {
        return null;
    }

    @Override
    public String toDeleteRows() {
        return null;
    }

    @Override
    public String toSelectSingleRowString() {
        return null;
    }

    @Override
    public ITable toITableFromJSON(JSONObject jsonObject) {
        OrderTable order = new OrderTable();
        try {
            if (jsonObject.has(Variable.ORDER_DES)){
                order.orderDescription = jsonObject.getString(Variable.ORDER_DES);
            }if (jsonObject.has(Variable.ORDER_ID)){
                order.orderID = jsonObject.getInt(Variable.ORDER_ID);
            }if (jsonObject.has(Variable.PRODUCT_ID)){
                order.productID = jsonObject.getInt(Variable.PRODUCT_ID);
            }
            if (jsonObject.has(Variable.UNIT_PRICE)){
                order.unit_price = jsonObject.getInt(Variable.UNIT_PRICE);
            }
            if (jsonObject.has(Variable.QUANTITY)){
                order.quantity = jsonObject.getInt(Variable.QUANTITY);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return order;
    }

    @Override
    public ITable toITableFromCursor(Cursor cursor) {
        OrderTable order = new OrderTable();
        if (cursor.getColumnIndex(Variable.ORDER_DES)!=-1){
            order.orderDescription = cursor.getString(cursor.getColumnIndex(Variable.ORDER_DES));
        }
        if (cursor.getColumnIndex(Variable.ORDER_ID)!=-1){
            order.orderID = cursor.getInt(cursor.getColumnIndex(Variable.ORDER_ID));
        }
        if (cursor.getColumnIndex(Variable.PRODUCT_ID)!=-1){
            order.productID = cursor.getInt(cursor.getColumnIndex(Variable.PRODUCT_ID));
        }
        if (cursor.getColumnIndex(Variable.UNIT_PRICE)!=-1){
            order.unit_price = cursor.getInt(cursor.getColumnIndex(Variable.UNIT_PRICE));
        }
        if (cursor.getColumnIndex(Variable.QUANTITY)!=-1){
            order.quantity = cursor.getInt(cursor.getColumnIndex(Variable.QUANTITY));
        }
        return order;
    }
    @Override
    public boolean isCloned(ITable iTable) {
        return false;
    }
    @Override
    public ITable toClone() {
        return new OrderTable(orderID, productID, orderDescription,unit_price,quantity);
    }
    @Override
    public ContentValues getInsertContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Variable.PRODUCT_ID, productID);
        contentValues.put(Variable.QUANTITY, quantity);
        contentValues.put(Variable.ORDER_DES,orderDescription);
        contentValues.put(Variable.UNIT_PRICE, unit_price);
        contentValues.put(Variable.ORDER_ID, ApplicationConstants.NEXT_ORDER_ID);
        ApplicationConstants.NEXT_ORDER_ID++;
        return contentValues;
    }

    @Override
    public void setUpdateContentValues(ContentValues updateContentValues) {

    }


    @Override
    public ContentValues getUpdateContentValues() {
        return null;
    }

    @Override
    public String getWhereClauseString() {
        return null;
    }

    public static class Variable{
        public static final String ORDER_ID ="order_id";
        public static final String ORDER_DES ="order_des";
        public static final String QUANTITY ="quantity";
        public static final String PRODUCT_ID ="product_id";
        public static final String UNIT_PRICE ="unit_price";
    }

    @Override
    public String toJsonString(){
        String jsonString="";
        jsonString = "{\""+Variable.PRODUCT_ID+"\":"+productID+",\""+Variable.ORDER_DES+"\":\""+orderDescription+"\",\""+Variable.QUANTITY+"\":"+quantity+"}";
        return jsonString;
    }

    public List<OrderTable> toOrders(List<ITable> iTableList){
        List<OrderTable> orderTableList= new ArrayList<OrderTable>();
        for (ITable iTable: iTableList){
            orderTableList.add((OrderTable) iTable);
        }
        return orderTableList;
    }

    public int getSubTotalPrice() {
        return quantity*unit_price;
    }
}
