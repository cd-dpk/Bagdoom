package com.example.user.bagdoomandroidapp.utils;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by chandradasdipok on 4/11/2016.
 */
public class CustomSnackbar {

    CoordinatorLayout coordinatorLayout;
    public CustomSnackbar(CoordinatorLayout coordinatorLayout) {
            this.coordinatorLayout = coordinatorLayout;
    }
    public void makeSnackBarNotReturn(String message, String action){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        snackbar.show();
    }
    public Snackbar makeSanckBarReturn(String message, String action){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).setAction(action, null);
        return snackbar;
    }

}
