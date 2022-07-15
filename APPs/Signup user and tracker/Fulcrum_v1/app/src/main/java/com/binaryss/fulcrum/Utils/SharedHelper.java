package com.binaryss.fulcrum.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedHelper {
    SharedPreferences storeInfo;
    public SharedPreferences.Editor editor;
    public Context context;
    int private_mode = 0;

    private static final String storeDBName = "fulcrumInfo"; //userData

    public SharedHelper(Context context) {
        this.context = context;
        storeInfo = context.getSharedPreferences(storeDBName, private_mode);
    }

    public void putKey(String key, String value) {
        SharedPreferences.Editor DataDetails = storeInfo.edit();
        DataDetails.putString(key, value);
        DataDetails.apply();
    }

    public String getKey(String key) {
        storeInfo = context.getSharedPreferences(storeDBName, private_mode);
        return storeInfo.getString(key, "");
    }

    public void putKeyBoolean(String key, boolean value) {
        SharedPreferences.Editor DataDetails = storeInfo.edit();
        DataDetails.putBoolean(key, value);
        DataDetails.apply();
    }

    public boolean getKeyBoolean(String key) {
        storeInfo = context.getSharedPreferences(storeDBName, private_mode);
        boolean keyData = storeInfo.getBoolean(key, false);
        return keyData;
    }

    public void clearStoreInfoData() {
        SharedPreferences.Editor clientSpEditor = storeInfo.edit();
        clientSpEditor.clear();
        clientSpEditor.apply();
    }
}
