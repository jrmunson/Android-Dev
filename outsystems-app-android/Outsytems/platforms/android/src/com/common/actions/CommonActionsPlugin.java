package com.common.actions;

import com.outsystems.android.core.DatabaseHandler;
import com.outsystems.android.helpers.HubManagerHelper;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

/**
 * Created by vmfo on 25/11/14.
 */
public class CommonActionsPlugin extends CordovaPlugin {

    private static final String CLOSE_TUTORIAL = "closeTutorial";
    private static final String LOG_OUT = "logOut";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

        if (action.equals(CLOSE_TUTORIAL)) {
            cordova.getActivity().finish();
        } else if (action.equals(LOG_OUT)) {
            clearCredentials();
            cordova.getActivity().finish();
        }
        return true;
    }

    private void clearCredentials(){
        DatabaseHandler database = new DatabaseHandler(this.cordova.getActivity().getApplicationContext());
        database.updateHubApplicationCredentials(HubManagerHelper.getInstance().getApplicationHosted(),"","","");
    }
}
