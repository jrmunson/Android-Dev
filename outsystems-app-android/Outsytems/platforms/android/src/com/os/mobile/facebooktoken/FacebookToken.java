package com.os.mobile.facebooktoken;

import com.outsystems.android.core.DatabaseHandler;
import com.outsystems.android.helpers.HubManagerHelper;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by João Gonçalves on 28/04/15.
 */
public class FacebookToken extends CordovaPlugin {

    private static final String SAVE_TOKEN = "saveToken";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

        try {
            String token = args.getString(0);
            String username = args.getString(1);

            if (action.equals(SAVE_TOKEN)) {
                if(saveToken(token, username) > 0)
                    callbackContext.success();
            }
        } catch (JSONException e) {
            callbackContext.error("Invalid arguments.");
            return false;
        }
        return true;
    }

    private int saveToken(String token, String username) {
        DatabaseHandler database = new DatabaseHandler(this.cordova.getActivity().getApplicationContext());
        int result = database.updateHubApplicationCredentials(HubManagerHelper.getInstance().getApplicationHosted(),username,"",token);
        return result;
    }
}
