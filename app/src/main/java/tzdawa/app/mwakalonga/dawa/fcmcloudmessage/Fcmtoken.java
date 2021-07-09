package tzdawa.app.mwakalonga.dawa.fcmcloudmessage;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.Objects;

public class Fcmtoken extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String fcmToken) {
        //super.onNewToken(fcmToken);
        if (!(fcmToken.isEmpty())) {
            Log.e("CLOUD_FCM_TOKEN", fcmToken);
            /*
                implementation 'com.google.firebase:firebase-perf:19.1.0'
    implementation 'com.google.firebase:firebase-analytics:18.0.1'
    implementation 'com.google.firebase:firebase-crashlytics:17.3.0'
    implementation 'com.google.firebase:firebase-dynamic-links:19.1.1'
    implementation 'com.google.firebase:firebase-ads:19.6.0'
             */
        }
    }
}