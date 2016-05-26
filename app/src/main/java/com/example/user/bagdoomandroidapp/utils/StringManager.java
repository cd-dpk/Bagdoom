package com.example.user.bagdoomandroidapp.utils;

/**
 * Created by chandradasdipok on 5/15/2016.
 */
public class StringManager {

    public String getAbsoluteString(String string){

        if (string.length()<12){
            return string;
        }
        else{
            String absoluteString = string.substring(0,7);
            absoluteString += "...";
            return absoluteString;
        }
    }
}
