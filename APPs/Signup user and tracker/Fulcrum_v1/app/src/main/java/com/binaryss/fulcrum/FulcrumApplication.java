package com.binaryss.fulcrum;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;

public class FulcrumApplication extends Application {
    public void onCreate() {
        super.onCreate();
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
            Log.i("FulcrumApplication", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("FulcrumApplication", "Could not initialize Amplify", error);
        }
    }
}
