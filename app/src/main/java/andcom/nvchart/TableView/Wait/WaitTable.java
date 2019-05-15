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

public class WaitTable extends Fragment implements MainActivity.Refresh {

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
        initRecyclerView();
        refreshData(MainActivity.getSelectedDate());

    }

    @Override
    public void refreshData(String date){

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

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.e("wait click","wait clicked");
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
}
