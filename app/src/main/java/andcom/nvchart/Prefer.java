package andcom.nvchart;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefer {
    static SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
    static SharedPreferences.Editor editor;

    public static void setPref(String key, boolean value){
        editor = prefs.edit();

        editor.putBoolean(key,value);
        editor.commit();
    }
    public static void setPref(String key, String value){
        editor = prefs.edit();

        editor.putString(key,value);
        editor.commit();
    }
    public static void setPref(String key, int value){
        editor = prefs.edit();

        editor.putInt(key,value);

        editor.commit();
    }
    public static void setPref(String key, float value){
        editor = prefs.edit();

        editor.putFloat(key,value);

        editor.commit();
    }

    public static boolean getPrefBoolean(String key, boolean value){

        return prefs.getBoolean(key,value);
    }
    public static String getPrefString(String key, String value){

        return prefs.getString(key,value);
    }
    public static int getPrefInt(String key, int value){

        return prefs.getInt(key,value);
    }
    public static float getPrefFloat(String key, float value){

        return prefs.getFloat(key,value);
    }
}
