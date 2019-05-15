package andcom.nvchart.TableView.Order;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.evrencoskun.tableview.TableView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import andcom.nvchart.MainActivity;
import andcom.nvchart.MakeJSONData;
import andcom.nvchart.MsgType;
import andcom.nvchart.R;
import andcom.nvchart.RecyclerTouchListener;
import andcom.nvchart.SendData;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;


public class OrderTable extends Fragment implements MainActivity.Refresh {

    private RecyclerView recyclerViewCall,recyclerViewOrder1,recyclerViewOrder2,recyclerViewOrder3,recyclerViewOrder4;
    private OrderRecyclerAdapter orderRecyclerAdapter;
    private OrderCashRecyclerAdapter orderCashRecyclerAdapter,orderCashRecyclerAdapter1,orderCashRecyclerAdapter2,orderCashRecyclerAdapter3,orderCashRecyclerAdapter4;
    private LinearLayout layoutCall,layoutCash1,layoutCash2,layoutCash3,layoutCash4;

    private Context context;

    Handler handler;
    public OrderTable() {
        Log.e("OrderTableConstruct","Load OrderTableFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                initRecyclerView();
                return false;
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.content_order_table, container, false);
        this.context = getContext();

        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        initRecyclerView();
        RecyclerViewVisibility();
        refreshData(MainActivity.getSelectedDate());
    }

    private void RecyclerViewVisibility(){

        layoutCall = (LinearLayout)getView().findViewById(R.id.layoutCall);
        layoutCash1 = (LinearLayout)getView().findViewById(R.id.layoutCash1);
        layoutCash2 = (LinearLayout)getView().findViewById(R.id.layoutCash2);
        layoutCash3 = (LinearLayout)getView().findViewById(R.id.layoutCash3);
        layoutCash4 = (LinearLayout)getView().findViewById(R.id.layoutCash4);


        CheckBox call = (CheckBox)getView().findViewById(R.id.chkCall);
        call.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layoutVisibility(layoutCall,isChecked);

            }
        });
        CheckBox cash1 = (CheckBox)getView().findViewById(R.id.chkCash1);
        cash1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layoutVisibility(layoutCash1,isChecked);

            }
        });
        CheckBox cash2 = (CheckBox)getView().findViewById(R.id.chkCash2);
        cash2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layoutVisibility(layoutCash2,isChecked);

            }
        });
        CheckBox cash3 = (CheckBox)getView().findViewById(R.id.chkCash3);
        cash3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layoutVisibility(layoutCash3,isChecked);

            }
        });
        CheckBox cash4 = (CheckBox)getView().findViewById(R.id.chkCash4);
        cash4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layoutVisibility(layoutCash4,isChecked);

            }
        });
        layoutVisibility(layoutCall,call.isChecked());
        layoutVisibility(layoutCash1,cash1.isChecked());
        layoutVisibility(layoutCash2,cash2.isChecked());
        layoutVisibility(layoutCash3,cash3.isChecked());
        layoutVisibility(layoutCash4,cash4.isChecked());
    }

    private void layoutVisibility(LinearLayout layout,boolean isChecked){
        if(isChecked){
            layout.setVisibility(View.VISIBLE);
        }else{
            layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void refreshData(String date){
        Log.d("refreshData","re");

        JSONObject rcvData = SendData.getMessage(context,MakeJSONData.get(MsgType.LOAD_ORDER,date).toString());
        try{
            Toasty.error(context,rcvData.getString("Msg"),Toasty.LENGTH_LONG).show();
            return;
        }catch (JSONException je2){

        }
        JSONArray arrCall=new JSONArray();
        JSONArray arrCash=new JSONArray();

        try{
            JSONArray jsonArray = rcvData.getJSONArray("List");
            for(int i=0;i<jsonArray.length();i++){
                try{
                    jsonArray.getJSONObject(i).getString("CATIME");
                    arrCall.put(jsonArray.getJSONObject(i));
                }catch (JSONException e){
                    arrCash.put(jsonArray.getJSONObject(i));
                }
            }

        }catch (JSONException je){
            je.printStackTrace();
        }


        orderRecyclerAdapter = new OrderRecyclerAdapter(arrCall);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerViewCall.setLayoutManager(linearLayoutManager);
        recyclerViewCall.setAdapter(orderRecyclerAdapter);
        orderRecyclerAdapter.notifyDataSetChanged();

        recyclerViewCall.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerViewCall, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Message msg = MainActivity.handler.obtainMessage();
                msg.what = MsgType.LOAD_NVCHART;
                msg.obj = orderRecyclerAdapter.getChartNo(position);

                MainActivity.handler.sendMessage(msg);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        final RecyclerView[] arrRecyclerView = {recyclerViewOrder1,recyclerViewOrder2,recyclerViewOrder3,recyclerViewOrder4};
        OrderCashRecyclerAdapter[] orderCashRecyclerAdapter = {orderCashRecyclerAdapter1,orderCashRecyclerAdapter2,orderCashRecyclerAdapter3,orderCashRecyclerAdapter4};
        for(int i=0;i<arrRecyclerView.length;i++){
            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
            orderCashRecyclerAdapter[i] = new OrderCashRecyclerAdapter(arrCash,i);
            arrRecyclerView[i].setLayoutManager(linearLayoutManager2);
            arrRecyclerView[i].setAdapter(orderCashRecyclerAdapter[i]);
            orderCashRecyclerAdapter[i].notifyDataSetChanged();

            arrRecyclerView[i].addOnItemTouchListener(new RecyclerTouchListener(context, arrRecyclerView[i], new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    RecyclerView recyclerView = (RecyclerView)view.getParent();
                    OrderCashRecyclerAdapter orderCashRecyclerAdapter = (OrderCashRecyclerAdapter)recyclerView.getAdapter();
                    Message msg = MainActivity.handler.obtainMessage();
                    msg.what = MsgType.LOAD_NVCHART;
                    msg.obj = orderCashRecyclerAdapter.getChartNo(position);

                    MainActivity.handler.sendMessage(msg);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }
    }
    public void initRecyclerView(){
        Log.d("OrderTable","initRecyclerView");

        /*ASyncTextSocket aSyncSocket = new ASyncTextSocket(getActivity(),"192.168.10.112",80);
        try{
            String data = aSyncSocket.execute("a").get();

        }catch (InterruptedException ie){
            ie.printStackTrace();
        }catch (ExecutionException ee){
            ee.printStackTrace();
        }*/

        //예약
        recyclerViewCall = (RecyclerView)getView().findViewById(R.id.recyclerCall);

        //접수,진료중,진료완료,수납완료
        recyclerViewOrder1 = (RecyclerView)getView().findViewById(R.id.recycler1);
        recyclerViewOrder2 = (RecyclerView)getView().findViewById(R.id.recycler2);
        recyclerViewOrder3 = (RecyclerView)getView().findViewById(R.id.recycler3);
        recyclerViewOrder4 = (RecyclerView)getView().findViewById(R.id.recycler4);

    }
}