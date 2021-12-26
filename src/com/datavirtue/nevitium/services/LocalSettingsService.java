package com.datavirtue.nevitium.services;

import com.formdev.flatlaf.util.StringUtils;
import com.google.gson.Gson;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import com.datavirtue.nevitium.models.settings.LocalAppSettings;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import javax.swing.UnsupportedLookAndFeelException;

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

    public static void setLookAndFeel() throws BackingStoreException {
        // https://www.formdev.com/flatlaf/themes/#intellij_themes_pack
        //      Documentation on how to create custom .json themes
        // https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-intellij-themes
        //      Documentation for LeF themes...just new-up the class you want

        if (LocalSettingsService.getLocalAppSettings() == null) {
            try {
                javax.swing.UIManager.setLookAndFeel(new FlatArcOrangeIJTheme());
            } catch (UnsupportedLookAndFeelException ex) {
                
            }
            return;
        }

        var theme = LocalSettingsService.getLocalAppSettings().getTheme();

        try {
            if (theme.equals(LocalSettingsService.ARC_ORANGE_THEME)) {
                javax.swing.UIManager.setLookAndFeel(new FlatArcOrangeIJTheme());
            } else if (theme.equals(LocalSettingsService.PURPLE_DARK_THEME)) {
                //javax.swing.UIManager.setLookAndFeel(new FlatGitHubDarkIJTheme());
                javax.swing.UIManager.setLookAndFeel(new FlatDarkPurpleIJTheme());
                //javax.swing.UIManager.setLookAndFeel(new FlatGradiantoDarkFuchsiaIJTheme());
            } else if (theme.equals(LocalSettingsService.HIGH_CONTRAST_THEME)) {
                javax.swing.UIManager.setLookAndFeel(new FlatHighContrastIJTheme());
            }
        } catch (UnsupportedLookAndFeelException e) {

        }
        // may not set a look and feel      

    }

}
