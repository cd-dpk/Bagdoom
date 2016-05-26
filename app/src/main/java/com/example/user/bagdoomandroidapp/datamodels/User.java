package com.example.user.bagdoomandroidapp.datamodels;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chandradasdipok on 3/23/2016.
 */
public class User implements ITable{

    public String name;
    public String email;
    public String phone;
    public String address;
    public String photoID;

    public User() {}

    public User(String phone,String name, String email, String address, String photoID) {
        this();
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.photoID = photoID;
    }

    @Override
    public String toInsertString() {
        return "insert into User (phone, name, email, address, photoID ) values (" +
                "'"+phone+"',"+
                "'"+name +"'," +
                "'"+email+"'," +
                "'"+address+"'," +
                "'"+photoID+"'"+
                ")";
    }

    @Override
    public String toSelectString() {
        return "select * from User";
    }

    @Override
    public ITable toITableFromJSON(JSONObject jsonObject) {
        User user = new User();
        try {
            if (jsonObject.has(Variable.STRING_PHONE)) user.phone = (String) jsonObject.get(Variable.STRING_PHONE);
            if (jsonObject.has(Variable.STRING_NAME))user.name = (String) jsonObject.get(Variable.STRING_NAME);
            if (jsonObject.has(Variable.STRING_EMAIL))user.email = (String) jsonObject.get(Variable.STRING_EMAIL);
            if (jsonObject.has(Variable.STRING_ADDRESS))user.address = (String) jsonObject.get(Variable.STRING_ADDRESS);
            if (jsonObject.has(Variable.STRING_PHOTOID))user.photoID = (String) jsonObject.get(Variable.STRING_PHOTOID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public ITable toITableFromCursor(Cursor cursor) {
        User user = new User();
        if (cursor.getColumnIndex(Variable.STRING_PHONE)!=-1){
            user.phone = cursor.getString(cursor.getColumnIndex(Variable.STRING_PHONE));
        }
        if (cursor.getColumnIndex(Variable.STRING_ADDRESS)!=-1){
            user.address = cursor.getString(cursor.getColumnIndex(Variable.STRING_ADDRESS));
        }
        if (cursor.getColumnIndex(Variable.STRING_EMAIL)!=-1){
            user.email = cursor.getString(cursor.getColumnIndex(Variable.STRING_EMAIL));
        }
        if (cursor.getColumnIndex(Variable.STRING_NAME)!=-1){
            user.name = cursor.getString(cursor.getColumnIndex(Variable.STRING_NAME));
        }
        if (cursor.getColumnIndex(Variable.STRING_PHOTOID)!=-1){
            user.photoID = cursor.getString(cursor.getColumnIndex(Variable.STRING_PHOTOID));
        }return user;
    }

    @Override
    public boolean isCloned(ITable iTable) {
        if (iTable.toString().equals(this.toString())) return true;
        else return false;
    }

    @Override
    public String toJsonString() {
        return null;
    }

    @Override
    public String tableName() {
        return "USER";
    }

    @Override
    public String toCreateTableString() {
        return "create table if not exists User (" +
                "phone text," +
                "name text," +
                "email text," +
                "address text," +
                "photoID text," +
                "primary key (phone)" +
                ")";
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
        return "select * from User where phone = '"+phone+"'";
    }
    @Override
    public ITable toClone(){
        return  new User(phone,name,email,address,photoID);
    }

    @Override
    public ContentValues getInsertContentValues() {
        ContentValues contentValues= new ContentValues();
        contentValues.put(Variable.STRING_PHONE,phone);
        contentValues.put(Variable.STRING_NAME,name);
        contentValues.put(Variable.STRING_EMAIL,email);
        contentValues.put(Variable.STRING_ADDRESS,address);
        contentValues.put(Variable.STRING_PHOTOID,photoID);
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

    @Override
    public String toString() {
        return "("+
                phone+","+
                name+","+
                email+","+
                address+","+
                photoID+")";
    }
    public static class Variable {
        public final static String STRING_NAME = "name";
        public final static String STRING_EMAIL = "email";
        public final static String STRING_PHONE = "phone";
        public final static String STRING_ADDRESS = "address";
        public final static String STRING_PHOTOID ="photoID";
    }
}
