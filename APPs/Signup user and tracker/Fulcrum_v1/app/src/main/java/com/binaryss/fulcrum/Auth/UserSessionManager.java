package com.binaryss.fulcrum.Auth;

import android.content.Context;

import com.binaryss.fulcrum.Utils.GlobalVariables;
import com.binaryss.fulcrum.Utils.SharedHelper;

public class UserSessionManager {
    SharedHelper sharedHelper;
    public Context context;
    public UserSessionManager(Context context) {
        this.context = context;
        sharedHelper = new SharedHelper(context);
    }
    public void setSessionDetails(Session sessionDetails) {
        sharedHelper.putKey(GlobalVariables.USER_ID, sessionDetails.getId());
        sharedHelper.putKey(GlobalVariables.USER_NAME, sessionDetails.getName());
        sharedHelper.putKey(GlobalVariables.USER_EMAIL, sessionDetails.getEmail());
        sharedHelper.putKey(GlobalVariables.USER_TYPE, sessionDetails.getType());
    }
    public Session getSessionDetails() {
        String id = sharedHelper.getKey(GlobalVariables.USER_ID);
        String name = sharedHelper.getKey(GlobalVariables.USER_NAME);
        String email = sharedHelper.getKey(GlobalVariables.USER_EMAIL);
        String type = sharedHelper.getKey(GlobalVariables.USER_TYPE);
        return new Session(id, email, name, type);
    }

    public boolean isLoggedIn() {
        return sharedHelper.getKeyBoolean("loggedIn");
    }


    public void clearSessionData() {
        sharedHelper.clearStoreInfoData();
    }


    public void setLoggedIn(boolean loggedIn) {
        sharedHelper.putKeyBoolean("loggedIn", loggedIn);

    }


}