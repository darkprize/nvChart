package andcom.nvchart;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by csy on 2018-03-27.
 */

public class SendData {
    static JSONObject jsonObject;
    private SendData(){
    }

    public class SocketException extends Exception {

        public SocketException(String s) {
            super(s);
        }

        public SocketException(String message, Throwable cause) {
            super(message, cause);
        }

        public SocketException(Throwable cause) {
            super(cause);
        }

    }


    public static JSONObject getMessage(Context context, String msg) {
        try{
            Log.w("SendData","SendData msg = "+msg);
            jsonObject = new JSONObject();
            jsonObject.put("Code","8");
            jsonObject.put("Msg","실행중 에러가 발생했습니다.");
        }catch(JSONException je){
            je.getStackTrace();
        }
        try{
            String ip = Prefer.getPrefString("key_ip","");

            int port = Integer.parseInt(Prefer.getPrefString("key_port","80"));

            ASyncTextSocket socket = new ASyncTextSocket((Activity)context,ip,port);
            String result = socket.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,msg).get();
            if(result == null){
                jsonObject = new JSONObject();
                jsonObject.put("code","8");
                jsonObject.put("Msg","서버에 접속할 수 없습니다.");
            }else{
                jsonObject = new JSONObject(result);
            }

            Log.e("sendData","try" + result );

        }/*catch (ExecutionException ee){
            Log.e("Socket Error","ExecutionException Error");
            ee.getStackTrace();
        }catch (InterruptedException ie){
            Log.e("Socket Error","InterruptedException Error");
            ie.getStackTrace();
        }catch (JSONException je){
            Log.e("Socket Error","JSONException Error");
            je.getStackTrace();
        }*/catch(Exception e){
            e.printStackTrace();
            try{
                jsonObject.put("Msg","msg;"+e.getLocalizedMessage());

            }catch (JSONException je){

            }
        }

        return jsonObject;
    }
}
