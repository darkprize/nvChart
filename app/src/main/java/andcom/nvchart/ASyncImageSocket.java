package andcom.nvchart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

public class ASyncImageSocket extends AsyncTask<byte[],Void,String> {

    public interface AsyncResponse {
        void processFinish(String response);
    }
    public AsyncResponse delegate = null;
    /*
    로딩화면 제어 변수
     */
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

    int bMulti = START_AND_END_BOTH;
    public ASyncImageSocket(Activity activity, String ip, int port){
        this.activity = activity;
        this.context = (Context)activity;
        this.ip=ip;
        this.port=port;
    }
    public ASyncImageSocket(Activity activity, String ip, int port,AsyncResponse delegate){
        this.activity = activity;
        this.context = (Context)activity;
        this.ip=ip;
        this.port=port;
        this.delegate = delegate;
    }
    public ASyncImageSocket(Activity activity, int bMulti){
        this.activity = activity;
        this.bMulti = bMulti;

    }
    @Override
    public void onPreExecute() {
        super.onPreExecute();
        Log.i(logcat,"onPreExecute");

    }

    @Override
    public String doInBackground(byte[]... bytes) {
        Log.i(logcat,"doInBackground");
        socket = new Socket();
        try{
            try {
                for(InetAddress address : InetAddress.getAllByName(ip)){
                    Log.i("address",address.getHostName());
                }
            }catch (UnknownHostException uhe){
                uhe.printStackTrace();
                Log.e("sendData",uhe.getMessage());
            }
            socket.connect(new InetSocketAddress(ip,port),5000);
            socket.setTcpNoDelay(true);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());


            byte[] byteArray = bytes[0];
            InputStream inn = new ByteArrayInputStream(byteArray);

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            int len1 = 0 ;
            int totalLen = 0;
            byte [] b = new byte [1024];
            while ((len1 = inn.read(b)) != -1)
            {
                Log.w("SendByte","len : " + len1 + " , string: "+new String(b));
                dos.write(b,0,len1);
                totalLen += len1;
            }

            Log.e("totalLen","totalLen : " + totalLen );
            dos.flush();
            inn.close();

            String msg = new String();

            int len;

            byte[] buffer = new byte[1024];
            while((len = bufferedInputStream.read(buffer)) != -1){
                Log.e("socket","recieve "+len);

                String packet = new String(buffer);

                result += packet;
                Log.e("saveChart socket","result " + result);
                if(indexOf(buffer,"---AndcomData_END---".getBytes(),0)>=0){
                    Log.e("break1","socket end");
                    break;
                }

            }

            socket.close();

        }catch(IOException e){
            e.getStackTrace();
            e.printStackTrace();
            //progressOFF();
            result=null;
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
        /*loadingProgress.progressOFF();*/
        delegate.processFinish(s);
        LoadingProgress.getInstance().progressOFF();

    }

}
