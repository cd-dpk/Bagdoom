package com.example.user.bagdoomandroidapp.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by chandradasdipok on 4/11/2016.
 */
public class ImageHandler {

    public void copyImageOnSDCard(Bitmap userImageBitmap) {
        String externalStoragePathString = ApplicationConstants.EXTERNAL_STORAGE_FOLDER;
        OutputStream outStream = null;
        File file = new File(externalStoragePathString, ApplicationConstants.PROFILE_IMAGE_FILE_NAME);
        try {
            outStream = new FileOutputStream(file);
            userImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("Error1", e.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d("Error2",e.toString());
            e.printStackTrace();
        }
    }
}
