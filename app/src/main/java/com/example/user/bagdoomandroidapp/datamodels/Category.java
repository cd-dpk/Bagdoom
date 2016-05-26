package com.example.user.bagdoomandroidapp.datamodels;


import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandradasdipok on 3/21/2016.
 */
public class Category implements ITable{

    public int category_id;
    public String category_name;

    public Category() {
    }

    public Category(int category_id, String category_name) {
        this.category_id = category_id;
        this.category_name = category_name;
    }
    @Override
    public String toInsertString() {
        String insertString="";
        insertString="insert into Category values ("+category_id+",'"+category_name+"')";
        return  insertString;
    }

    @Override
    public String toSelectString() {
        return "Select * from Category";
    }

    @Override
    public ITable toITableFromJSON(JSONObject jsonObject) {

        Category category=new Category();
        try {
            if (jsonObject.has(Variable.INT_CATEGORY_ID))category.category_id = (int) jsonObject.get(Variable.INT_CATEGORY_ID);
            if (jsonObject.has(Variable.STRING_CATEGORY_NAME))category.category_name = (String) jsonObject.get(Variable.STRING_CATEGORY_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return category;
    }

    @Override
    public ITable toITableFromCursor(Cursor cursor) {
        Category category = new Category();

        if (cursor.getColumnIndex(Variable.INT_CATEGORY_ID)!=-1){
            category.category_id = cursor.getInt(cursor.getColumnIndex(Variable.INT_CATEGORY_ID));
        }
        if (cursor.getColumnIndex(Variable.STRING_CATEGORY_NAME)!=-1){
            category.category_name = cursor.getString(cursor.getColumnIndex(Variable.STRING_CATEGORY_NAME));
        }
        return category;
    }

    @Override
    public boolean isCloned(ITable iTable) {
        if (iTable.toString().equals(this.toString())){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public String toJsonString() {
        return null;
    }

    @Override
    public ITable toClone() {
        return new Category(category_id, category_name);
    }

    @Override
    public ContentValues getInsertContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Variable.INT_CATEGORY_ID, category_id);
        contentValues.put(Variable.STRING_CATEGORY_NAME, category_name);
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
    public String tableName() {
        return "Category";
    }

    @Override
    public String toCreateTableString() {
        String createTableString="";
        createTableString="CREATE TABLE IF NOT EXISTS Category ( " +
                "category_id integer primary key," +
                "category_name text" +
                ")";
        return createTableString;
    }

    @Override
    public String toDeleteSingleRowString() {
        return "";
    }

    @Override
    public String toDeleteRows() {
        return "DELETE FROM Category WHERE 1=1";
    }

    @Override
    public String toSelectSingleRowString() {
        return "select * from Category where category_id= "+ category_id;
    }

    public static class Variable {
        public  static  final String INT_CATEGORY_ID ="category_id";
        public  static  final String STRING_CATEGORY_NAME ="category_name";
    }

    @Override
    public String toString() {
        return "("+ category_id+","+
                category_name+")";
    }
    public List<Category> toCategories(List<ITable> iTableList){
        List<Category> categoryList = new ArrayList<Category>();
        for (ITable iTable:iTableList) {
            categoryList.add((Category) iTable);
        }
        return categoryList;
    }
}
