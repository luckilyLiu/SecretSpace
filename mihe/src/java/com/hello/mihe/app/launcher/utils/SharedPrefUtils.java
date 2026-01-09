package com.hello.mihe.app.launcher.utils;

import android.content.Context;

import app.lawnchair.LawnchairApp;


/** 使用默认文件存储k-v对 若有大量数据，请使用新文件 */
public class SharedPrefUtils {

    private static final String PREF_APP = "pref_app";
    private static final String PREF_STABLE = "pref_app_stable";
    private static final String PREF_APP_MULTI = "pref_app_multi";

    /**
     * Gets boolean data.
     *
     * @param context the context
     * @param key     the key
     * @return the boolean data
     */
    public static boolean getBooleanData(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getBoolean(key, false);
    }

    public static boolean getBooleanWithDefault(Context context, String key, boolean def) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getBoolean(key, def);
    }

    /**
     * Gets int data.
     *
     * @param context the context
     * @param key     the key
     * @return the int data
     */
    public static int getIntData(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getInt(key, 0);
    }

    public static int getIntDataWithDefault(Context context, String key, int defValue) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getInt(key, defValue);
    }

    /**
     * Gets string data.
     *
     * @param context the context
     * @param key     the key
     * @return the string data
     */
    // Get Data
    public static String getStringData(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getString(key, "");
    }

    public static String getStringDataWithDefaultValue(Context context, String key,
            String defValue) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
                .getString(key, defValue);
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    // Save Data
    public static void saveData(Context context, String key, String val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putString(key, val)
                .apply();
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    public static void saveData(Context context, String key, int val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putInt(key, val)
                .apply();
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    public static void saveData(Context context, String key, boolean val) {
        context
                .getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, val)
                .apply();
    }

    public static void saveData(Context context, String key, long val) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putLong(key, val)
                .apply();
    }

    public static long getLongData(Context context, String key) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getLong(key, 0L);
    }

    public static void clear() {
        LawnchairApp.getInstance().getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().clear().commit();
    }

    /**
     * 注销也不会清除的数据
     *
     * @param context
     * @param key
     * @param val
     */
    public static void saveStableData(Context context, String key, String val) {
        context
                .getSharedPreferences(PREF_STABLE, Context.MODE_PRIVATE)
                .edit()
                .putString(key, val)
                .apply();
    }

    public static String getStableStringData(Context context, String key) {
        return context.getSharedPreferences(PREF_STABLE, Context.MODE_PRIVATE).getString(key, "");
    }

    public static int getStableIntData(Context context, String key) {
        return context.getSharedPreferences(PREF_STABLE, Context.MODE_PRIVATE).getInt(key, 0);
    }

    public static void saveStableData(Context context, String key, int val) {
        context.getSharedPreferences(PREF_STABLE, Context.MODE_PRIVATE).edit().putInt(key, val)
                .apply();
    }

    public static boolean getStableBooleanData(Context context, String key) {
        return context.getSharedPreferences(PREF_STABLE, Context.MODE_PRIVATE)
                .getBoolean(key, false);
    }


    public static boolean getStableBooleanData(Context context, String key,boolean defaultValue) {
        return context.getSharedPreferences(PREF_STABLE, Context.MODE_PRIVATE)
                .getBoolean(key, defaultValue);
    }

    public static void saveStableData(Context context, String key, boolean val) {
        context
                .getSharedPreferences(PREF_STABLE, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(key, val)
                .apply();
    }

    public static long getStableLongData(Context context, String key) {
        return context.getSharedPreferences(PREF_STABLE, Context.MODE_PRIVATE).getLong(key, 0);
    }

    public static void saveStableData(Context context, String key, long val) {
        context
                .getSharedPreferences(PREF_STABLE, Context.MODE_PRIVATE)
                .edit()
                .putLong(key, val)
                .apply();
    }

    public static void clearKey(Context context, String key) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().remove(key).apply();
    }


}
