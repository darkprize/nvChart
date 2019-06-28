package andcom.nvchart.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import andcom.nvchart.MakeJSONData;
import andcom.nvchart.MsgType;
import andcom.nvchart.Prefer;
import andcom.nvchart.R;
import andcom.nvchart.SendData;
import es.dmoral.toasty.Toasty;

public class NodeRefreshDialog extends DialogFragment {
    final String BACK_IMAGE_START = "<---AndcomData_BackImage---\r\n";
    final String BACK_IMAGE_END = "\n---AndcomData_BackImage--->";

    public NodeRefreshDialog(){}
    View view;
    Context context;
    ImageView loading;
    TextView notify;
    RecyclerView recyclerView;
    Button btnConfirm;
    HashMap<String,Integer> nodeList;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //view = inflater.inflate(R.layout.node_refresh_dialog, container);
        view = getActivity().getLayoutInflater().inflate(R.layout.node_refresh_dialog, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getContext();
        getDialog().getWindow().setLayout(1000,1500);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //getDialog().getWindow().setLayout(getActivity().getWindow().getAttributes().width,view.getMeasuredHeight());
                //Log.e("height","width " +getActivity().getWindow().getAttributes().width +" height " + view.getMeasuredHeight());
                getActivity().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                initView();

            }
        });

    }
    private void initView(){
        loading = view.findViewById(R.id.loading_nodelist);
        notify = view.findViewById(R.id.notify);
        recyclerView = view.findViewById(R.id.recyclerViewNode);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshNode(recyclerView);
            }
        });
        nodeList = new HashMap<>();
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                NodeListRecyclerAdapter.ItemViewHolder holder = (NodeListRecyclerAdapter.ItemViewHolder)recyclerView.getChildViewHolder(v);
                holder.check();
                nodeList.put(holder.getNode(),holder.isChecked()?1:0);
            }
        });
        LoadNodeList loadNodeList = new LoadNodeList();
        loadNodeList.execute();
    }
    public void refreshNode(RecyclerView recyclerView){
        NodeListRecyclerAdapter adapter = (NodeListRecyclerAdapter)recyclerView.getAdapter();

        NodeRefreshASync nodeRefreshASync = new NodeRefreshASync();
        nodeRefreshASync.execute(nodeList);

    }

    public class NodeRefreshASync extends AsyncTask<HashMap<String,Integer>,String,Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(HashMap<String,Integer>... hashMaps) {

            for(String key : hashMaps[0].keySet()){

                int value = hashMaps[0].get(key);

                if(value==1){
                    Log.e("nodeList",key);
                    String[] cSend = key.split(":");
                    String cFileName = key.replace(":","")+".jpg";
                    File fNode = new File(context.getFilesDir()+File.separator+cFileName);
                    if(!fNode.canRead()){
                        try {
                            fNode.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Socket socket = new Socket();
                        String ip = Prefer.getPrefString("key_ip","");
                        int port = Integer.parseInt(Prefer.getPrefString("key_port","80"));
                        socket.connect(new InetSocketAddress(ip,port),3000);
                        if(!socket.isConnected()){
                            return false;
                        }
                        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"EUC-KR")),true);
                        printWriter.println(MakeJSONData.get(MsgType.LOAD_NVCHART,cSend[0],"",cSend[1],"0"));
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());

                        int len;
                        byte[] buffer = new byte[5124];
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        while((len = bufferedInputStream.read(buffer)) != -1){
                            bos.write(buffer,0,len);

                            if(indexOf(buffer,"---AndcomData_END---".getBytes(),0)>=0){
                                Log.e("break1","socket end");
                                break;
                            }

                        }
                        socket.close();

                        byte[] data = bos.toByteArray();
                        bos.close();
                        int start=indexOf(data,BACK_IMAGE_START.getBytes(),0)+BACK_IMAGE_START.getBytes().length;
                        int end=indexOf(data,BACK_IMAGE_END.getBytes(),0);
                        byte[] backImageData = new byte[end-start];
                        System.arraycopy(data,start,backImageData,0,end-start);
                        FileOutputStream fos = new FileOutputStream(fNode);
                        fos.write(backImageData);
                        fos.flush();
                        fos.close();

                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
            return null;
        }
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                Toasty.info(context,"작업 완료").show();
            }else{
                Toasty.error(context,"서버에 접속 할 수 없습니다.").show();
            }
            loading.setVisibility(View.INVISIBLE);

        }

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

    public class LoadNodeList extends AsyncTask<String,Void,Boolean>{
        NodeListRecyclerAdapter adapter;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getDialog().getWindow().setLayout(getActivity().getWindow().getAttributes().width,view.getHeight());

            AnimationDrawable drawable = (AnimationDrawable)loading.getDrawable();
            drawable.start();

            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                JSONObject msg = MakeJSONData.get(MsgType.LOAD_DBLIST);
                JSONObject rcvData= SendData.getMessage(context,msg.toString());
                String db = rcvData.getString("DB");
                if(db==null){
                    return false;
                }
                db = db.trim();
                String[] dbs = db.split("");
                ArrayList<JSONObject> arrJson = new ArrayList<>();
                for(String item : dbs){
                    if(item.length()>0){
                        msg = MakeJSONData.get(MsgType.LOAD_NVLIST,item,"");
                        rcvData = SendData.getMessage(context,msg.toString());
                        rcvData.put("DB",item);
                        arrJson.add(rcvData);
                    }

                }

                adapter = new NodeListRecyclerAdapter(arrJson);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }


        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                recyclerView.setAdapter(adapter);

                Toasty.info(context,"작업 완료").show();
            }else{
                Toasty.error(context,"서버에 접속 할 수 없습니다.").show();
            }
            loading.setVisibility(View.INVISIBLE);

        }
    }
    @Override
    public void onResume() {
        super.onResume();

    }

}
