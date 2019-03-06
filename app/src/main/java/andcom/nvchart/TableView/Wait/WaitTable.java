package andcom.nvchart.TableView.Wait;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.ProgressBar;

import com.evrencoskun.tableview.TableView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import andcom.nvchart.ASyncSocket;
import andcom.nvchart.ASyncTextSocket;
import andcom.nvchart.MainActivity;
import andcom.nvchart.MakeJSONData;
import andcom.nvchart.MsgType;
import andcom.nvchart.R;
import andcom.nvchart.RecyclerTouchListener;
import andcom.nvchart.SendData;
import andcom.nvchart.TableView.model.CellModel;
import andcom.nvchart.TableView.model.ColumnHeaderModel;
import andcom.nvchart.TableView.model.RowHeaderModel;

public class WaitTable extends Fragment implements MainActivity.Refresh {

    private TableView mTableView;
    private ProgressBar mProgressBar;
    private WaitTableAdapter mTableAdapter;
    private RecyclerView recyclerView;
    private Context context;

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
        initRecyclerView();
        refreshData();
    }

    @Override
    public void refreshData(){

        JSONObject rcvData = SendData.getMessage(context,MakeJSONData.get(MsgType.LOAD_WAIT,"2019-03-06").toString());

        final WaitRecyclerAdapter adapter = new WaitRecyclerAdapter(rcvData.toString());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Message msg = MainActivity.handler.obtainMessage();
                msg.what = MsgType.LOAD_NVCHART;
                msg.obj = adapter.getChartNo(position);

                MainActivity.handler.sendMessage(msg);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
    private void initRecyclerView(){
        recyclerView = (RecyclerView)getView().findViewById(R.id.waitRecyclerView);

    }
    private void initializeTableView(TableView tableView){

        // Create TableView Adapter
        mTableAdapter = new WaitTableAdapter(getContext());

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
            cellModels.add(new CellModel("5",i*5+5));
            cellModels.add(new CellModel("5",i*5+6));
            cellModels.add(new CellModel("5",i*5+7));
            cellModels.add(new CellModel("5",i*5+8));
            mCellList.add(cellModels);
            mRowHeaderList.add(new RowHeaderModel(String.valueOf(i)));
        }

        mColumnHeaderList.add(new ColumnHeaderModel("이름"));
        mColumnHeaderList.add(new ColumnHeaderModel("상태"));
        mColumnHeaderList.add(new ColumnHeaderModel("순번"));
        mColumnHeaderList.add(new ColumnHeaderModel("시간(대기시간)"));
        mColumnHeaderList.add(new ColumnHeaderModel("내역"));
        mColumnHeaderList.add(new ColumnHeaderModel("의사"));
        mColumnHeaderList.add(new ColumnHeaderModel("예약공간"));
        mColumnHeaderList.add(new ColumnHeaderModel("접수자"));
        mColumnHeaderList.add(new ColumnHeaderModel("예약시간(경과시간)"));


        tableView.setAdapter(mTableAdapter);
        mTableAdapter.setAllItems(mColumnHeaderList,mRowHeaderList,mCellList);

        // Create listener
        tableView.setTableViewListener(new WaitTableViewListener(tableView));

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
