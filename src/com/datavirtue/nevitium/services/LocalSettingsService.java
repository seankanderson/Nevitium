/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.datavirtue.nevitium.services;

import com.formdev.flatlaf.util.StringUtils;
import com.google.gson.Gson;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import com.datavirtue.nevitium.models.settings.LocalAppSettings;

/**
 *
 * @author SeanAnderson
 */
public class LocalSettingsService {

    private static final String APP_NODE = "com/datavirtue/nevitium";
    private static final String USER_SETTINGS_KEY = "LocalUserSettings";
    private static final String DEFAULT_VALUE = "";
    public static final String DEFAULT_DATA_PATH = "~/nevitium/nevitium_database";
    public static final String DEFAULT_CONNECTION_STRING = "jdbc:h2:" + DEFAULT_DATA_PATH + ";AUTO_SERVER=TRUE";
    
    public static final String ARC_ORANGE_THEME = "ArcOrange";
    public static final String PURPLE_DARK_THEME = "DarkPurple";
    public static final String HIGH_CONTRAST_THEME = "HighContrast";
    
    public static final String[] THEME_NAMES = {ARC_ORANGE_THEME, PURPLE_DARK_THEME, HIGH_CONTRAST_THEME};
    public static final String DEFAULT_THEME = THEME_NAMES[0]; 
    
    public LocalSettingsService() {

    }

    public static LocalAppSettings getLocalAppSettings() throws BackingStoreException {
        Preferences prefs = Preferences.userRoot().node(APP_NODE);
        var settings = prefs.get(USER_SETTINGS_KEY, DEFAULT_VALUE);
        if (StringUtils.isEmpty(settings)) {
            return null;
        }
        return new Gson().fromJson(settings, LocalAppSettings.class);
    }

    public static LocalAppSettings createDefaultLocalAppSettings() {
        var userConfig = new LocalAppSettings();
        userConfig.setConnectionString(DEFAULT_CONNECTION_STRING);
        userConfig.setDataPath(DEFAULT_DATA_PATH);
        return userConfig;
    }
    
    public static void saveLocalAppSettings(LocalAppSettings settings) throws BackingStoreException {
        Preferences prefs = Preferences.userRoot().node(APP_NODE);
        prefs.put(USER_SETTINGS_KEY, new Gson().toJson(settings));
        prefs.flush();
    }

    public static void removeLocalAppSettings() throws BackingStoreException {
        Preferences prefs = Preferences.userRoot().node(APP_NODE);
        prefs.remove(USER_SETTINGS_KEY);
        prefs.removeNode();
        prefs.flush();
    }

}
