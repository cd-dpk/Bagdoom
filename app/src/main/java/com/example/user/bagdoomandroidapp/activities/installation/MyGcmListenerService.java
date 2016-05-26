package com.example.user.bagdoomandroidapp.activities.installation;

/**
 * Created by User on 2/9/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.data.constants.ApplicationConstants;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message_code = data.getString(ApplicationConstants.MESSAGE_CODE_STRING);
        String phone_number = data.getString(ApplicationConstants.PHONE_NUMBER_STRING);
        Log.d("Dipok", "From: " + from);
        Log.d("Dipok", "Message: " + message_code);
        if (ApplicationConstants.PHONE_NUMBER.equals(phone_number)) {
            ApplicationConstants.SENT_CODE = message_code;
            //sendNotification(message_code);
        }
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, PhoneVerificationStep2Activity.class);
        intent.putExtra(ApplicationConstants.MESSAGE_CODE_STRING, ApplicationConstants.SENT_CODE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.bagdoom)
                .setContentTitle("GCM Message-Sent-And-Received")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}