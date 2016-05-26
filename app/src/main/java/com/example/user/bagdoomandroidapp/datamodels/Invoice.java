package com.example.user.bagdoomandroidapp.datamodels;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chandradasdipok on 5/16/2016.
 */
public class Invoice  implements  ITable{

    public int invoiceID;
    public int totalPrice;
    public String invoiceStatus;
    public String invoicePhoneNumber;
    public String invoiceTime;
    public WhereClause whereClause = new WhereClause();
    public WhereClause getWhereClause(){
        return whereClause;
    }

    public WhereClause getNewWhereClause(){
        whereClause = new WhereClause();
        return whereClause;
    }
    public Invoice() {
    }

    public Invoice(int invoiceID, int totalPrice, String invoiceStatus, String invoicePhoneNumber, String invoiceTime) {
        this.invoiceID = invoiceID;
        this.totalPrice = totalPrice;
        this.invoiceStatus = invoiceStatus;
        this.invoicePhoneNumber = invoicePhoneNumber;
        this.invoiceTime = invoiceTime;
    }

    @Override
    public String tableName() {
        return Variable.TABLE_NAME;
    }

    @Override
    public String toCreateTableString() {
        return "create table IF not exists "+Variable.TABLE_NAME+" ( " +
                Variable.INT_INVOICE_ID+" integer primary key," +
                Variable.INT_TOTAL_PRICE+" integer," +
                Variable.STRING_INVOICE_STATUS+" text," +
                Variable.STRING_INVOICE_PHONE_NUMBER+ " text," +
                Variable.STRING_INVOICE_TIME+ " text," +
                "foreign key ("+Variable.STRING_INVOICE_PHONE_NUMBER+") references User ("+Variable.STRING_INVOICE_PHONE_NUMBER+")"+
                ")";
    }

    @Override
    public String toInsertString() {
        return null;
    }

    @Override
    public String toSelectString() {
        return "select * from "+Variable.TABLE_NAME+" where "+whereClause.toString()+" order by "+Variable.INT_INVOICE_ID+" desc";
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
        Invoice invoice = new Invoice();
        try {
            if (jsonObject.has(Variable.INT_INVOICE_ID)) invoice.invoiceID = (int) jsonObject.get(Variable.INT_INVOICE_ID);
            if (jsonObject.has(Variable.INT_TOTAL_PRICE)) invoice.totalPrice = (int) jsonObject.get(Variable.INT_TOTAL_PRICE);
            if (jsonObject.has(Variable.STRING_INVOICE_STATUS)) invoice.invoiceStatus = (String ) jsonObject.get(Variable.STRING_INVOICE_STATUS);
            if (jsonObject.has(Variable.STRING_INVOICE_PHONE_NUMBER)) invoice.invoicePhoneNumber = (String ) jsonObject.get(Variable.STRING_INVOICE_PHONE_NUMBER);
            if (jsonObject.has(Variable.STRING_INVOICE_TIME)) invoice.invoiceTime = (String ) jsonObject.get(Variable.STRING_INVOICE_TIME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return invoice;
    }

    @Override
    public ITable toITableFromCursor(Cursor cursor) {
        Invoice invoice = new Invoice();
        if (cursor.getColumnIndex(Variable.INT_INVOICE_ID)!=-1){
            invoice.invoiceID = cursor.getInt(cursor.getColumnIndex(Variable.INT_INVOICE_ID));
        }
        if (cursor.getColumnIndex(Variable.INT_TOTAL_PRICE)!=-1){
            invoice.totalPrice = cursor.getInt(cursor.getColumnIndex(Variable.INT_TOTAL_PRICE));
        }
        if (cursor.getColumnIndex(Variable.STRING_INVOICE_STATUS)!=-1){
            invoice.invoiceStatus = cursor.getString(cursor.getColumnIndex(Variable.STRING_INVOICE_STATUS));
        }
        if (cursor.getColumnIndex(Variable.STRING_INVOICE_PHONE_NUMBER)!=-1){
            invoice.invoicePhoneNumber = cursor.getString(cursor.getColumnIndex(Variable.STRING_INVOICE_PHONE_NUMBER));
        }
        if (cursor.getColumnIndex(Variable.STRING_INVOICE_TIME)!=-1){
            invoice.invoiceTime = cursor.getString(cursor.getColumnIndex(Variable.STRING_INVOICE_TIME));
        }
        return invoice;
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
        return new Invoice(invoiceID,totalPrice,invoiceStatus,invoicePhoneNumber,invoiceTime);
    }

    @Override
    public ContentValues getInsertContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Variable.INT_INVOICE_ID,invoiceID);
        contentValues.put(Variable.INT_TOTAL_PRICE,totalPrice);
        contentValues.put(Variable.STRING_INVOICE_STATUS,invoiceStatus);
        contentValues.put(Variable.STRING_INVOICE_PHONE_NUMBER,invoicePhoneNumber);
        contentValues.put(Variable.STRING_INVOICE_TIME,invoiceTime);
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
        return new WhereClause().toString();
    }

    public static class Variable {
        public static String TABLE_NAME ="Invoice";
        public static String INT_INVOICE_ID ="invoice_id";
        public static String INT_TOTAL_PRICE ="total_price";
        public static String STRING_INVOICE_STATUS ="invoice_status";
        public static String STRING_INVOICE_PHONE_NUMBER ="phone";
        public static String STRING_INVOICE_TIME ="invoice_time";
    }

    @Override
    public String toString() {
        return invoiceID+", "+totalPrice+","+invoiceStatus+","+invoicePhoneNumber+","+ invoiceTime;
    }
}
