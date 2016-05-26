package com.example.user.bagdoomandroidapp.datamodels;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chandradasdipok on 5/16/2016.
 */

public class RemoteOrder extends OrderTable {

    public int invoiceID;
    public String orderStatus;
    public String TABLE_NAME="RemoteOrder";
    public WhereClause whereClause = new WhereClause();
    public WhereClause getWhereClause(){
        return whereClause;
    }

    public WhereClause getNewWhereClause(){
        whereClause = new WhereClause();
        return whereClause;
    }
    public RemoteOrder() {
            super();
    }

    public RemoteOrder(int invoiceID,int orderID, int productID, String orderDescription, int quantity, int unit_price, String orderStatus) {
        super();
        this.invoiceID = invoiceID;
        this.orderID =orderID;
        this.productID = productID;
        this.orderDescription = orderDescription;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.orderStatus =orderStatus;
    }

    @Override
    public String toCreateTableString() {
        return "create table if not exists RemoteOrder (" +
                "order_id  integer," +
                "product_id integer," +
                "order_des text," +
                "quantity integer," +
                "unit_price integer," +
                "invoice_id integer," +
                "order_status text," +
                "primary key (order_id, product_id)," +
                "foreign key (product_id) references Product (product_id)," +
                "foreign key (invoice_id) references Invoice (invoice_id))";
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues getInsertContentValues() {
        ContentValues contentValues = super.getInsertContentValues();
        contentValues.put(Invoice.Variable.INT_INVOICE_ID, invoiceID);
        contentValues.put(OrderTable.Variable.ORDER_ID, orderID);
        contentValues.put(Variable.STRING_ORDER_STATUS, orderStatus);
        return contentValues;
    }

    @Override
    public ITable toITableFromCursor(Cursor cursor) {
        RemoteOrder remoteOrder = new RemoteOrder();
        if (cursor.getColumnIndex(OrderTable.Variable.ORDER_DES)!=-1){
            remoteOrder.orderDescription = cursor.getString(cursor.getColumnIndex(OrderTable.Variable.ORDER_DES));
        }
        if (cursor.getColumnIndex(OrderTable.Variable.ORDER_ID)!=-1){
            remoteOrder.orderID = cursor.getInt(cursor.getColumnIndex(OrderTable.Variable.ORDER_ID));
        }
        if (cursor.getColumnIndex(OrderTable.Variable.PRODUCT_ID)!=-1){
            remoteOrder.productID = cursor.getInt(cursor.getColumnIndex(OrderTable.Variable.PRODUCT_ID));
        }
        if (cursor.getColumnIndex(OrderTable.Variable.UNIT_PRICE)!=-1){
            remoteOrder.unit_price = cursor.getInt(cursor.getColumnIndex(OrderTable.Variable.UNIT_PRICE));
        }
        if (cursor.getColumnIndex(OrderTable.Variable.QUANTITY)!=-1){
            remoteOrder.quantity = cursor.getInt(cursor.getColumnIndex(OrderTable.Variable.QUANTITY));
        }
        if (cursor.getColumnIndex(Invoice.Variable.INT_INVOICE_ID)!=-1){
            remoteOrder.invoiceID = cursor.getInt(cursor.getColumnIndex(Invoice.Variable.INT_INVOICE_ID));
        }
        if (cursor.getColumnIndex(Variable.STRING_ORDER_STATUS)!=-1){
            remoteOrder.orderStatus = cursor.getString(cursor.getColumnIndex(Variable.STRING_ORDER_STATUS));
        }
        return remoteOrder;
    }
    @Override
    public ITable toITableFromJSON(JSONObject jsonObject) {
        RemoteOrder remoteOrder = new RemoteOrder();
        try {
            if (jsonObject.has(OrderTable.Variable.ORDER_DES)){
                remoteOrder.orderDescription = jsonObject.getString(OrderTable.Variable.ORDER_DES);
            }if (jsonObject.has(OrderTable.Variable.ORDER_ID)){
                remoteOrder.orderID = jsonObject.getInt(OrderTable.Variable.ORDER_ID);
            }if (jsonObject.has(OrderTable.Variable.PRODUCT_ID)){
                remoteOrder.productID = jsonObject.getInt(OrderTable.Variable.PRODUCT_ID);
            }
            if (jsonObject.has(OrderTable.Variable.UNIT_PRICE)){
                remoteOrder.unit_price = jsonObject.getInt(OrderTable.Variable.UNIT_PRICE);
            }
            if (jsonObject.has(OrderTable.Variable.QUANTITY)){
                remoteOrder.quantity = jsonObject.getInt(OrderTable.Variable.QUANTITY);
            }
            if (jsonObject.has(Invoice.Variable.INT_INVOICE_ID)) {
                remoteOrder.invoiceID = (int) jsonObject.get(Invoice.Variable.INT_INVOICE_ID);
            }
            if (jsonObject.has(Variable.STRING_ORDER_STATUS)) {
                remoteOrder.orderStatus = (String) jsonObject.get(Variable.STRING_ORDER_STATUS);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return remoteOrder;
    }

    @Override
    public String toSelectString() {
        return "select * from "+TABLE_NAME+" where "+whereClause.toString();
    }

    public static class Variable{
        public static final String STRING_ORDER_STATUS ="order_status";
    }

}
