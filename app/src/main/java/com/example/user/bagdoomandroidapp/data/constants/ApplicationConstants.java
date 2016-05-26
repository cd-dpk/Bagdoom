package com.example.user.bagdoomandroidapp.data.constants;

import android.os.Environment;

import com.example.user.bagdoomandroidapp.datamodels.ITable;
import com.example.user.bagdoomandroidapp.datamodels.OrderTable;
import com.example.user.bagdoomandroidapp.datamodels.User;

import java.io.File;

/**
 * Created by chandradasdipok on 3/15/2016.
 */
public class ApplicationConstants {
    public static final String SERVER_IP="192.168.1.57";

    public static  final String EXTERNAL_STORAGE_FOLDER = Environment.getExternalStorageDirectory().toString();
    public static final String API_KEY="AIzaSyCl6O1tVYsvxDlFdK9AOzShBv1UvWyt49g";
    public static final String SERVER_PRODUCT_IMAGE_DIRECTORY ="product_img";
    public static final String FILE_PATH_SEPARATOR ="/";

    public static final String SERVER_PRODUCT_IMAGE_DIRECTORY_PATH= "http://"+SERVER_IP+FILE_PATH_SEPARATOR+SERVER_PRODUCT_IMAGE_DIRECTORY+FILE_PATH_SEPARATOR;

    public static final String PROJECT_ID="bagdoomapp";
    public static final String PROJECT_NUMBER="524127203847";

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String MESSAGE_CODE_STRING="message_code";
    public static final String PHONE_NUMBER_STRING="phone_number";

    public static final String UPLOAD_FROM_GALLERY="Upload From Gallery";
    public static final String CAPTURE_VIA_CAMERA="Capture New Image";

    public static ITable iTable;
    public static int CATEGORY_ID = 0;
    public static int PRODUCT_ID = 0;
    public static int INVOICE_ID = 0;

    public ITable getITable() {
        return iTable;
    }
    public void setITable(ITable iTable) {
        this.iTable = iTable;
    }

    public static final String PHP_REGISTER_USER = "http://192.168.1.57/php/users.php";
    public static final String PHP_PRODUCT = "http://192.168.1.57/products.php";
    public final static  String PHP_CATEGORY ="http://192.168.1.57/categories.php";
    public final static  String PHP_DATA ="http://192.168.1.57/php/data.php";
    public final static  String PHP_MAKE_A_ORDER ="http://192.168.1.57/php/make_a_order.php";
    public final static String PHP_INVOICE = "http://192.168.1.57/php/invoice_data.php";
    public final static String PHP_ORDER = "http://192.168.1.57/php/order_data.php";


    public static String PHONE_NUMBER="01743972128";
    public static int USER_ID =1;
    public static String PROFILE_IMAGE_FILE_NAME = "user.PNG";
    public static String SENT_CODE ="123456";
    public static String TYPED_CODE = "";
    public static String INVOICE_INFO ="invoice_info";

    public static String NEXT_ORDER_STRING ="NextOrder";
    public static int NEXT_ORDER_ID =0;
}