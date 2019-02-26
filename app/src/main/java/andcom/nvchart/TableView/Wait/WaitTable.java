package andcom.nvchart.TableView.Wait;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import andcom.nvchart.ASyncSocket;
import andcom.nvchart.ASyncTextSocket;
import andcom.nvchart.MainActivity;
import andcom.nvchart.R;
import andcom.nvchart.TableView.model.CellModel;
import andcom.nvchart.TableView.model.ColumnHeaderModel;
import andcom.nvchart.TableView.model.RowHeaderModel;

public class WaitTable extends Fragment implements MainActivity.Refresh {

    private TableView mTableView;
    private ProgressBar mProgressBar;
    private WaitTableAdapter mTableAdapter;
    private RecyclerView recyclerView;

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
        String tempData = "{\n" +
                "        \"User\":\"andcom3\",\n" +
                "        \"List\":\n" +
                "            [\n" +
                "              {\"CSCHARTNO\":\"00000003\",\"CSNAME\":\"이정옥\",\"CSAGE\":\"58/여\",\"CSSATE\":\"접수\",\"CSSATE_COLOR\":\"16711680\",\"CSSATE_BOLD\":\"1\",\"CSSEQ\":\"003\",\"CSTIME\":\"PM  2:26(00:02)\",\"CSDESC\":\"\",\"CSDOCTOR\":\"\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000002\",\"CSNAME\":\"이향선2\",\"CSAGE\":\"36/여\",\"CSSATE\":\"진료중\",\"CSSATE_COLOR\":\"32768\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"002\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM 10:00\"},\n" +
                "              {\"CSCHARTNO\":\"00000001\",\"CSNAME\":\"홍길동\",\"CSAGE\":\"52/남\",\"CSSATE\":\"진료완료\",\"CSSATE_COLOR\":\"255\",\"CSSATE_BOLD\":\"0\",\"CSSEQ\":\"001\",\"CSTIME\":\"PM  2:26\",\"CSDESC\":\"\",\"CSDOCTOR\":\"A이광수\",\"CSCASHIER\":\"\",\"CAAMPM\":\"\",\"CAAMPM_BOLD\":\"0\",\"CAAMPM_COLOR\":\"0\",\"CATIME\":\"AM  9:30\"}\n" +
                "            ]\n" +
                "     }";

        ASyncTextSocket aSyncSocket = new ASyncTextSocket(getActivity(),"192.168.10.109",80);
        try{
            tempData = aSyncSocket.execute("{\"User\":\"andcom3\",\"UserPhone\":\"010-1234-5678\",\"구분코드\":\"7\",\"CSDATE\":\"2019-02-18\"}").get();
            if(tempData == null){
                return;
            }
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }catch (ExecutionException ee){
            ee.printStackTrace();
        }
        WaitRecyclerAdapter adapter = new WaitRecyclerAdapter(tempData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
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
