package andcom.nvchart;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import andcom.nvchart.TableView.Order.OrderRecyclerAdapter;
import andcom.nvchart.TableView.Wait.WaitRecyclerAdapter;
import andcom.nvchart.util.ItemClickSupport;
import es.dmoral.toasty.Toasty;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustPanel.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustPanel#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustPanel extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static RecyclerView recyclerViewNvList;
    public static NvListRecyclerAdapter nvListRecyclerAdapter;
    Context context;

    EditText editName,editChartNo;

    static WaitRecyclerAdapter waitAdapter;
    static OrderRecyclerAdapter orderAdapter;
    RecyclerView waitRecyclerView,orderRecyclerView;
    ImageButton btnNext,btnPrev;
    TextView dateView;
    Button btnSearch;
    ImageButton btnRefresh;
    static DatePickerDialog datePickerDialog;

    public CustPanel() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustPanel.
     */
    // TODO: Rename and change types and number of parameters
    public static CustPanel newInstance(String param1, String param2) {
        CustPanel fragment = new CustPanel();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private void initView(){
        context = getContext();
        editName = getView().findViewById(R.id.editName);
        editChartNo = getView().findViewById(R.id.editChartNo);
        editName.setOnEditorActionListener(new keyboardActionListener());
        editChartNo.setOnEditorActionListener(new keyboardActionListener());

        recyclerViewNvList = getView().findViewById(R.id.recyclerNvList);
        waitRecyclerView = getView().findViewById(R.id.waitRecyclerView);
        orderRecyclerView = getView().findViewById(R.id.orderRecyclerView);
        btnSearch = getView().findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new searchBtnClickListener());

        btnRefresh = getView().findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDBList();
                loadOrderList();
                loadWaitList();
            }
        });
    }
    class keyboardActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId== EditorInfo.IME_ACTION_SEARCH){
                btnSearch.performClick();
            }
            return true;
        }
    }
    public class dateOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.dateNext){
                datePickerDialog.updateDate(datePickerDialog.getDatePicker().getYear(),
                        datePickerDialog.getDatePicker().getMonth(),
                        datePickerDialog.getDatePicker().getDayOfMonth()+1);
            }else if(v.getId() == R.id.datePrev){

                datePickerDialog.updateDate(datePickerDialog.getDatePicker().getYear(),
                        datePickerDialog.getDatePicker().getMonth(),
                        datePickerDialog.getDatePicker().getDayOfMonth()-1);
            }

            refreshDate(datePickerDialog.getDatePicker().getYear(),
                    datePickerDialog.getDatePicker().getMonth(),
                    datePickerDialog.getDatePicker().getDayOfMonth());
        }
    }

    private void refreshDate(int year, int month, int dayOfMonth){
        Calendar selectCal = Calendar.getInstance();
        selectCal.set(year,month,dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (E)");
        String date = sdf.format(selectCal.getTime());
        dateView.setText(date);

        loadOrderList();
        loadWaitList();
    }
    private void initDate(){
        btnNext = getView().findViewById(R.id.dateNext);
        btnPrev = getView().findViewById(R.id.datePrev);
        btnPrev.setOnClickListener(new dateOnclickListener());
        btnNext.setOnClickListener(new dateOnclickListener());
        datePickerDialog = new DatePickerDialog(context);

        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.e("datePicker","date = " + year+month+dayOfMonth);
                refreshDate(year,month,dayOfMonth);
            }
        });
        dateView = getView().findViewById(R.id.date);
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog.show();
            }
        });
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (E)");
        dateView.setText(sdf.format(date));
    }

    public static String getSelectedDate(){
        int year = datePickerDialog.getDatePicker().getYear();
        int month = datePickerDialog.getDatePicker().getMonth();
        int dayOfMonth = datePickerDialog.getDatePicker().getDayOfMonth();

        Calendar selectCal = Calendar.getInstance();
        selectCal.set(year,month,dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(selectCal.getTime());

        Log.e("datePicker","new date = " + date);

        return date;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cust_panel, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setLayout(getActivity().getWindow().getAttributes().width,getActivity().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initView();
        initDate();
        loadDBList();
        loadWaitList();
        loadOrderList();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @SuppressLint("StaticFieldLeak")
    private void loadDBList(){
        final ImageView loading_nvlist = getView().findViewById(R.id.loading_nvlist);

        new AsyncTask<Void,String,Integer>(){
            @Override
            protected void onPreExecute() {
                //LoadingProgress.getInstance().progressON((Activity)context,"불러오는중");
                super.onPreExecute();
                AnimationDrawable drawable = (AnimationDrawable)loading_nvlist.getDrawable();
                drawable.start();

                loading_nvlist.setVisibility(View.VISIBLE);
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                //LoadingProgress.getInstance().progressOFF();
                JSONObject msg = MakeJSONData.get(MsgType.LOAD_DBLIST);
                JSONObject rcvData=SendData.getMessage(context,msg.toString());
                /*try{
                    Toasty.error(context,rcvData.getString("Msg"),Toasty.LENGTH_LONG).show();
                    return null;
                }catch (JSONException je){

                }*/

                try{
                    int db = rcvData.getInt("DB");

                    return db;

                }catch(JSONException jje){
                    jje.printStackTrace();
                }finally {

                }
                return null;
            }
            @Override
            protected void onPostExecute(Integer db) {
                super.onPostExecute(db);
                if(db==null){
                    Toasty.error(context,"서버에 접속 할 수 없습니다.").show();

                    return;
                }
                LinearLayout ldb = getView().findViewById(R.id.layoutDB);
                ImageButton db1 = getView().findViewById(R.id.btnDB1);
                ImageButton db2 = getView().findViewById(R.id.btnDB2);
                ImageButton db3 = getView().findViewById(R.id.btnDB3);

                db1.setOnClickListener(new dbBtnOnClickListener());
                db2.setOnClickListener(new dbBtnOnClickListener());
                db3.setOnClickListener(new dbBtnOnClickListener());
                if(db>100){ //3개
                    ldb.setVisibility(View.VISIBLE);
                    db3.setVisibility(View.VISIBLE);
                    dbBtnSelection(db1);
                }else if(db>10){    //2개
                    ldb.setVisibility(View.VISIBLE);
                    if(db%10 == 3){
                        db3.setVisibility(View.VISIBLE);
                    }else{

                    }


                }else{  //1개

                }

                String sDB = Prefer.getPrefString("DB","1");
                switch (sDB){
                    case "1" :
                        dbBtnSelection(db1);
                        break;
                    case "2" :
                        dbBtnSelection(db2);
                        break;
                    case "3" :
                        dbBtnSelection(db3);
                        break;
                }
                loading_nvlist.setVisibility(View.GONE);

            }

        }.execute();



        /*try{
            rcvData = new JSONObject("{\"User\":\"andcom3\",\"DB\":\"123\"}");
        }catch(Exception e){
            e.printStackTrace();

        }*/

    }
    public class searchBtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            JSONObject rcvData = new JSONObject();

            closeKeyBoard();
            String name = editName.getText().toString().trim();
            String chart = editChartNo.getText().toString().trim();

            if(!name.equals("") && chart.equals("")){
                //이름만 넣고 검색
                JSONObject msg = MakeJSONData.get(MsgType.SEARCH_BY_NAME,name);
                rcvData =  SendData.getMessage(context,msg.toString());
                try{
                    Toasty.error(context,rcvData.getString("Msg"),Toasty.LENGTH_LONG).show();
                    return;
                }catch (JSONException je2){

                }
                Log.e("btnSearch","이름 검색 클릭");

            }else if(!editChartNo.getText().toString().equals("")){
                //차트번호 검색
                JSONObject msg = MakeJSONData.get(MsgType.SEARCH_BY_CHARTNO,chart);
                rcvData =  SendData.getMessage(context,msg.toString());
                try{
                    Toasty.error(context,rcvData.getString("Msg"),Toasty.LENGTH_LONG).show();
                    return;
                }catch (JSONException je2){

                }
                Log.e("btnSearch","차트 번호 검색 클릭");

                //차트번호로 검색
            }else{
                //둘다 공백인채로 눌렀을 때

                Log.e("btnSearch","둘다 공백 클릭");
                return;
            }

            try{
                if(rcvData.getString("Code").equals("9")){
                    Toast.makeText(context,rcvData.getString("Msg"),Toast.LENGTH_LONG).show();
                }
            }catch (JSONException je){

                RecyclerView recyclerView = getView().findViewById(R.id.recyclerCust);
                final CustListRecyclerAdapter adapter = new CustListRecyclerAdapter(rcvData.toString());
                if(adapter.getItemCount()==0){
                    Toasty.warning(context,"검색 결과가 없습니다.").show();
                }
                if(adapter.getItemCount()==1){
                    sendMessage(MsgType.LOAD_NVCHART,adapter.getChartNo(0));
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(adapter);

                ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        MainActivity.selectChart = adapter.getChartNo(position);
                        sendMessage(MsgType.LOAD_NVCHART,MainActivity.selectChart);
                    }
                });
            }
        }
    }
    public class dbBtnOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            dbBtnSelection(v);
        }
    }

    private void loadWaitList(){
        SendData.getMessageAsync(context, MakeJSONData.get(MsgType.LOAD_WAIT, getSelectedDate()).toString(), new ASyncTextSocket.AsyncResponse() {
            @Override
            public void processFinish(String rcvData) {

                waitAdapter = new WaitRecyclerAdapter(rcvData);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                waitRecyclerView.setLayoutManager(linearLayoutManager);
                waitRecyclerView.setAdapter(waitAdapter);

                ItemClickSupport.addTo(waitRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if(position<0)
                            return;

                        MainActivity.selectChart  = ((WaitRecyclerAdapter)recyclerView.getAdapter()).getChartNo(position);

                        if(MainActivity.selectChart!=null){
                            //load_NvChart(MainActivity.selectChart);
                            sendMessage(MsgType.LOAD_NVCHART,MainActivity.selectChart);

                        }else{
                            Toasty.info(context,"차트번호 또는 수진자명을 먼저 입력하세요.",Toasty.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
    private void loadOrderList(){
        SendData.getMessageAsync(context, MakeJSONData.get(MsgType.LOAD_ORDER, getSelectedDate()).toString(), new ASyncTextSocket.AsyncResponse() {
            @Override
            public void processFinish(String rcvMsg) {
                try {
                    JSONObject rcvData = new JSONObject(rcvMsg);
                    JSONArray arrCall = new JSONArray();
                    JSONArray arrCash = new JSONArray();

                    try {
                        JSONArray jsonArray = rcvData.getJSONArray("List");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                jsonArray.getJSONObject(i).getString("CATIME");
                                arrCall.put(jsonArray.getJSONObject(i));
                            } catch (JSONException e) {
                                arrCash.put(jsonArray.getJSONObject(i));
                            }
                        }

                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                    orderAdapter = new OrderRecyclerAdapter(arrCall);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    orderRecyclerView.setLayoutManager(linearLayoutManager);
                    orderRecyclerView.setAdapter(orderAdapter);

                }catch (JSONException je2){
                    je2.printStackTrace();
                }


                ItemClickSupport.addTo(orderRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if(position<0)
                            return;

                        MainActivity.selectChart  = ((OrderRecyclerAdapter)recyclerView.getAdapter()).getChartNo(position);

                        if(MainActivity.selectChart!=null){
                            //load_NvChart(MainActivity.selectChart);
                            sendMessage(MsgType.LOAD_NVCHART,MainActivity.selectChart);

                        }else{
                            Toasty.info(context,"차트번호 또는 수진자명을 먼저 입력하세요.",Toasty.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
    private void dbBtnSelection(View v){
        Log.e("dbBtnSelec","asd");
        LinearLayout ldb = getView().findViewById(R.id.layoutDB);
        ImageButton db1 = getView().findViewById(R.id.btnDB1);
        ImageButton db2 = getView().findViewById(R.id.btnDB2);
        ImageButton db3 = getView().findViewById(R.id.btnDB3);

        String db = "";

        db1.setSelected(false);
        db2.setSelected(false);
        db3.setSelected(false);

        View thisBtn = (ImageButton)v;
        db = thisBtn.getTag().toString();
        thisBtn.setSelected(true);
        Prefer.setPref("DB",thisBtn.getTag().toString());
        MainActivity.setjNvData(db,null,null,null);


        JSONObject msg = MakeJSONData.get(MsgType.LOAD_NVLIST,thisBtn.getTag().toString(),MainActivity.selectChart);

        JSONObject rcvData = SendData.getMessage(context,msg.toString());
        //리스트 만들기
        try{

            Toasty.error(context,rcvData.getString("Msg"),Toasty.LENGTH_LONG).show();
            return;
        }catch (JSONException je2){

        }
        Log.e("dbBtnSelec","asd"+rcvData.toString());



        nvListRecyclerAdapter = NvListRecyclerAdapter.getInstance(rcvData.toString());
        recyclerViewNvList = getView().findViewById(R.id.recyclerNvList);

        ItemClickSupport.addTo(recyclerViewNvList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if(position<0)
                    return;
                Prefer.setPref("NVLIST",position);

                nvListRecyclerAdapter.setSeletedPosition(position);
                if(MainActivity.selectChart!=null){
                    //load_NvChart(MainActivity.selectChart);
                    sendMessage(MsgType.LOAD_NVCHART,MainActivity.selectChart);

                }else{
                    Toasty.info(context,"차트번호 또는 수진자명을 먼저 입력하세요.",Toasty.LENGTH_LONG).show();
                }
            }
        });

        recyclerViewNvList.setAdapter(nvListRecyclerAdapter);
        recyclerViewNvList.setLayoutManager(new LinearLayoutManager(context));
        nvListRecyclerAdapter.notifyDataSetChanged();

        /*//db 생성
        NvChartDB nvChartDB = new NvChartDB(this,"NVCHART",null,1);
        SQLiteDatabase database = nvChartDB.getReadableDatabase();
        for(int i = 0; i<nvListRecyclerAdapter.getItemCount();i++){
            String _nodekey = nvListRecyclerAdapter.getNodeKey(i);
            String sql = "select * from NVCHART where NODEKEY = '"+_nodekey+"' and DB_NO='"+db+"'";
            Log.e("SQLITE",sql);
            Cursor cursor = database.rawQuery(sql,null);
            if(cursor.getCount()==0){
                database = nvChartDB.getWritableDatabase();
                sql = "insert into NVCHART(DB_NO,NODEKEY) values ('"+db+"','"+_nodekey+"')";
                database.execSQL(sql);
            }
            cursor.close();
        }
        nvChartDB.close();
        database.close();*/

        MainActivity.setjNvData(null,null,nvListRecyclerAdapter.getNodeKey(0),nvListRecyclerAdapter.getPageCnt(0));

    }

    private void sendMessage(int msgWhat,String msg){
        Message message = MainActivity.handler.obtainMessage();
        message.what = msgWhat;
        message.obj = msg;
        MainActivity.handler.sendMessage(message);
        if(msgWhat == MsgType.LOAD_NVCHART){
            dismiss();
        }
    }

    private void closeKeyBoard(){

        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(editChartNo.getWindowToken(), 0);
    }
}
