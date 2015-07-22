/*
 * OutSystems Project
 * 
 * Copyright (C) 2014 OutSystems.
 * 
 * This software is proprietary.
 */
package com.outsystems.android;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.arellomobile.android.push.PushManager;
import com.outsystems.android.core.DatabaseHandler;
import com.outsystems.android.core.EventLogger;
import com.outsystems.android.helpers.CustomConfiguration;
import com.outsystems.android.helpers.DeepLinkController;
import com.outsystems.android.helpers.HubManagerHelper;
import com.outsystems.android.model.DeepLink;
import com.outsystems.android.model.HubApplicationModel;

/**
 * Class description.
 * 
 * @author <a href="mailto:vmfo@xpand-it.com">vmfo</a>
 * @version $Revision: 666 $
 * 
 */
public class SplashScreen extends Activity {

    /** The time splash screen. */
    public static int TIME_SPLASH_SCREEN = 2000;
    private static PushManager pushManager;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // Push Messages    	
    
        // Create and start push manager
        pushManager = PushManager.getInstance(this);

        try {
            pushManager.onStartup(this);
        } catch (Exception e) {
            // push notifications are not available or AndroidManifest.xml is not configured properly
            EventLogger.logError(getClass(), e);
        }

        // Register for push!
        pushManager.registerForPushNotifications();

  
        // Get data from Deep Link
        Uri data = this.getIntent().getData();

        if(data != null){
        	DeepLinkController.getInstance().createSettingsFromUrl(data);
        }        
        
        
        // Add delay to show splashscreen
        delaySplashScreen();
    }

    private void delaySplashScreen() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (CustomConfiguration.CASE == 1) {
                    /**
                     * #Case 1 - Open WebApplication
                     *
                     */

                    Intent intent = new Intent(getApplicationContext(), WebApplicationActivity.class);
                    intent.putExtra("key_url", CustomConfiguration.URL_WEB_APPLICATION);
                    startActivity(intent);
                    finish();
                } else {
                    // Check if Open Tutorial
                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                    boolean isFirstTime = prefs.getBoolean("firstTimeKey", true);

                    if (isFirstTime) {
                        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                        editor.putBoolean("firstTimeKey", false);
                        editor.apply();

                        goNextActivity();

                        Intent intent = new Intent(getApplicationContext(), WebApplicationActivity.class);
                        intent.putExtra("key_url", CustomConfiguration.URL_TUTORIAL);
                        startActivity(intent);
                        finish();
                    } else {
                        goNextActivity();
                    }
                }

            }
        }, TIME_SPLASH_SCREEN);
    }

    protected void goNextActivity() {

        if (CustomConfiguration.CASE == 2 || CustomConfiguration.CASE == 3) {
            /**
             * Case 2 - Open Login with server fixed
             *
             */
            HubApplicationModel hubApplication = new HubApplicationModel(CustomConfiguration.HOSTNAME, "", "", null, CustomConfiguration.NAME, CustomConfiguration.IS_JAVA_SERVER, "");
            if (hubApplication != null) {
                HubManagerHelper.getInstance().setApplicationHosted(hubApplication.getHost());
                HubManagerHelper.getInstance().setJSFApplicationServer(hubApplication.isJSF());
            }
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            if (hubApplication != null) {
                intent.putExtra(LoginActivity.KEY_INFRASTRUCTURE_NAME, hubApplication.getName());
                intent.putExtra(LoginActivity.KEY_AUTOMATICLY_LOGIN, true);
            }
            startActivity(intent);
            finish();
            return;
        }
        
        DatabaseHandler database = new DatabaseHandler(getApplicationContext());
        List<HubApplicationModel> hubApplications = database.getAllHubApllications();
        openHubActivity();
        
        if(DeepLinkController.getInstance().hasValidSettings()){
        	DeepLink deepLinkSettings = DeepLinkController.getInstance().getDeepLinkSettings();
        	HubManagerHelper.getInstance().setApplicationHosted(deepLinkSettings.getEnvironment());
        	
            Intent intent = new Intent(getApplicationContext(), HubAppActivity.class);
            startActivity(intent);
        	
        }
        else{
	        	
	        if (hubApplications != null && hubApplications.size() > 0) {
	            HubApplicationModel hubApplication = hubApplications.get(0);
	            if (hubApplication != null) {
	                HubManagerHelper.getInstance().setApplicationHosted(hubApplication.getHost());
	                HubManagerHelper.getInstance().setJSFApplicationServer(hubApplication.isJSF());
	            }
	            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
	            if (hubApplication != null) {
	                intent.putExtra(LoginActivity.KEY_INFRASTRUCTURE_NAME, hubApplication.getName());
	                intent.putExtra(LoginActivity.KEY_AUTOMATICLY_LOGIN, true);
	            }
	            startActivity(intent);
	        }
	    }
        finish();
    }

    private void openHubActivity() {
        Intent intent = new Intent(this, HubAppActivity.class);
        startActivity(intent);
    }        
          
}
