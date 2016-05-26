package com.example.user.bagdoomandroidapp.parser;

import android.util.Log;

import com.example.user.bagdoomandroidapp.datamodels.ITable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chandradasdipok on 3/21/2016.
 */
public class JSONHandler {
    public final static String LOG ="JSONHandler";

    /**
     * It converts JSONArray to Rows of a ITable object
     * @param jsonArray is a JSONArray constructed from rows of a table
     * @return
     */
    public List<ITable> getRowsFromJSONArray(JSONArray jsonArray, ITable iTableType){
        List<ITable> iTables = new ArrayList<ITable>();
        try {
            for (int i=0; i< jsonArray.length(); i++){
                Log.d(JSONHandler.LOG,toStringFromJSONObject(jsonArray.getJSONObject(i)));
                iTables.add(iTableType.toITableFromJSON(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return iTables;
    }

    /**
     * converts a JSONOBJECT to a string
     * @param jsonObject a JSONOBject
     * @return jsonString a string in json format
     */
    private String toStringFromJSONObject(JSONObject jsonObject){
        String jsonString="{";
        Iterator keyIterator = jsonObject.keys();
        while (keyIterator.hasNext()){
            if (!jsonString.equals("{")) jsonString += ",";
            String key = (String) keyIterator.next();
            jsonString += key;
            jsonString += ":";
            try {
                jsonString += jsonObject.get(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        jsonString += "}";
        return  jsonString;
    }


}
