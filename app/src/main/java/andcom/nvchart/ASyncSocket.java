package andcom.nvchart;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by csy on 2017-12-26.
 */

public class ASyncSocket extends AsyncTask<String,Void,byte[]> {
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

    private byte[] data;

    //
    //LoadingProgress loadingProgress;

    int bMulti = START_AND_END_BOTH;
    public ASyncSocket(Activity activity, String ip, int port){
        this.activity = activity;
        this.context = (Context)activity;
        this.ip=ip;
        this.port=port;
    }
    public ASyncSocket(Activity activity, int bMulti){
        this.activity = activity;
        this.bMulti = bMulti;

    }
    @Override
    public void onPreExecute() {
        super.onPreExecute();
        Log.i(logcat,"onPreExecute");

        //SharedPreferences prefer = PreferenceManager.getDefaultSharedPreferences(activity);
        //ip = prefer.getString("key_ip","");
        //port = Integer.parseInt(prefer.getString("key_port","80"));

        //Log.i(logcat,"ip : " + MainActivity.CONNECT_IP + ", port : "+MainActivity.CONNECT_PORT);
        //ip ="192.168.10.186";
        //port = 1235;
        //if(bMulti!=2 && bMulti!=3)

        //loadingProgress = LoadingProgress.getInstance();
        //loadingProgress.progressON(activity,"불러오는 중");
    }

    @Override
    public byte[] doInBackground(String... params) {
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
            socket.connect(new InetSocketAddress(ip,port),5000);
            socket.setTcpNoDelay(true);

            printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"EUC-KR"),true);
            //networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"EUC-KR"));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"EUC_KR"));
            long dataTimeStart = System.currentTimeMillis();
            printWriter.println(params[0]);
            long waitConEnd =System.currentTimeMillis();
            Log.e("waitCon",String.format("%.4f",(waitConEnd-waitConStart)/1000.0000f));

            String msg = new String();

            int len;
            byte[] buffer = new byte[5124];

            File file = new File(context.getFilesDir()+ File.separator+"stroke1");
            if(!file.exists())
                file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            while((len = bufferedInputStream.read(buffer)) != -1){
                Log.e("socket","recieve "+len);
                fos.write(buffer,0,len);
                bos.write(buffer,0,len);

                String packet = new String(buffer);
                //result += packet;

                long searchStart = System.currentTimeMillis();
                if(indexOf(buffer,"---AndcomData_END---".getBytes(),0)>=0){
                    Log.e("break1","socket end");
                    break;
                }
                long searchEnd = System.currentTimeMillis();

                Log.e("packetSearchTime","="+String.format("%.4f",(searchEnd-searchStart)/1000.0000f) );


                /*
                if(packet.contains("---AndcomData_END---"))
                {
                    Log.e("break2","socket end");
                    break;
                }*/

            }
            long dataTimeEnd = System.currentTimeMillis();
            Log.e("packetTransferTime1","="+ String.format("%.4f",(dataTimeEnd-dataTimeStart)/1000.0000f));



            data = bos.toByteArray() ;
            bos.flush();
            bos.close();

            fos.close();
            fos.flush();
            socket.close();
            Log.e("packetTransferTime2","="+ String.format("%.4f",(System.currentTimeMillis()-dataTimeStart)/1000.0000f));
/*
            Log.e("socketResult","="+result);
            FileInputStream fis = new FileInputStream(file);
            byte[] image = new byte[(int)file.length()];
            fis.read(image);
            fis.close();*/




        }catch(IOException e){
            e.getStackTrace();
            e.printStackTrace();
            //progressOFF();
            result=null;
        }
        Log.w("AsyncSocket","result = " + result);

        return data;
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
    protected void onPostExecute(byte[] s) {
        super.onPostExecute(s);
        Log.d(logcat,"onPostExecute");
        /*loadingProgress.progressOFF();*/

    }

}
