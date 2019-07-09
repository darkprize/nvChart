package andcom.nvchart.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import andcom.nvchart.Prefer;

public class SocketCheck {
    public static boolean connectionCheck(){

        try {
            SocketCheckASync socketCheckASync = new SocketCheckASync();
            return socketCheckASync.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;

        }
    }

    public static class SocketCheckASync extends AsyncTask<Void,Void,Boolean>{
        Socket socket;

        String ip;
        int port;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            socket = new Socket();

            ip = Prefer.getPrefString("key_ip","");
            port = Integer.parseInt(Prefer.getPrefString("key_port","80"));
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                InetSocketAddress address = new InetSocketAddress(ip,port);
                socket.connect(address,3000);
                boolean result =  socket.isConnected();
                if(result){
                    socket.close();
                }
                Log.e("socketCheck","socket result " + result);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

    }
}
