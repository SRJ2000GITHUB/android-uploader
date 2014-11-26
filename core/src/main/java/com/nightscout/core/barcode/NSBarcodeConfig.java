package com.nightscout.core.barcode;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.nightscout.core.preferences.NightscoutPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A class to manage barcode configuration of the uploader
 */
public class NSBarcodeConfig {
    protected static final Logger log = LoggerFactory.getLogger(NSBarcodeConfig.class);
    private JSONObject config;
    private NightscoutPreferences prefs;

    public NSBarcodeConfig(String decodeResults, NightscoutPreferences prefs) {
        this.prefs = prefs;
        configureBarcode(decodeResults);
    }

    public void configureBarcode(String jsonConfig){
        if (jsonConfig == null){
            return;
        }
        try {
            this.config = new JSONObject(jsonConfig);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid configuration from barcode: "+jsonConfig);
        }
    }

    public Optional<String> getMongoUri() {
        String mongoUri = null;
        try {
            if (config.has(NSBarcodeConfigKeys.MONGO_CONFIG) &&
                    config.getJSONObject(NSBarcodeConfigKeys.MONGO_CONFIG).has(NSBarcodeConfigKeys.MONGO_URI)) {
                mongoUri = config.getJSONObject(NSBarcodeConfigKeys.MONGO_CONFIG).getString(NSBarcodeConfigKeys.MONGO_URI);
            } else {
                return Optional.absent();
            }
        } catch (JSONException e) {
            return Optional.absent();
        }
        return Optional.of(mongoUri);
    }

    public List<String> getApiUris() {
        List<String> apiUris = Lists.newArrayList();
        if (config.has(NSBarcodeConfigKeys.API_CONFIG)) {
            JSONArray childNode = null;
            try {
                childNode = config.getJSONArray(NSBarcodeConfigKeys.API_CONFIG);
            } catch (JSONException e) {
                log.error("Invalid json array: "+ config.toString());
                return apiUris;
            }
            for (int index = 0; index < childNode.length(); index++) {
                try {
                    apiUris.add(childNode.getJSONObject(index).getString(NSBarcodeConfigKeys.API_URI));
                } catch (JSONException e) {
                    log.error("Invalid child json object: "+ config.toString());
                }
            }
        }
        return apiUris;
    }

    public String getMongoCollection(){
        if (! config.has(NSBarcodeConfigKeys.MONGO_CONFIG)) {
            return null;
        }
        String mongoCollection = prefs.getDefaultMongoCollection();
        try {
            if (config.has(NSBarcodeConfigKeys.MONGO_CONFIG) &&
                    config.getJSONObject(NSBarcodeConfigKeys.MONGO_CONFIG).has(NSBarcodeConfigKeys.MONGO_COLLECTION)) {
                mongoCollection = config.getJSONObject(NSBarcodeConfigKeys.MONGO_CONFIG).getString(NSBarcodeConfigKeys.MONGO_COLLECTION);
            }
        } catch (JSONException e) {
            return mongoCollection;
        }
        return mongoCollection;
    }

    public String getMongoDeviceStatusCollection(){
        if (! config.has(NSBarcodeConfigKeys.MONGO_CONFIG)) {
            return null;
        }
        String deviceStatusCollection = prefs.getDefaultMongoDeviceStatusCollection();
        try {
            if (config.has(NSBarcodeConfigKeys.MONGO_CONFIG) &&
                    config.getJSONObject(NSBarcodeConfigKeys.MONGO_CONFIG).has(NSBarcodeConfigKeys.MONGO_COLLECTION)) {
                deviceStatusCollection = config.getJSONObject(NSBarcodeConfigKeys.MONGO_CONFIG).getString(NSBarcodeConfigKeys.MONGO_DEVICE_STATUS_COLLECTION);
            }
        } catch (JSONException e) {
            return deviceStatusCollection;
        }
        return deviceStatusCollection;
    }

    public boolean hasMongoConfig(){
        return config.has(NSBarcodeConfigKeys.MONGO_CONFIG);
    }

    public boolean hasApiConfig(){
        return config.has(NSBarcodeConfigKeys.API_CONFIG);
    }
}