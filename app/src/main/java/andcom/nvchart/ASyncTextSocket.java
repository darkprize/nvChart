package andcom.nvchart;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import andcom.nvchart.util.LoadingProgress;

/**
 * Created by csy on 2017-12-26.
 */

public class ASyncTextSocket extends AsyncTask<String,Void,String> {
    /*
    로딩화면 제어 변수
     */

    public interface AsyncResponse{
        void processFinish(String msg);

    }

    AsyncResponse delegate;
    static final int START_AND_END_BOTH = 0;        //디폴트값:시작,종료
    static final int START_ONLY = 1;                //시작만
    static final int NO_OPR = 2;                    //동작없음
    static final int END_ONLY = 3;                  //종료만

    static final String BACK_IMAGE_START = "----AndcomData_BackImage----<";
    static final String BACK_IMAGE_END = "----AndcomData_BackImage---->";

    String logcat = "ASyncSocket";
    String result = new String();

    Socket socket;
    PrintWriter printWriter;
    BufferedReader networkReader;
    DataInputStream dataInputStream;

    String ip;
    int port;

    Activity activity;
    Context context;

    LoadingProgress loadingProgress;

    private byte[] data;

    //
    //LoadingProgress loadingProgress;

    int bMulti = START_AND_END_BOTH;
    public ASyncTextSocket(Activity activity){
        this.activity = activity;
        this.context = (Context)activity;
    }
    public ASyncTextSocket(Activity activity,AsyncResponse delegate){
        this.activity = activity;
        this.context = (Context)activity;
        this.delegate = delegate;
    }
    @Override
    public void onPreExecute() {
        super.onPreExecute();
        Log.i(logcat,"onPreExecute");

        //SharedPreferences prefer = PreferenceManager.getDefaultSharedPreferences(activity);
        ip = Prefer.getPrefString("key_ip","");
        port = Integer.parseInt(Prefer.getPrefString("key_port","80"));

        //Log.i(logcat,"ip : " + MainActivity.CONNECT_IP + ", port : "+MainActivity.CONNECT_PORT);
        //ip ="192.168.10.186";
        //port = 1235;
        //if(bMulti!=2 && bMulti!=3)

        //loadingProgress = LoadingProgress.getInstance();
        //loadingProgress.progressON(activity,"불러오는 중");
    }

    @Override
    public String doInBackground(String... params) {
        Log.i(logcat,"doInBackground");


        long start =System.currentTimeMillis();
        socket = new Socket();
        StringBuilder sb = new StringBuilder();
        try{
            try {
                //ip = InetAddress.getAllByName(ip);
                for(InetAddress address : InetAddress.getAllByName(ip)){
                    Log.i("address",address.getHostName());
                }
            }catch (UnknownHostException uhe){
                uhe.printStackTrace();
                Log.e("sendData",uhe.getMessage());
            }
            long waitConStart =System.currentTimeMillis();
            socket.connect(new InetSocketAddress(ip,port),3000);
            socket.setTcpNoDelay(true);

            //printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"EUC-KR"),true);
            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"EUC-KR")),true);
            //networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"EUC-KR"));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"EUC_KR"));
            long dataTimeStart = System.currentTimeMillis();
            printWriter.println(params[0]);

            long waitConEnd =System.currentTimeMillis();
            Log.e("waitCon",String.format("%.4f",(waitConEnd-waitConStart)/1000.0000f));

            String msg = new String();

            int len;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            while((len = bufferedInputStream.read(buffer)) != -1){

                bos.write(buffer,0,len);

                long searchStart = System.currentTimeMillis();

                Log.e("TextSocketBuffer","TextSocketBuffer : "+new String(buffer));

                if(indexOf(bos.toByteArray(),"---AndcomData_END---".getBytes(),0)>=0){
                    Log.e("break1","socket end");
                    break;
                }
                long searchEnd = System.currentTimeMillis();

                Log.e("packetSearchTime","="+String.format("%.4f",(searchEnd-searchStart)/1000.0000f) );

            }

            byte[] data = bos.toByteArray();
            bos.close();
            result = new String(data);
            long dataTimeEnd = System.currentTimeMillis();
            Log.e("packetTransferTime1","="+ String.format("%.4f",(dataTimeEnd-dataTimeStart)/1000.0000f));

            socket.close();
            Log.e("packetTransferTime2","="+ String.format("%.4f",(System.currentTimeMillis()-dataTimeStart)/1000.0000f));
/*
            Log.e("socketResult","="+result);
            FileInputStream fis = new FileInputStream(file);
            byte[] image = new byte[(int)file.length()];
            fis.read(image);
            fis.close();*/


        result = result.replace("---AndcomData_END---","");

        }catch(IOException e){
            e.getStackTrace();
            e.printStackTrace();
            //progressOFF();
            result=null;
        }
        //Log.w("TextSocket","result = " + result);
        String temp = result;
        int log_index = 1;
        try {
            while (temp.length() > 0) {
                if (temp.length() > 2000) {
                    Log.e("textSocketResult", "json - " + log_index + " : "
                            + temp.substring(0, 2000));
                    temp = temp.substring(2000);
                    log_index++;
                } else {
                    Log.e("textSocketResult", "json - " + log_index + " :" + temp);
                    break;
                }
            }
        } catch (Exception e) {
        }
        return result;
    }
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }
    public int indexOf(byte[] outerArray, byte[] smallerArray,int from) {
        for(int i = from; i < outerArray.length - smallerArray.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i+j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    public ArrayList<Integer> indexOfArray(byte[] outerArray, byte[] smallerArray) {
        ArrayList<Integer> array = new ArrayList<>();
        for(int i = 0; i < outerArray.length - smallerArray.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i+j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) array.add(i);
        }
        return array;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Log.d(logcat,"onProgressUpdate");
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(logcat,"onPostExecute");
        if(delegate != null)
            delegate.processFinish(s);
        //loadingProgress.progressOFF();

    }

}
