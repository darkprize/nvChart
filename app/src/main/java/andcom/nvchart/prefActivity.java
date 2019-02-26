package andcom.nvchart;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


/**
 * Created by csy on 2017-04-18.
 */

public class prefActivity extends PreferenceActivity {
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2525;
    public static final int MY_PERMISSIONS_REQUEST_INTERNET = 2524;
    static EditTextPreference prefIp,prefPort;
    Preference prefPhone,prefVersion,prefPublicIp,prefAS;
    static Context context;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*screen = getPreferenceScreen();
        //prefIp = (EditTextPreference)screen.findPreference("key_ip");

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content,
                        new Prefs1Fragment()).commit();*/


        setContentView(R.layout.toolbar);
        context = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_settings);
        addPreferencesFromResource(R.xml.setting);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        prefIp = (EditTextPreference)findPreference("key_ip");

        prefPort =(EditTextPreference)findPreference("key_port");
        prefPhone = (Preference)findPreference("key_phone");
        prefVersion = (Preference)findPreference("key_version");
        prefPublicIp = (Preference)findPreference("key_public_ip");
        prefAS = (Preference)findPreference("key_as");

        checkPermission();

        Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                Prefer.setPref(preference.getKey(),newValue.toString());
                Log.e("값 변화","new value = " + newValue.toString());
                return true;
            }
        };
        prefPhone.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                checkPermission();
                preference.setSummary(Prefer.getPrefString("key_phone",""));
                Log.e("key_phone","click " + Prefer.getPrefString("key_phone",""));
                return true;
            }
        });
        prefVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return false;
            }
        });

        prefAS.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.teamviewer.quicksupport.market")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.teamviewer.quicksupport.market")));
                }
                return false;
            }
        });
        prefIp.setOnPreferenceChangeListener(preferenceChangeListener);
        prefPort.setOnPreferenceChangeListener(preferenceChangeListener);
        prefPhone.setOnPreferenceChangeListener(preferenceChangeListener);
        toolbar.setTitle("설정");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다

                    String uuid = PhoneState.getPhoneNumber(this);

                    Prefer.setPref("key_phone", uuid);


                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                    //handleMessage(TOAST_MESSAGE,"전화번호를 읽어올 수 없습니다. 권한요청을 수락해 주세요.");
                }
                return;
        }
    }

    @Override
    public void onResume(){
        super.onResume();

            /*SharedPreferences prefer = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            SharedPreferences.Editor editor = prefer.edit();
            editor.
            editor.commit();*/




        if(Prefer.getPrefString("key_public_ip","").equals("")){
            prefPublicIp.setSummary("설정되지 않음");
        }else{
            prefPublicIp.setSummary("공인 IP 주소 : "+Prefer.getPrefString("key_public_ip","설정되지 않음"));
        }

        if(Prefer.getPrefString("key_ip","").equals("")){

            prefIp.setSummary("설정되지 않음");
        }else{
            prefIp.setSummary("IP 주소 : "+Prefer.getPrefString("key_ip","설정되지 않음"));
        }

        if(Prefer.getPrefString("key_port","").equals("")){
            prefPort.setSummary("설정되지 않음");
        }else{
            prefPort.setSummary("포트 번호 : " + Prefer.getPrefString("key_port","설정되지 않음"));
        }

        if(Prefer.getPrefString("key_phone","").equals("")){
            prefPhone.setSummary("설정되지 않음. 전화 사용 권한을 [허용]해주세요");

        }else{
            prefPhone.setSummary("인증번호 : "+Prefer.getPrefString("key_phone","설정되지 않음"));
        }

        try{
            prefVersion.setSummary("설치된 버전:"+getPackageManager().getPackageInfo(getPackageName(),0).versionName +"\n최신 버전:"+MainActivity.store_version);
        }catch (Exception e){
            for(int i = 0;i<e.getStackTrace().length;i++){
                Log.e("market Test" , "error occur : "+e.getStackTrace()[i].toString());
            }
        }

    }

    public void checkPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            // 권한 없음
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }else{
            // 권한 있음

            String uuid = PhoneState.getPhoneNumber(this);

            Prefer.setPref("key_phone", uuid);


            Log.e("key_phone","checkPer " + Prefer.getPrefString("key_phone",""));
        }
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            // 권한 없음
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        }else{
            // 권한 있음
        }


    }




}
