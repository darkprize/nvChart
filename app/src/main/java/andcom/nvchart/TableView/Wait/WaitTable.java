package andcom.nvchart.TableView.Wait;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import andcom.nvchart.ASyncTextSocket;
import andcom.nvchart.MainActivity;
import andcom.nvchart.MakeJSONData;
import andcom.nvchart.MsgType;
import andcom.nvchart.R;
import andcom.nvchart.SendData;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import andcom.nvchart.TableView.WaitingBoardDialog;
import andcom.nvchart.util.ClickItem;
import andcom.nvchart.util.ItemClickSupport;
import es.dmoral.toasty.Toasty;

public class WaitTable extends Fragment  {
    ClickItem clickItem=null;

    private RecyclerView recyclerView;
    private Context context;
    WaitRecyclerAdapter adapter;
    public WaitTable (){
        Log.e("WaitTableConstruct","Load WaitTableFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.content_wait_table, container, false);
        this.context = getContext();


        //initializeTableView(mTableView);

        //hideProgressBar();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.e("wait click","view created");

        initRecyclerView();

        refreshDataAsync(WaitingBoardDialog.getSelectedDate());

    }

    public void setClickItem(ClickItem clickItem){
        this.clickItem = clickItem;
    }
    public void refreshData(String date){
        Log.d("refreshData","re wait");

        JSONObject rcvData = SendData.getMessage(context,MakeJSONData.get(MsgType.LOAD_WAIT,date).toString());
        try{
            Toasty.error(context,rcvData.getString("Msg"),Toasty.LENGTH_LONG).show();
            return;
        }catch (JSONException je2){

        }
        adapter = new WaitRecyclerAdapter(rcvData.toString());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
/*
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.e("wait click","wait clicked");
                Message msg = MainActivity.handler.obtainMessage();
                msg.what = MsgType.LOAD_NVCHART;
                msg.obj = adapter.getChartNo(position);

                MainActivity.handler.sendMessage(msg);
                clickItem.click();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
    }
    public void refreshDataAsync(String date){
        Log.d("refreshData","re wait");

        SendData.getMessageAsync(context, MakeJSONData.get(MsgType.LOAD_WAIT, date).toString(), new ASyncTextSocket.AsyncResponse() {
            @Override
            public void processFinish(String rcvData) {

                adapter = new WaitRecyclerAdapter(rcvData);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
            }
        });



    }
    private void initRecyclerView(){
        recyclerView = (RecyclerView)getView().findViewById(R.id.waitRecyclerView);
        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Log.e("wait click","wait clicked  "+position+" view id =" +view.getId());
                Message msg = MainActivity.handler.obtainMessage();
                msg.what = MsgType.LOAD_NVCHART;
                msg.obj = adapter.getChartNo(position);

                MainActivity.handler.sendMessage(msg);
                clickItem.click();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Log.e("wait click","wait clicked  "+position+" view id =" +v.getId());
                Message msg = MainActivity.handler.obtainMessage();
                msg.what = MsgType.LOAD_NVCHART;
                msg.obj = adapter.getChartNo(position);

                MainActivity.handler.sendMessage(msg);
                clickItem.click();
            }
        });
    }
}
