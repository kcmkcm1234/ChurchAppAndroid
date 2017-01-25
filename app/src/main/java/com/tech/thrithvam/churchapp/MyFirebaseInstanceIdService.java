package com.tech.thrithvam.churchapp;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
       // Log.d(TAG, "Refreshed token: " + refreshedToken);

        FirebaseMessaging.getInstance().subscribeToTopic("common");

        DatabaseHandler db=DatabaseHandler.getInstance(this);

        if(db.GetMyChurch("ChurchID")!=null) {
            FirebaseMessaging.getInstance().subscribeToTopic(db.GetMyChurch("ChurchID"));
        }
    }
}
