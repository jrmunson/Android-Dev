package com.os.mobile.facebooktoken;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

/**
 * Created by João Gonçalves on 28/04/15.
 */
public class FacebookToken extends CordovaPlugin {

    private static final String SAVE_TOKEN = "saveToken";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

        if (action.equals(SAVE_TOKEN)) {
            saveToken();
        }
        return true;
    }

    private void saveToken() {
        
    }
}
