package com.example.user.bagdoomandroidapp.datamodels;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONObject;

/**
 * Created by chandradasdipok on 3/23/2016.
 */
public interface ITable {
    public String tableName();
    public String toCreateTableString();
    public String toInsertString();
    public String toSelectString();
    public String toDeleteSingleRowString();
    public String toDeleteRows();
    public String toSelectSingleRowString();
    public ITable toITableFromJSON(JSONObject jsonObject);
    public ITable toITableFromCursor(Cursor cursor);
    public boolean isCloned(ITable iTable);
    public String toJsonString();
    public ITable toClone();
    public ContentValues getInsertContentValues();
    public void setUpdateContentValues(ContentValues updateContentValues);
    public ContentValues getUpdateContentValues();
    public String getWhereClauseString();
}
