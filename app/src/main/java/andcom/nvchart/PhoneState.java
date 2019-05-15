package andcom.nvchart;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by csy on 2017-06-14.
 */

public class PhoneState {
    static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE=2525;
    public static String getPhoneNumber(Context context){
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber ="";
        checkPermission();
        try{
            if(telephonyManager.getLine1Number() !=null){
                phoneNumber = telephonyManager.getLine1Number();

            }else{
                if(telephonyManager.getSimSerialNumber()!=null){
                    phoneNumber = telephonyManager.getSimSerialNumber();
                }
            }
        }catch (Exception e ){
            Log.d("error phone","error occur while get phone number");

            e.getStackTrace();
        }
        //String regEx = "(\\d{3})(\\d{3,4})(\\d{s4})";
        //if(!Pattern.matches(regEx, phoneNumber)) return null;
        if(phoneNumber.equals("")){
            phoneNumber = setUUID();
        }
        //phoneNumber = phoneNumber.replaceAll(regEx, "$1-$2-$3");
        phoneNumber = phoneNumber.replace("-", "").replace("+82", "0").replace("+1","");
        return phoneNumber;
    }
    private static String setUUID(){

        String uuid = Settings.Secure.getString(MainActivity.context.getContentResolver(),Settings.Secure.ANDROID_ID);
        if(uuid.equals("")){
            return "알수없음";
        }
        if(uuid.length()>=11){
            uuid = uuid.substring(uuid.length()-11,uuid.length());
            uuid = uuid.replace("a","0")
                    .replace("b","1")
                    .replace("c","2")
                    .replace("d","3")
                    .replace("e","4")
                    .replace("f","5")
                    .replace("g","6")
                    .replace("h","7")
                    .replace("i","8")
                    .replace("j","9")
                    .replace("k","0")
                    .replace("l","1")
                    .replace("m","2")
                    .replace("n","3")
                    .replace("o","4")
                    .replace("p","5")
                    .replace("q","6")
                    .replace("r","7")
                    .replace("s","8")
                    .replace("t","9")
                    .replace("u","0")
                    .replace("v","1")
                    .replace("w","2")
                    .replace("x","3")
                    .replace("y","4")
                    .replace("z","5");
        }else{
            uuid += uuid;
            setUUID();
        }

        Log.e("uuid","uuid is " +uuid);

        return uuid;
    }

    public static void checkPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.context, android.Manifest.permission.READ_PHONE_STATE);

        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            // 권한 없음
            ActivityCompat.requestPermissions((Activity)MainActivity.context,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }else{
            // 권한 있음


        }

    }
}
