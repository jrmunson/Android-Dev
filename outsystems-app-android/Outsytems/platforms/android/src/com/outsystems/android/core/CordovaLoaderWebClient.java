/*
 * OutSystems Project
 * 
 * Copyright (C) 2014 OutSystems.
 * 
 * This software is proprietary.
 */
package com.outsystems.android.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.IceCreamCordovaWebViewClient;


import android.content.res.AssetManager;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;


/**
 * Class description.
 *
 * @author <a href="mailto:vmfo@xpand-it.com">vmfo</a>
 * @version $Revision: 666 $
 */
public class CordovaLoaderWebClient extends IceCreamCordovaWebViewClient {

    /**
     * The identifier cordova.
     */
    private static String IDENTIFIER_CORDOVA = "/cdvload/";

    /**
     * The identifier images.
     */
    private static String IDENTIFIER_IMAGES = "/img/";

    /**
     * The identifier fonts.
     */
    private static String IDENTIFIER_FONTS = "/fonts/";

    /**
     * The mngr.
     */
    private AssetManager mngr;

    /**
     * @param cordova
     * @param view
     */
    public CordovaLoaderWebClient(CordovaInterface cordova, CordovaWebView view) {
        super(cordova, view);
        mngr = cordova.getActivity().getAssets();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.webkit.WebViewClient#shouldInterceptRequest(android.webkit.WebView, java.lang.String)
     */
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        String path = "";
        try {
            if (url.contains(IDENTIFIER_IMAGES)) {
                path = pathToResource(url, IDENTIFIER_IMAGES);
            } else if (url.contains(IDENTIFIER_CORDOVA)) {
                path = pathToResource(url, IDENTIFIER_CORDOVA);
            } else if (url.contains(IDENTIFIER_FONTS)) {
                path = pathToResource(url, IDENTIFIER_FONTS);

                InputStream stream = mngr.open("www/" + path);
                WebResourceResponse response = new WebResourceResponse("*/*", "UTF-8", stream);
                return response;
            }

            InputStream stream = mngr.open("www/" + path);
            WebResourceResponse response = new WebResourceResponse("text/javascript", "UTF-8", stream);
            return response;

        } catch (IOException e) {
            //EventLogger.logError(getClass(), e);
        }
        return null;
    }

    private String pathToResource(String url, String identifier) throws MalformedURLException {
        URL url1 = new URL(url);

        String resourceName = url1.getPath();

        String[] split = resourceName.split(identifier);
        String path = "";
        if (split.length > 1) {
            path = split[1];
        }

        if (identifier.equals(IDENTIFIER_IMAGES)) {
            path = "img/" + path;
        } else if (identifier.equals(IDENTIFIER_FONTS)) {
            path = "fonts/" + path;
        }
        return path;
    }
}
