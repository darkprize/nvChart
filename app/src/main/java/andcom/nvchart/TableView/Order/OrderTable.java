package andcom.nvchart.TableView.Order;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.evrencoskun.tableview.TableView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import andcom.nvchart.ASyncTextSocket;
import andcom.nvchart.MainActivity;
import andcom.nvchart.R;
import andcom.nvchart.TableView.Wait.WaitRecyclerAdapter;
import andcom.nvchart.TableView.model.CellModel;
import andcom.nvchart.TableView.model.ColumnHeaderModel;
import andcom.nvchart.TableView.Wait.WaitTableViewListener;
import andcom.nvchart.TableView.model.RowHeaderModel;


public class OrderTable extends Fragment implements MainActivity.Refresh {
    private TableView mTableView;
    private ProgressBar mProgressBar;
    private OrderTableAdapter mTableAdapter;

    private RecyclerView recyclerViewCall,recyclerViewOrder1,recyclerViewOrder2,recyclerViewOrder3,recyclerViewOrder4;
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


        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        initRecyclerView();
        RecyclerViewVisibility();
        refreshData();
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
    public void refreshData(){
        Log.d("refreshData","re");
        String tempData = "{\n" +
                "        \"User\":\"andcom3\",\n" +
                "        \"List\":\n" +
                "            [\n" +
                "              {\"CATIME\":\"AM  9:30\",\"CANAME\":\"홍길동\",\"CACHARTNO\":\"00000001\",\"CTJUMINNO\":\"661116-1******\",\"CASIGN\":\"예약내용 테스트1\",\"CARESULT\":\"1\",\"CA_FONTFORECOLOR\":\"-2147483632\",\"CA_FONTSTRIKETHROUGH\":\"0\",\"CAVIP\":\"0\",\"CAVIP_FONTFORECOLOR\":\"0\"},\n" +
                "              {\"CATIME\":\"AM 10:00\",\"CANAME\":\"이향선2\",\"CACHARTNO\":\"00000002\",\"CTJUMINNO\":\"820629-2******\",\"CASIGN\":\"예약내용 테스트2\",\"CARESULT\":\"1\",\"CA_FONTFORECOLOR\":\"-2147483632\",\"CA_FONTSTRIKETHROUGH\":\"0\",\"CAVIP\":\"1\",\"CAVIP_FONTFORECOLOR\":\"16711680\"},\n" +
                "              {\"CATIME\":\"AM 10:00\",\"CANAME\":\"이향선2\",\"CACHARTNO\":\"00000002\",\"CTJUMINNO\":\"820629-2******\",\"CASIGN\":\"예약내용 테스트3 1234567890\",\"CARESULT\":\"1\",\"CA_FONTFORECOLOR\":\"-2147483632\",\"CA_FONTSTRIKETHROUGH\":\"0\",\"CAVIP\":\"1\",\"CAVIP_FONTFORECOLOR\":\"16711680\"},\n" +
                //"              {\"CATIME\":\"AM 10:00\",\"CANAME\":\"이향선2\",\"CACHARTNO\":\"00000002\",\"CTJUMINNO\":\"820629-2******\",\"CASIGN\":\"\",\"CARESULT\":\"1\",\"CA_FONTFORECOLOR\":\"-2147483632\",\"CA_FONTSTRIKETHROUGH\":\"0\",\"CAVIP\":\"1\",\"CAVIP_FONTFORECOLOR\":\"16711680\"},\n" +
                //"              {\"CATIME\":\"AM 10:00\",\"CANAME\":\"이향선2\",\"CACHARTNO\":\"00000002\",\"CTJUMINNO\":\"820629-2******\",\"CASIGN\":\"\",\"CARESULT\":\"1\",\"CA_FONTFORECOLOR\":\"-2147483632\",\"CA_FONTSTRIKETHROUGH\":\"0\",\"CAVIP\":\"1\",\"CAVIP_FONTFORECOLOR\":\"16711680\"},\n" +
                //"              {\"CATIME\":\"AM 10:00\",\"CANAME\":\"이향선2\",\"CACHARTNO\":\"00000002\",\"CTJUMINNO\":\"820629-2******\",\"CASIGN\":\"\",\"CARESULT\":\"1\",\"CA_FONTFORECOLOR\":\"-2147483632\",\"CA_FONTSTRIKETHROUGH\":\"0\",\"CAVIP\":\"1\",\"CAVIP_FONTFORECOLOR\":\"16711680\"},\n" +
                //"              {\"CATIME\":\"AM 10:00\",\"CANAME\":\"이향선2\",\"CACHARTNO\":\"00000002\",\"CTJUMINNO\":\"820629-2******\",\"CASIGN\":\"\",\"CARESULT\":\"1\",\"CA_FONTFORECOLOR\":\"-2147483632\",\"CA_FONTSTRIKETHROUGH\":\"0\",\"CAVIP\":\"1\",\"CAVIP_FONTFORECOLOR\":\"16711680\"},\n" +
                //"              {\"CATIME\":\"AM 10:00\",\"CANAME\":\"이향선2\",\"CACHARTNO\":\"00000002\",\"CTJUMINNO\":\"820629-2******\",\"CASIGN\":\"\",\"CARESULT\":\"1\",\"CA_FONTFORECOLOR\":\"-2147483632\",\"CA_FONTSTRIKETHROUGH\":\"0\",\"CAVIP\":\"1\",\"CAVIP_FONTFORECOLOR\":\"16711680\"},\n" +
                //"              {\"CATIME\":\"AM 10:00\",\"CANAME\":\"이향선2\",\"CACHARTNO\":\"00000002\",\"CTJUMINNO\":\"820629-2******\",\"CASIGN\":\"\",\"CARESULT\":\"1\",\"CA_FONTFORECOLOR\":\"-2147483632\",\"CA_FONTSTRIKETHROUGH\":\"0\",\"CAVIP\":\"1\",\"CAVIP_FONTFORECOLOR\":\"16711680\"},\n" +
                "              {\"CSSEQ\":\"001\",\"CSTIME\":\"PM  2:26\",\"CSNAME\":\"홍길동\",\"CSCHARTNO\":\"00000001\",\"CTJUMINNO\":\"661116-1******\",\"CSDESC\":\"\",\"CSCALLCASH\":\"1\",\"CSSTATE\":\"2\",\"CSCASH\":\"0\"},\n" +
                "              {\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSNAME\":\"이향선2\",\"CSCHARTNO\":\"00000002\",\"CTJUMINNO\":\"820629-2******\",\"CSDESC\":\"\",\"CSCALLCASH\":\"1\",\"CSSTATE\":\"1\",\"CSCASH\":\"0\"},\n" +
                "              {\"CSSEQ\":\"003\",\"CSTIME\":\"PM  2:26\",\"CSNAME\":\"이정옥\",\"CSCHARTNO\":\"00000003\",\"CTJUMINNO\":\"600220-2******\",\"CSDESC\":\"\",\"CSCALLCASH\":\"0\",\"CSSTATE\":\"0\",\"CSCASH\":\"0\"}\n" +
                "            ]\n" +
                "     }";
        ASyncTextSocket aSyncSocket = new ASyncTextSocket(getActivity(),"192.168.10.109",80);
        try{
            tempData = aSyncSocket.execute("{\"User\":\"andcom3\",\"UserPhone\":\"010-1234-5678\",\"구분코드\":\"8\",\"CSDATE\":\"2019-02-18\"}").get();
            if(tempData == null){
                return;
            }
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }catch (ExecutionException ee){
            ee.printStackTrace();
        }

        JSONArray arrCall=new JSONArray();
        JSONArray arrCash=new JSONArray();

        try{
            JSONObject data = new JSONObject(tempData);
            JSONArray jsonArray = data.getJSONArray("List");
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


        OrderRecyclerAdapter adapter = new OrderRecyclerAdapter(arrCall);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerViewCall.setLayoutManager(linearLayoutManager);
        recyclerViewCall.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        RecyclerView[] arrRecyclerView = {recyclerViewOrder1,recyclerViewOrder2,recyclerViewOrder3,recyclerViewOrder4};
        for(int i=0;i<arrRecyclerView.length;i++){
            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
            OrderCashRecyclerAdapter orderCashRecyclerAdapter = new OrderCashRecyclerAdapter(arrCash,i);
            arrRecyclerView[i].setLayoutManager(linearLayoutManager2);
            arrRecyclerView[i].setAdapter(orderCashRecyclerAdapter);
            orderCashRecyclerAdapter.notifyDataSetChanged();
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
    private void initializeTableView(TableView tableView){

        // Create TableView Adapter
        mTableAdapter = new OrderTableAdapter(getContext());

        List<RowHeaderModel> mRowHeaderList=new ArrayList<>();
        List<ColumnHeaderModel> mColumnHeaderList=new ArrayList<>();
        List<List<CellModel>> mCellList=new ArrayList<>();
        for(int i = 0 ; i<8 ; i++){
            List<CellModel> cellModels = new ArrayList<>();
            cellModels.add(new CellModel("1",i*5+0));
            cellModels.add(new CellModel("2",i*5+1));
            cellModels.add(new CellModel("3",i*5+2));
            cellModels.add(new CellModel("4",i*5+3));
            cellModels.add(new CellModel("5",i*5+4));
            mCellList.add(cellModels);
            mRowHeaderList.add(new RowHeaderModel(String.valueOf(i)));
        }

        mColumnHeaderList.add(new ColumnHeaderModel("예약"));
        mColumnHeaderList.add(new ColumnHeaderModel("접수"));
        mColumnHeaderList.add(new ColumnHeaderModel("진료중"));
        mColumnHeaderList.add(new ColumnHeaderModel("진료완료"));
        mColumnHeaderList.add(new ColumnHeaderModel("수납완료"));

        tableView.setAdapter(mTableAdapter);
        mTableAdapter.setAllItems(mColumnHeaderList,mRowHeaderList,mCellList);
        mTableAdapter.setRowHeaderWidth(300);
        // Create listener
        tableView.setTableViewListener(new OrderTableViewListener(tableView));


    }
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTableView.setVisibility(View.INVISIBLE);
    }

    public void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mTableView.setVisibility(View.VISIBLE);
    }
}