package andcom.nvchart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;





import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.JSONObject;
import org.jsoup.nodes.Node;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import andcom.nvchart.TableView.MainFrameTable;
import andcom.nvchart.TableView.Order.OrderTable;
import andcom.nvchart.TableView.Wait.WaitTable;
import andcom.nvchart.TableView.WaitingBoardDialog;
import andcom.nvchart.nvChart.NvChart;
import andcom.nvchart.util.ItemClickSupport;
import andcom.nvchart.util.LoadingProgress;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import andcom.nvchart.util.NodeRefreshDialog;
import andcom.nvchart.util.NvChartDB;
import es.dmoral.toasty.Toasty;
import kotlin.Function;
import kotlin.jvm.functions.Function1;

import static androidx.fragment.app.DialogFragment.STYLE_NORMAL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ToolButtonListener {
    int READ_STORAGE_PERMISSION = 11;

    static long transfer;
    public static Context context;
    public static Handler handler;
    static String store_version;

    static String selectDB;
    static String selectChart;

    FrameLayout waitFrame, frmMain;
    Button btnSearch;
    Fragment fragment;
    FragmentManager fm;
    FragmentTransaction ft;
    NavigationView navigationView;
    DrawerLayout drawer;
    RelativeLayout bottom_sheet;
    //BottomSheetBehavior bottomSheetBehavior;


    TextInputEditText editName,editChartNo;

    TextView pageIndicator,titleCustInfo;

    public static RecyclerView recyclerViewNvList;
    public static NvListRecyclerAdapter nvListRecyclerAdapter;
    private static JSONObject jNvData;

    Handler handler1 ;

    public static void setjNvData(@Nullable String db, @Nullable String chartno, @Nullable String nodekey, @Nullable String page){
        if(jNvData==null){
            jNvData = MakeJSONData.get(MsgType.LOAD_NVCHART,db,chartno,nodekey,page);
        }else{
            try{
                if(db!=null)jNvData.put("DB",db);
                if(chartno!=null) jNvData.put("ND_CHARTNO",chartno);
                if(nodekey!=null) jNvData.put("ND_NODEKEY",nodekey);
                if(page!=null) jNvData.put("ND_PAGENO",page);

            }catch (JSONException je){
                je.printStackTrace();
            }
        }
        Log.w("jNvData",jNvData.toString());
    }
    public static JSONObject getjNvData(){

        return jNvData;
    }


    @SuppressLint({"ClickableViewAccessibility", "HandlerLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnWaitOrder = findViewById(R.id.btnWaitOrder);
        btnWaitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                int state = bottomSheetBehavior.getState();
                if (state == BottomSheetBehavior.STATE_HIDDEN || state == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                if (state == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FragmentManager fm = getSupportFragmentManager();
                        WaitingBoardDialog waitingBoardDialog = new WaitingBoardDialog();
                        waitingBoardDialog.show(fm,"");
                    }
                }).start();


            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
                int state = bottomSheetBehavior.getState();
                if (state == BottomSheetBehavior.STATE_HIDDEN || state == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                if (state == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });*/


        //gesturedetector = new GestureDetector(new MyGestureListener());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        bottom_sheet = (RelativeLayout) findViewById(R.id.bottom_sheet);
        final LinearLayout bottom_tool_bar = (LinearLayout) findViewById(R.id.bottom_tool_bar);

        bottom_sheet.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });

/*
        fm2 = getSupportFragmentManager();
        ft2 = fm2.beginTransaction();
        bottomFragment = new WaitTable();
        ft2.add(R.id.orderFrame, bottomFragment);
        ft2.commit();*/


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                loadDBList();
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FragmentManager fm = getSupportFragmentManager();
                        WaitingBoardDialog waitingBoardDialog = new WaitingBoardDialog();
                        waitingBoardDialog.setStyle(STYLE_NORMAL,STYLE_NORMAL );
                        waitingBoardDialog.show(fm,"");
                    }
                }).start();*/
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                closeKeyBoard();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Thread market = new Thread(){
            public void run(){
                //String store_version = MarketVersionChecker.getMarketVersion("andcom.elec_agreement");
                //MainActivity.store_version = store_version;
                String device_version = new String();

                try{
                    Document test = Jsoup.connect("http://www.andwin.co.kr/app_version.html").get();
                    MainActivity.store_version = test.getElementById("app_version").text();
                    String updateDescription = test.getElementById("desc").text();

                    Document doc = Jsoup.connect("http://www.checkip.org").get();
                    String localAddr = doc.getElementById("yourip").select("h1").first().select("span").text();
                    Prefer.setPref("key_public_ip",localAddr);
                    Log.d("IP", "local address is : " + localAddr);

                    device_version = getPackageManager().getPackageInfo(getPackageName(),0).versionName;

                    if(VersionStringToInteger(store_version)==VersionStringToInteger(device_version)){    //현재 최신버전
                        Log.d("versionCheck","최신버전");
                    }else if(store_version==null){        //스토어 버전 확인 불가


                    }else if(VersionStringToInteger(store_version)>VersionStringToInteger(device_version)){                                  //스토어와 버전 불일치
                        Message message = handler.obtainMessage();
                        message.what = 3;
                        message.obj = updateDescription;
                        handler.sendMessage(message);
                    }
                }catch(Exception e){
                    for(int i = 0;i<e.getStackTrace().length;i++){
                        Log.e("market Test" , "error occur : "+e.getStackTrace()[i].toString());
                    }
                }
                Log.d(getPackageName(),"Store Version : "+store_version+", Device Version : "+ device_version);

            }
        };
        market.start();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MsgType.LOAD_NVCHART :
                        load_NvChart(msg.obj.toString());
                        break;
                    case MsgType.UPDATE_PAGE_NO :
                        pageIndicator.setText(msg.obj.toString());
                        break;
                        case MsgType.LOAD_DBLIST:
                            loadDBList();
                            break;
                    case MsgType.SET_CUST_NAME :
                        String custInfo = msg.obj.toString();
                        titleCustInfo.setText(custInfo);
                        titleCustInfo.setVisibility(View.VISIBLE);
                        break;

                    case MsgType.CLOSE_CHART :

                        pageIndicator.setText("0/0");
                        titleCustInfo.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        };

        handler1 = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {


                ItemClickSupport.addTo(recyclerViewNvList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if(position<0)
                            return;
                        Prefer.setPref("NVLIST",position);

                        nvListRecyclerAdapter.setSeletedPosition(position);
                        if(selectChart!=null){
                            load_NvChart(selectChart);

                        }else{
                            Toasty.info(context,"차트번호 또는 수진자명을 먼저 입력하세요.",Toasty.LENGTH_LONG).show();
                        }
                    }
                });

                recyclerViewNvList.setAdapter(nvListRecyclerAdapter);
                recyclerViewNvList.setLayoutManager(new LinearLayoutManager(context));
            }
        };

        initView();
        loadDBList();
    }

    private void initView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        //headerView.getBackground().setColorFilter(0x80000000,PorterDuff.Mode.MULTIPLY);
        navigationView.getBackground().setColorFilter(0x80ffffff,PorterDuff.Mode.MULTIPLY);

        frmMain = (FrameLayout) findViewById(R.id.frmMain);

        btnSearch = (Button) navigationView.getHeaderView(0).findViewById(R.id.btnSearch);

        class keyboardActionListener implements TextView.OnEditorActionListener {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    onClick_btnSearch(btnSearch);
                }
                return true;
            }
        }
        editName = (TextInputEditText)navigationView.getHeaderView(0).findViewById(R.id.editName);
        editChartNo = (TextInputEditText)navigationView.getHeaderView(0).findViewById(R.id.editChartNo);
        editName.setOnEditorActionListener(new keyboardActionListener());
        editChartNo.setOnEditorActionListener(new keyboardActionListener());



        pageIndicator = (TextView)findViewById(R.id.pageIndicator);

        titleCustInfo = findViewById(R.id.custInfoText);


        //mainFragment = new MainFrameTable();
        fragment = new NvChart();
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.frmMain, fragment);

        ft.commit();

        /*FragmentManager fm2 = getSupportFragmentManager();
        FragmentTransaction ft2 = fm2.beginTransaction();
        ft2.replace(R.id.frmMain,mainFragment);

        ft2.commit();*/

        LinearLayout tool = (LinearLayout)findViewById(R.id.toollayout);
        for(int i=0;i<tool.getChildCount();i++){
            tool.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClicked(v);
                }
            });
        }

        LinearLayout pageControl = (LinearLayout)findViewById(R.id.pageControl);
        for(int i = 0 ; i<pageControl.getChildCount();i++){
            if(pageControl.getChildAt(i) instanceof ImageButton){
                pageControl.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClicked(v);
                    }
                });
            }
        }

        LinearLayout UndoRedo = (LinearLayout)findViewById(R.id.layoutUndoRedo);
        for(int i=0;i<UndoRedo.getChildCount();i++){
            UndoRedo.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClicked(v);
                }
            });
        }

        ImageButton btnNew = (ImageButton)findViewById(R.id.btnNew);
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicked(v);
            }
        });
        ImageButton btnSave = (ImageButton)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicked(v);
            }
        });
    }
    private void initSqlite(){

    }
    public void onClicked(View v){
        if("search".equals(v.getTag().toString())){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(Gravity.LEFT);
        }
        if(fragment instanceof NvChart){
            ((NvChart) fragment).onClicked(v);
        }else{
            Log.e("MainActivity","OnClicked" + v.getTag().toString() );

        }
    }
    @SuppressLint("StaticFieldLeak")
    private void loadDBList(){
        final ImageView loading_nvlist = (ImageView)findViewById(R.id.loading_nvlist);

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
                LinearLayout ldb = (LinearLayout)findViewById(R.id.layoutDB);
                ImageButton db1 = (ImageButton)findViewById(R.id.btnDB1);
                ImageButton db2 = (ImageButton)findViewById(R.id.btnDB2);
                ImageButton db3 = (ImageButton)findViewById(R.id.btnDB3);

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
    public class dbBtnOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            dbBtnSelection(v);
        }
    }
    private void dbBtnSelection(View v){
        Log.e("dbBtnSelec","asd");
        LinearLayout ldb = (LinearLayout)findViewById(R.id.layoutDB);
        ImageButton db1 = (ImageButton)findViewById(R.id.btnDB1);
        ImageButton db2 = (ImageButton)findViewById(R.id.btnDB2);
        ImageButton db3 = (ImageButton)findViewById(R.id.btnDB3);

        String db = "";

        db1.setSelected(false);
        db2.setSelected(false);
        db3.setSelected(false);

        View thisBtn = (ImageButton)v;
        db = thisBtn.getTag().toString();
        thisBtn.setSelected(true);
        Prefer.setPref("DB",thisBtn.getTag().toString());
        setjNvData(db,null,null,null);


        JSONObject msg = MakeJSONData.get(MsgType.LOAD_NVLIST,thisBtn.getTag().toString(),selectChart);

        JSONObject rcvData = SendData.getMessage(context,msg.toString());
        //리스트 만들기
        try{
            Toasty.error(this,rcvData.getString("Msg"),Toasty.LENGTH_LONG).show();
            return;
        }catch (JSONException je2){

        }
        Log.e("dbBtnSelec","asd"+rcvData.toString());



        nvListRecyclerAdapter = NvListRecyclerAdapter.getInstance(rcvData.toString());
        recyclerViewNvList = (RecyclerView)findViewById(R.id.recyclerNvList);

        Message message = handler1.obtainMessage();
        handler1.sendMessage(message);
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



        setjNvData(null,null,nvListRecyclerAdapter.getNodeKey(0),nvListRecyclerAdapter.getPageCnt(0));

    }

    private void setfullwidth(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        params.width = displayMetrics.widthPixels;
        navigationView.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void closeKeyBoard(){

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(editChartNo.getWindowToken(), 0);
    }

    public void onClick_btnSearch(View v){
        JSONObject rcvData = new JSONObject();

        closeKeyBoard();
        String name = editName.getText().toString().trim();
        String chart = editChartNo.getText().toString().trim();

        if(!name.equals("") && chart.equals("")){
            //이름만 넣고 검색
            JSONObject msg = MakeJSONData.get(MsgType.SEARCH_BY_NAME,name);
            rcvData =  SendData.getMessage(this,msg.toString());
            try{
                Toasty.error(this,rcvData.getString("Msg"),Toasty.LENGTH_LONG).show();
                return;
            }catch (JSONException je2){

            }
            Log.e("btnSearch","이름 검색 클릭");

        }else if(!editChartNo.getText().toString().equals("")){
            //차트번호 검색
            JSONObject msg = MakeJSONData.get(MsgType.SEARCH_BY_CHARTNO,chart);
            rcvData =  SendData.getMessage(this,msg.toString());
            try{
                Toasty.error(this,rcvData.getString("Msg"),Toasty.LENGTH_LONG).show();
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
                Toast.makeText(this,rcvData.getString("Msg"),Toast.LENGTH_LONG).show();
            }
        }catch (JSONException je){

            RecyclerView recyclerView = (RecyclerView)navigationView.findViewById(R.id.recyclerCust);
            final CustListRecyclerAdapter adapter = new CustListRecyclerAdapter(rcvData.toString());
            if(adapter.getItemCount()==0){
                Toasty.warning(this,"검색 결과가 없습니다.").show();
            }
            if(adapter.getItemCount()==1){
                load_NvChart(adapter.getChartNo(0));
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    selectChart = adapter.getChartNo(position);
                    load_NvChart(selectChart);
                }
            });
        }
    }


    private void sendMessage(int msgWhat,Object msg){
        Message message = NvChart.handler.obtainMessage();
        message.what=msgWhat;
        message.obj=msg;
        NvChart.handler.sendMessage(message);
    }

    public void load_NvChart(final String chartNo){

        if(nvListRecyclerAdapter == null){
            loadDBList();
        }
        if(chartNo.equals("")){

        }
        if(chartNo!=null){
            selectChart=chartNo;
            JSONObject rcvData = SendData.getMessage(this,MakeJSONData.get(MsgType.LOAD_NVLIST,Prefer.getPrefString("DB","1"),chartNo).toString());
            try{
                Toasty.error(this,rcvData.getString("Code")+":"+rcvData.getString("Msg"),Toasty.LENGTH_LONG).show();
                return;
            }catch (JSONException je2){

            }
            nvListRecyclerAdapter = NvListRecyclerAdapter.getInstance(rcvData.toString());
            recyclerViewNvList.setAdapter(nvListRecyclerAdapter);
            nvListRecyclerAdapter.notifyDataSetChanged();
        }

        int nPageCnt = nvListRecyclerAdapter.getPageCnt(nvListRecyclerAdapter.getNodeKey(Prefer.getPrefInt("NVLIST",0)));
        Log.e("pagecnt",Prefer.getPrefInt("NVLIST",0)+" "+nvListRecyclerAdapter.getNodeKey(Prefer.getPrefInt("NVLIST",0))+nPageCnt);
        if(nPageCnt == 0)
            nPageCnt = 1;
        setjNvData(null,chartNo,nvListRecyclerAdapter.getNodeKey(Prefer.getPrefInt("NVLIST",0)),String.valueOf(nPageCnt));
        nvListRecyclerAdapter.setSeletedPosition(Prefer.getPrefInt("NVLIST",0));

        drawer.closeDrawer(GravityCompat.START, true);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editChartNo.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);

        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                //fragment = new NvChart();
                //Bundle bundle = new Bundle();
                //bundle.putString("DATA",getjNvData().toString());
                //fragment.setArguments(bundle);
                //FragmentManager fm = getSupportFragmentManager();
                //FragmentTransaction ft = fm.beginTransaction();

                //ft.replace(R.id.frmMain, fragment);
                //ft.commit();

                sendMessage(MsgType.REFRESH_NVCHART,getjNvData().toString());
                ft.replace(R.id.frmMain,fragment);
            }
        });
        worker.start();



    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(fragment instanceof NvChart){
            /*selectChart = null;
            loadDBList();
            fragment = new MainFrameTable();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frmMain,fragment);
            ft.commit();*/

            //sendMessage(MsgType.NVCHART_RELEASE,true);
            if(findViewById(R.id.backgroundImage).getVisibility()==View.VISIBLE){
                super.onBackPressed();

            }else{
                sendMessage(MsgType.CLOSE_CHART,"");
                selectChart=null;
            }
        }
    }
    private int VersionStringToInteger(String version){
        int nVersion;

        String[] splitVersion = version.split("\\.");
        Log.e("version=","v?"+version);
        nVersion =  Integer.parseInt(splitVersion[0])*100 +Integer.parseInt(splitVersion[1])*10 + Integer.parseInt(splitVersion[2]);
        Log.e("version=","v?"+nVersion);
        return nVersion;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this,prefActivity.class);
            startActivity(settings);
            return true;
        }
        if(id==R.id.action_node){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fm = getSupportFragmentManager();
                    NodeRefreshDialog nodeRefreshDialog = new NodeRefreshDialog();
                    nodeRefreshDialog.show(fm,"");
                }
            }).start();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /*public boolean dispatchTouchEvent(MotionEvent ev) {

        super.dispatchTouchEvent(ev);

        return gesturedetector.onTouchEvent(ev);

    }*/
    /*class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 20;

        private static final int SWIPE_MAX_OFF_PATH = 100;

        private static final int SWIPE_THRESHOLD_VELOCITY = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,

                               float velocityY) {

            float dX = e2.getX() - e1.getX();

            float dY = e1.getY() - e2.getY();

            if (Math.abs(dY) < SWIPE_MAX_OFF_PATH &&

                    Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY &&

                    Math.abs(dX) >= SWIPE_MIN_DISTANCE) {

                if (dX > 0) {

                    Toast.makeText(getApplicationContext(), "Right Swipe",
                            Toast.LENGTH_SHORT).show();
                    //Now Set your animation

                    *//*if(ivLayer2.getVisibility()==View.GONE)
                    {
                        Animation fadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_right_in);
                        ivLayer2.startAnimation(fadeInAnimation);
                        ivLayer2.setVisibility(View.VISIBLE);
                    }*//*

                    if(bottomFragment instanceof WaitTable){
                        bottomFragment = new OrderTable();
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.orderFrame,bottomFragment);
                        ft.commit();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Left Swipe",
                            Toast.LENGTH_SHORT).show();

                    *//*if(ivLayer2.getVisibility()==View.VISIBLE)
                    {
                        Animation fadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_left_out);
                        ivLayer2.startAnimation(fadeInAnimation);
                        ivLayer2.setVisibility(View.GONE);
                    }*//*
                    if(bottomFragment instanceof OrderTable){
                        bottomFragment = new WaitTable();
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.orderFrame,bottomFragment);
                        ft.commit();
                    }
                }

                return true;

            } else if (Math.abs(dX) < SWIPE_MAX_OFF_PATH &&

                    Math.abs(velocityY) >= SWIPE_THRESHOLD_VELOCITY &&

                    Math.abs(dY) >= SWIPE_MIN_DISTANCE) {

                if (dY > 0) {

                    Toast.makeText(getApplicationContext(), "Up Swipe",
                            Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getApplicationContext(), "Down Swipe",
                            Toast.LENGTH_SHORT).show();
                }

                return true;

            }

            return false;

        }

    }

*/
}
