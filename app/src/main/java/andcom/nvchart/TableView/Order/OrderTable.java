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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import andcom.nvchart.ASyncTextSocket;
import andcom.nvchart.MainActivity;
import andcom.nvchart.MakeJSONData;
import andcom.nvchart.MsgType;
import andcom.nvchart.Prefer;
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


public class OrderTable extends Fragment {
    ClickItem clickItem=null;
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
        refreshDataAsync(WaitingBoardDialog.getSelectedDate());

        //refreshData(MainActivity.getSelectedDate());
    }
    public void setClickItem(ClickItem clickItem){
        this.clickItem = clickItem;
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
                Prefer.setPref("order_0",isChecked);

            }
        });
        call.setChecked(Prefer.getPrefBoolean("order_0",true));
        CheckBox cash1 = (CheckBox)getView().findViewById(R.id.chkCash1);
        cash1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layoutVisibility(layoutCash1,isChecked);
                Prefer.setPref("order_1",isChecked);

            }
        });
        cash1.setChecked(Prefer.getPrefBoolean("order_1",true));
        CheckBox cash2 = (CheckBox)getView().findViewById(R.id.chkCash2);
        cash2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layoutVisibility(layoutCash2,isChecked);
                Prefer.setPref("order_2",isChecked);

            }
        });
        cash2.setChecked(Prefer.getPrefBoolean("order_2",true));
        CheckBox cash3 = (CheckBox)getView().findViewById(R.id.chkCash3);
        cash3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layoutVisibility(layoutCash3,isChecked);
                Prefer.setPref("order_3",isChecked);

            }
        });
        cash3.setChecked(Prefer.getPrefBoolean("order_3",true));
        CheckBox cash4 = (CheckBox)getView().findViewById(R.id.chkCash4);
        cash4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layoutVisibility(layoutCash4,isChecked);
                Prefer.setPref("order_4",isChecked);

            }
        });
        cash4.setChecked(Prefer.getPrefBoolean("order_4",true));
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

    public void refreshData(String date){
        Log.d("refreshData","re order");

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


        ItemClickSupport.addTo(recyclerViewCall).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Message msg = MainActivity.handler.obtainMessage();
                msg.what = MsgType.LOAD_NVCHART;
                msg.obj = orderRecyclerAdapter.getChartNo(position);

                MainActivity.handler.sendMessage(msg);
                clickItem.click();
            }
        });

        final RecyclerView[] arrRecyclerView = {recyclerViewOrder1,recyclerViewOrder2,recyclerViewOrder3,recyclerViewOrder4};
        OrderCashRecyclerAdapter[] orderCashRecyclerAdapter = {orderCashRecyclerAdapter1,orderCashRecyclerAdapter2,orderCashRecyclerAdapter3,orderCashRecyclerAdapter4};
        for(int i=0;i<arrRecyclerView.length;i++){
            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
            orderCashRecyclerAdapter[i] = new OrderCashRecyclerAdapter(arrCash,i);
            arrRecyclerView[i].setLayoutManager(linearLayoutManager2);
            arrRecyclerView[i].setAdapter(orderCashRecyclerAdapter[i]);
            orderCashRecyclerAdapter[i].notifyDataSetChanged();


            ItemClickSupport.addTo(arrRecyclerView[i]).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView r, int position, View v) {
                    RecyclerView recyclerView = (RecyclerView)v.getParent();
                    OrderCashRecyclerAdapter orderCashRecyclerAdapter = (OrderCashRecyclerAdapter)recyclerView.getAdapter();
                    Message msg = MainActivity.handler.obtainMessage();
                    msg.what = MsgType.LOAD_NVCHART;
                    msg.obj = orderCashRecyclerAdapter.getChartNo(position);

                    MainActivity.handler.sendMessage(msg);
                    clickItem.click();
                }
            });
        }
    }
    public void refreshDataAsync(String date){
        Log.d("refreshData","re order");


        SendData.getMessageAsync(context,MakeJSONData.get(MsgType.LOAD_ORDER,date).toString(),new ASyncTextSocket.AsyncResponse() {
            @Override
            public void processFinish(String rcvMsg) {

                try{
                    JSONObject rcvData = new JSONObject(rcvMsg);
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

                    final RecyclerView[] arrRecyclerView = {recyclerViewOrder1,recyclerViewOrder2,recyclerViewOrder3,recyclerViewOrder4};
                    OrderCashRecyclerAdapter[] orderCashRecyclerAdapter = {orderCashRecyclerAdapter1,orderCashRecyclerAdapter2,orderCashRecyclerAdapter3,orderCashRecyclerAdapter4};
                    for(int i=0;i<arrRecyclerView.length;i++){
                        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
                        orderCashRecyclerAdapter[i] = new OrderCashRecyclerAdapter(arrCash,i);
                        arrRecyclerView[i].setLayoutManager(linearLayoutManager2);
                        arrRecyclerView[i].setAdapter(orderCashRecyclerAdapter[i]);
                        orderCashRecyclerAdapter[i].notifyDataSetChanged();

                        ItemClickSupport.addTo(arrRecyclerView[i]).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                            @Override
                            public void onItemClicked(RecyclerView r, int position, View v) {
                                RecyclerView recyclerView = (RecyclerView)v.getParent();
                                OrderCashRecyclerAdapter orderCashRecyclerAdapter = (OrderCashRecyclerAdapter)recyclerView.getAdapter();
                                Message msg = MainActivity.handler.obtainMessage();
                                msg.what = MsgType.LOAD_NVCHART;
                                msg.obj = orderCashRecyclerAdapter.getChartNo(position);

                                MainActivity.handler.sendMessage(msg);
                                clickItem.click();
                            }
                        });
                    }
                }catch (JSONException je2){
                    je2.printStackTrace();
                }
            }
        });






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


        ItemClickSupport.addTo(recyclerViewCall).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                Message msg = MainActivity.handler.obtainMessage();
                msg.what = MsgType.LOAD_NVCHART;
                msg.obj = orderRecyclerAdapter.getChartNo(position);

                MainActivity.handler.sendMessage(msg);
                clickItem.click();
            }
        });
    }
}