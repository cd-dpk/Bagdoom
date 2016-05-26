package com.example.user.bagdoomandroidapp.datamodels;

/**
 * Created by chandradasdipok on 5/11/2016.
 */
public class WhereClause {

    private String whereClauseString =" 1=1 ";

    public WhereClause addYESWhereClauseString(String name, String value){
        whereClauseString += " and "+name+" = '"+value+"'";
        return this;
    }
    public WhereClause addYESWhereClauseString(String name, int value){
        whereClauseString += " and "+name+" = "+value;
        return this;
    }
    public WhereClause addNOTWhereClauseString(String name, String value){
        whereClauseString += " and "+name+" != '"+value+"'";
        return this;
    }
    public WhereClause addNOTWhereClauseString(String name, int value){
        whereClauseString += " and "+name+" != "+value;
        return this;
    }
    public WhereClause addGREATERWhereClauseString(String name, int value){
        whereClauseString += " and "+name+" > "+value;
        return this;
    }
    public WhereClause addLESSWhereClauseString(String name, String value){
        whereClauseString += " and "+name+" < '"+value+"'";
        return this;
    }
    public WhereClause addLIKEWhereClauseString(String name, String value){
        whereClauseString += " and "+name+" LIKE '%"+value+"%'";
        return this;
    }

    @Override
    public String toString() {
        return whereClauseString;
    }
}
