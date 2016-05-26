package com.example.user.bagdoomandroidapp.data.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.user.bagdoomandroidapp.datamodels.Category;
import com.example.user.bagdoomandroidapp.datamodels.ITable;
import com.example.user.bagdoomandroidapp.datamodels.Invoice;
import com.example.user.bagdoomandroidapp.datamodels.OrderTable;
import com.example.user.bagdoomandroidapp.datamodels.Product;
import com.example.user.bagdoomandroidapp.datamodels.RemoteOrder;
import com.example.user.bagdoomandroidapp.datamodels.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandradasdipok on 3/21/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "ANDROID_DATABASE";
    public static final int DATABASE_VERSION = 1;
    public static final String LOG = "DATABASE_HANDLER";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * @param db
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(new User().toCreateTableString());
 //       Log.d(DataBaseHelper.LOG, new User().toCreateTableString());
        db.execSQL(new Category().toCreateTableString());
//        Log.d(DataBaseHelper.LOG, new Category().toCreateTableString());
        db.execSQL(new Product().toCreateTableString());
//        Log.d(DataBaseHelper.LOG, new Product().toCreateTableString());
        db.execSQL(new OrderTable().toCreateTableString());
 //       Log.d(DataBaseHelper.LOG, new OrderTable().toCreateTableString());
        db.execSQL(new Invoice().toCreateTableString());
 //       Log.d(DataBaseHelper.LOG, new Invoice().toCreateTableString());
        db.execSQL(new RemoteOrder().toCreateTableString());
 //       Log.d(DataBaseHelper.LOG, new RemoteOrder().toCreateTableString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * @param iTableList is the rows of a table from remote server
     * @param iTableType defines the table name
     * @return true if the data is inserted right otherwise false if  error
     */
    public boolean insertRowsFromServer(List<ITable> iTableList, ITable iTableType) {
        boolean flag = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(iTableType.tableName(), "1=1", null);
        for (ITable iTable : iTableList) {
            flag = insertRow(iTable);
/*
            Log.d(DataBaseHelper.LOG, iTable.toInsertString());
            if (sqLiteDatabase.insert(iTableType.tableName(), null, iTable.getInsertContentValues()) == -1) {
                flag = false;
            } else {
                flag = true;
            }
*/
        }
        sqLiteDatabase.close();
        return flag;
    }

    /**
     * Gives us a row from a table with its primary key
     *
     * @param iTable a ITable object initialize with primary key
     * @return a row of a table
     */
    public ITable selectRow(ITable iTable) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(iTable.toSelectSingleRowString(), null);
        if (cursor != null) cursor.moveToFirst();
        sqLiteDatabase.close();
        return iTable.toITableFromCursor(cursor);
    }
    /**
     * It gives  us the all rows of local databases
     *
     * @param iTableType
     * @return row list
     */
    public List<ITable> selectRows(ITable iTableType) {
        List<ITable> iTableList = new ArrayList<ITable>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(iTableType.toSelectString(), null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                iTableList.add(iTableType.toITableFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        return iTableList;
    }

    /**
     * delete all the rows from a table
     *
     * @param iTableType is the table type of databse
     */
    public void deleteRows(ITable iTableType) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(iTableType.tableName(), "1=1", null);
        sqLiteDatabase.close();
    }

    public boolean deleteRow(ITable iTable) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        if (sqLiteDatabase.delete(iTable.tableName(),iTable.getWhereClauseString(),null)!=0){
            sqLiteDatabase.close();
            return true;
        }else {
            sqLiteDatabase.close();
            return false;
        }
    }

    public boolean insertRows(List<ITable> iTableList, ITable iTableType) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        boolean flag = false;
        Log.d(DataBaseHelper.LOG, iTableList.size()+"");
        for (ITable iTable : iTableList) {
            if (sqLiteDatabase.insert(iTableType.tableName(), null, iTable.getInsertContentValues()) == -1) {
                flag = false;
            } else {
                flag = true;
            }
        }
        sqLiteDatabase.close();
        return flag;
    }

    /**
     * insert a row
     * @param iTable
     * @return true if successful otherwise false if fail to insert
     */
    public boolean insertRow(ITable iTable) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Log.d(DataBaseHelper.LOG, iTable.toString());
        if (sqLiteDatabase.insert(iTable.tableName(), null, iTable.getInsertContentValues()) == -1) {
            sqLiteDatabase.close();
            return false;
        } else {
            sqLiteDatabase.close();
            return true;
        }
    }

    /**
     * @param iTable
     * @return
     */
    public boolean updateRow(ITable iTable) {
        //update row with table's primary key
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Log.d(DataBaseHelper.LOG, iTable.getUpdateContentValues().toString()+":"+iTable.getWhereClauseString());
        int flag = sqLiteDatabase.update(iTable.tableName(), iTable.getUpdateContentValues(), iTable.getWhereClauseString(), null);
        sqLiteDatabase.close();
        if (flag>=0){
            return  true;
        }
        else{
            return  false;
        }
    }
    public int countRows(ITable iTable){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int count =-1;
        Cursor cursor = sqLiteDatabase.rawQuery("select count(*) from " + iTable.tableName() + " where " + iTable.getWhereClauseString(), null);
        if (cursor != null && cursor.moveToFirst()){
            if (cursor.getColumnIndex("count(*)")!=-1){
                count = cursor.getInt(cursor.getColumnIndex("count(*)"));
            }
        }
        sqLiteDatabase.close();
        return count;
    }

}
