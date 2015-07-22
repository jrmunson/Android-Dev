/*
 * OutSystems Project
 * 
 * Copyright (C) 2014 OutSystems.
 * 
 * This software is proprietary.
 */
package com.outsystems.android;

import java.util.ArrayList;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.outsystems.android.core.DatabaseHandler;
import com.outsystems.android.core.EventLogger;
import com.outsystems.android.core.Installation;
import com.outsystems.android.core.WSRequestHandler;
import com.outsystems.android.core.WebServicesClient;
import com.outsystems.android.helpers.CustomConfiguration;
import com.outsystems.android.helpers.DeepLinkController;
import com.outsystems.android.helpers.HubManagerHelper;
import com.outsystems.android.model.Application;
import com.outsystems.android.model.HubApplicationModel;
import com.outsystems.android.model.Login;

/**
 * Class description.
 *
 * @author <a href="mailto:vmfo@xpand-it.com">vmfo</a>
 * @version $Revision: 666 $
 */
public class LoginActivity extends BaseActivity {

    public static String KEY_INFRASTRUCTURE_NAME = "infrastructure";
    public static String KEY_AUTOMATICLY_LOGIN = "key_login_automaticly";

    public boolean doLogin = false;

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            String userName = ((EditText) findViewById(R.id.edit_text_user_mail)).getText().toString();
            String password = ((EditText) findViewById(R.id.edit_text_passwod)).getText().toString();

            if (!"".equals(userName) && !"".equals(password)) {
                callLoginService(findViewById(R.id.view_buttons), userName, password);
            } else {
                ((EditText) findViewById(R.id.edit_text_user_mail)).setError(getResources().getString(
                        R.string.label_error_login));
                ((EditText) findViewById(R.id.edit_text_passwod)).setError(getResources().getString(
                        R.string.label_error_login));
                showError(findViewById(R.id.root_view));
            }
        }
    };

    private OnClickListener onClickListenerSignUp = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), WebApplicationActivity.class);
            intent.putExtra("key_url", CustomConfiguration.URL_SIGN_UP);
            startActivity(intent);
        }
    };

    private OnClickListener onClickListenerFacebookLogin = new OnClickListener() {
        @Override
        public void onClick(View view) {
            openFacebookWebApplicationActivity(CustomConfiguration.LOGIN_FACEBOOK);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String infrastructure = bundle.getString(KEY_INFRASTRUCTURE_NAME);
            doLogin = bundle.getBoolean(KEY_AUTOMATICLY_LOGIN);

            ((TextView) findViewById(R.id.text_view_label_application_value)).setText(infrastructure);
        }

        final Button buttonLogin = (Button) findViewById(R.id.button_login);
        final Button buttonSignUp = (Button) findViewById(R.id.button_sign_up);
        final LinearLayout buttonFacebookLogin = (LinearLayout) findViewById(R.id.button_facebook_login);

        buttonLogin.setOnClickListener(onClickListener);
        buttonSignUp.setOnClickListener(onClickListenerSignUp);
        buttonFacebookLogin.setOnClickListener(onClickListenerFacebookLogin);

        DatabaseHandler database = new DatabaseHandler(getApplicationContext());
        if (database.getHubApplication(CustomConfiguration.HOSTNAME) == null) {
            database.addHostHubApplication(CustomConfiguration.HOSTNAME, CustomConfiguration.NAME, HubManagerHelper
                    .getInstance().isJSFApplicationServer());
        }

        //DatabaseHandler database = new DatabaseHandler(getApplicationContext());
        HubApplicationModel hub = database.getHubApplication(HubManagerHelper.getInstance().getApplicationHosted());

        // Check if deep link has valid settings                
        if (DeepLinkController.getInstance().hasValidSettings()) {

            Object[] params = new Object[1];
            params[0] = hub;

            DeepLinkController.getInstance().resolveOperation(this, params);

        }

        if(hub != null && (hub.getUserName() != null && !"".equals(hub.getUserName()))
                && (hub.getToken() != null && !"".equals(hub.getToken()))){

            openFacebookWebApplicationActivity(hub.getUserName(), hub.getToken());

        } else if (hub != null && (hub.getUserName() != null && !"".equals(hub.getUserName()))
                && (hub.getPassword() != null && !"".equals(hub.getPassword()))) {
            ((EditText) findViewById(R.id.edit_text_user_mail)).setText(hub.getUserName());
            ((EditText) findViewById(R.id.edit_text_passwod)).setText(hub.getPassword());
            if (doLogin) {
                callLoginService(findViewById(R.id.view_buttons), hub.getUserName(), hub.getPassword());
                getIntent().removeExtra(KEY_AUTOMATICLY_LOGIN);
            }
        }

        if (CustomConfiguration.CASE == 2 || CustomConfiguration.CASE == 3) {
            // Hide action bar
            getSupportActionBar().hide();
        }

        // Add a custom Action Bar
        setupActionBar();

        final EditText editText = (EditText) findViewById(R.id.edit_text_user_mail);
        editText.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                int width = editText.getWidth();
                int height = editText.getHeight();

             //   ViewGroup.LayoutParams params = buttonLogin.getLayoutParams();
             //   params.height = height;
             //   params.width = width;
              //  buttonLogin.requestLayout();

             //   ViewGroup.LayoutParams paramsSignUp = buttonSignUp.getLayoutParams();
             //   paramsSignUp.height = height;
             //   paramsSignUp.width = width;
             //   buttonSignUp.requestLayout();

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    editText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    editText.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        TextView textViewClickHere = (TextView) findViewById(R.id.link_click_here);
        /** Setting the values to Views */
        SpannableString content = new SpannableString(getResources().getString(R.string.label_click_here));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textViewClickHere.setText(content);

        textViewClickHere.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WebApplicationActivity.class);
                intent.putExtra("key_url", CustomConfiguration.URL_RECOVERY);
                startActivity(intent);
            }
        });

    }

    public void callLoginService(final View v, final String userName, final String password) {
        showLoading(v);

        final DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displaymetrics);

        WebServicesClient.getInstance().loginPlattform(getApplicationContext(), userName, password,
                HubManagerHelper.getInstance().getDeviceId(), (int) (displaymetrics.widthPixels / displaymetrics.density), (int) (displaymetrics.heightPixels / displaymetrics.density), new WSRequestHandler() {
                    @Override
                    public void requestFinish(Object result, boolean error, int statusCode) {
                        stopLoading(v);
                        ((EditText) findViewById(R.id.edit_text_user_mail)).setError(null);
                        ((EditText) findViewById(R.id.edit_text_passwod)).setError(null);
                        EventLogger.logMessage(getClass(), "Status Code: " + statusCode);

                        if (!error) {
                            Login login = (Login) result;

                            if (login == null || !login.isSuccess()) {
                                ((EditText) findViewById(R.id.edit_text_user_mail)).setError(getResources().getString(
                                        R.string.label_error_login));
                                ((EditText) findViewById(R.id.edit_text_passwod)).setError(getResources().getString(
                                        R.string.label_error_login));
                                showError(findViewById(R.id.root_view));
                            } else if (login.getVersion() == null || !login.getVersion().startsWith(getString(R.string.required_module_version))) {

                                // invalid OutSystems Now modules in the server
                                ((EditText) findViewById(R.id.edit_text_user_mail)).setError(getResources().getString(
                                        R.string.label_invalid_version));
                                ((EditText) findViewById(R.id.edit_text_passwod)).setError(getResources().getString(
                                        R.string.label_invalid_version));
                                showError(findViewById(R.id.root_view));
                            } else {

                                /**
                                 * Case 2 - Open always Web Container 
                                 *
                                 * Call method openWebApplicationActivity(login);
                                 */

                                // Using authentication in the web view
                                WebView webView = new WebView(getApplicationContext());
                                String url = String.format(WebServicesClient.BASE_URL,
                                        HubManagerHelper.getInstance().getApplicationHosted()).concat(
                                        "login" + WebServicesClient.getApplicationServer());
                                url = url.replace("https", "http");

                                String postData = "username=" + userName + "&password=" + password + "&screenWidth=" +
                                        (int) (displaymetrics.widthPixels / displaymetrics.density) + "&screenHeight=" +
                                        (int) (displaymetrics.heightPixels / displaymetrics.density) + "&devicetype=android&deviceHwId=" +
                                        Installation.id(getApplicationContext());

                                webView.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));

                                DatabaseHandler database = new DatabaseHandler(getApplicationContext());
                                database.updateHubApplicationCredentials(HubManagerHelper.getInstance()
                                        .getApplicationHosted(), userName, password);
                             //   if (login.getApplications() != null && login.getApplications().size() == 1) {
                                    openWebApplicationActivity(login);
                               // } else {
                                 //   openApplicationsActivity(login);
                               // }
                            }
                        } else {
                            ((EditText) findViewById(R.id.edit_text_user_mail)).setError(WebServicesClient.PrettyErrorMessage(statusCode)); // getResources().getString(R.string.label_error_login)                            
                            showError(findViewById(R.id.root_view));
                        }
                    }
                });

    }

    /**
     * Open applications activity.
     *
     * @param login the login
     */
    @SuppressWarnings("unchecked")
    private void openApplicationsActivity(Login login) {
        Intent intent = new Intent(getApplicationContext(), ApplicationsActivity.class);
        ArrayList arrayList = (ArrayList) login.getApplications();
        intent.putParcelableArrayListExtra(ApplicationsActivity.KEY_CONTENT_APPLICATIONS,
                (ArrayList<? extends Parcelable>) arrayList);
        intent.putExtra(ApplicationsActivity.KEY_TITLE_ACTION_BAR, getResources().getString(R.string.label_logout));
        startActivity(intent);
    }

    /**
     * Open web application activity.
     *
     * @param login the login
     */
    private void openWebApplicationActivity(Login login) {
        Intent intent = new Intent(getApplicationContext(), WebApplicationActivity.class);
        Application application = login.getApplications().get(0);
        application.setPath(CustomConfiguration.URL_APPLICATION);
        if (application != null) {
            intent.putExtra(WebApplicationActivity.KEY_APPLICATION, application);
        }
        clearInputs();
        startActivity(intent);
    }

    /**
     * Open web application activity and auto login facebook
     * @param username
     * @param token facebook's token
     */
    private void openFacebookWebApplicationActivity(String username, String token) {
        String fbUrl = CustomConfiguration.LOGIN_FACEBOOK;
        fbUrl += "?Token=" + token + "&Username=" + username;
        openFacebookWebApplicationActivity(fbUrl);
    }

    /**
     * Open web application activity and present facebook login page
     * */
    private void openFacebookWebApplicationActivity(String fbUrl) {
        Intent intent = new Intent(getApplicationContext(), WebApplicationActivity.class);
        intent.putExtra("key_url", fbUrl);
        startActivity(intent);
    }

    private void clearInputs(){
        ((EditText) findViewById(R.id.edit_text_user_mail)).getText().clear();
        ((EditText) findViewById(R.id.edit_text_passwod)).getText().clear();
    }
}
