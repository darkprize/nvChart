package andcom.nvchart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;





import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
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

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import andcom.nvchart.TableView.MainFrameTable;
import andcom.nvchart.TableView.Order.OrderTable;
import andcom.nvchart.TableView.Wait.WaitTable;
import andcom.nvchart.nvChart.NvChart;
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
import es.dmoral.toasty.Toasty;
import kotlin.Function;
import kotlin.jvm.functions.Function1;

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
    Fragment fragment, mainFragment;
    FragmentManager fm;
    FragmentTransaction ft;
    NavigationView navigationView;
    DrawerLayout drawer;
    RelativeLayout bottom_sheet;
    BottomSheetBehavior bottomSheetBehavior;

    Fragment bottomFragment;
    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    OrderTable orderTable;
    WaitTable waitTable;

    TextInputEditText editName,editChartNo;

    TextView pageIndicator,dateView;
    static DatePickerDialog datePickerDialog;

    public static RecyclerView recyclerViewNvList;
    public static NvListRecyclerAdapter nvListRecyclerAdapter;
    private static JSONObject jNvData;

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
        final View bottomToolbar = (View) findViewById(R.id.bottomToolBarSlideBtn);
        bottomToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int state = bottomSheetBehavior.getState();
                if (state == BottomSheetBehavior.STATE_HIDDEN || state == BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                if (state == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        });


        //gesturedetector = new GestureDetector(new MyGestureListener());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        bottom_sheet = (RelativeLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);


        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
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
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

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
                }
            }
        };



        initView();
        loadDBList();

    }

    private void initView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CardView cardViewNvList = (CardView)findViewById(R.id.cardViewNvList);
                CardView.LayoutParams params = (CardView.LayoutParams)cardViewNvList.getLayoutParams();
                params.topMargin = navigationView.getHeaderView(0).getHeight();
                cardViewNvList.setLayoutParams(params);
            }
        });

        View headerView = navigationView.getHeaderView(0);
        //headerView.getBackground().setColorFilter(0x80000000,PorterDuff.Mode.MULTIPLY);
        navigationView.getBackground().setColorFilter(0x80ffffff,PorterDuff.Mode.MULTIPLY);

        frmMain = (FrameLayout) findViewById(R.id.frmMain);

        waitFrame = (FrameLayout) navigationView.getHeaderView(0).findViewById(R.id.waitFrame_Header);
        btnSearch = (Button) navigationView.getHeaderView(0).findViewById(R.id.btnSearch);

        editName = (TextInputEditText)navigationView.getHeaderView(0).findViewById(R.id.editName);
        editChartNo = (TextInputEditText)navigationView.getHeaderView(0).findViewById(R.id.editChartNo);



        pageIndicator = (TextView)findViewById(R.id.pageIndicator);
        dateView = findViewById(R.id.date);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (E)");
        dateView.setText(sdf.format(date));


        mainFragment = new MainFrameTable();
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


        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText("대기현황"));
        tabLayout.addTab(tabLayout.newTab().setText("진료현황"));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        waitTable = new WaitTable();
        orderTable = new OrderTable();
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), waitTable, orderTable);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        orderTable = (OrderTable) pagerAdapter.getItem(1);
        waitTable = (WaitTable) pagerAdapter.getItem(0);

        datePickerDialog = new DatePickerDialog(this);
    }

    public void onClicked(View v){
        if(fragment instanceof NvChart){
            ((NvChart) fragment).onClicked(v);
        }else{
            Log.e("MainActivity","OnClicked" + v.getTag().toString() );

        }
    }
    private void loadDBList(){
        ImageView loading_nvlist = (ImageView)findViewById(R.id.loading_nvlist);
        AnimationDrawable drawable = (AnimationDrawable)loading_nvlist.getBackground();
        drawable.start();
        loading_nvlist.setVisibility(View.VISIBLE);
        JSONObject msg = MakeJSONData.get(MsgType.LOAD_DBLIST);
        JSONObject rcvData=SendData.getMessage(this,msg.toString());
        try{
            Toasty.error(this,rcvData.getString("Msg"),Toasty.LENGTH_LONG).show();
            return;
        }catch (JSONException je){

        }

        LinearLayout ldb = (LinearLayout)findViewById(R.id.layoutDB);
        ImageButton db1 = (ImageButton)findViewById(R.id.btnDB1);
        ImageButton db2 = (ImageButton)findViewById(R.id.btnDB2);
        ImageButton db3 = (ImageButton)findViewById(R.id.btnDB3);

        db1.setOnClickListener(new dbBtnOnClickListener());
        db2.setOnClickListener(new dbBtnOnClickListener());
        db3.setOnClickListener(new dbBtnOnClickListener());
        try{
            int db = rcvData.getInt("DB");
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

        }catch(JSONException jje){
            jje.printStackTrace();
        }finally {

            loading_nvlist.setVisibility(View.GONE);
        }
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
        LinearLayout ldb = (LinearLayout)findViewById(R.id.layoutDB);
        ImageButton db1 = (ImageButton)findViewById(R.id.btnDB1);
        ImageButton db2 = (ImageButton)findViewById(R.id.btnDB2);
        ImageButton db3 = (ImageButton)findViewById(R.id.btnDB3);



        db1.setSelected(false);
        db2.setSelected(false);
        db3.setSelected(false);

        View thisBtn = (ImageButton)v;
        thisBtn.setSelected(true);
        Prefer.setPref("DB",thisBtn.getTag().toString());
        setjNvData(thisBtn.getTag().toString(),null,null,null);


        JSONObject msg = MakeJSONData.get(MsgType.LOAD_NVLIST,thisBtn.getTag().toString(),selectChart);

        JSONObject rcvData = SendData.getMessage(context,msg.toString());
        //리스트 만들기
        try{
            Toasty.error(this,rcvData.getString("Msg"),Toasty.LENGTH_LONG).show();
            return;
        }catch (JSONException je2){

        }

        nvListRecyclerAdapter = NvListRecyclerAdapter.getInstance(rcvData.toString());
        recyclerViewNvList = (RecyclerView)findViewById(R.id.recyclerNvList);

        recyclerViewNvList.setAdapter(nvListRecyclerAdapter);
        recyclerViewNvList.setLayoutManager(new LinearLayoutManager(context));
        nvListRecyclerAdapter.notifyDataSetChanged();

        recyclerViewNvList.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerViewNvList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(position<0)
                    return;
                Prefer.setPref("NVLIST",position);

                //setjNvData(null,null,nvListRecyclerAdapter.getNodeKey(position),nvListRecyclerAdapter.getPageCnt(position));
                /*for(int i=0;i<recyclerViewNvList.getChildCount();i++)
                    recyclerViewNvList.findViewHolderForAdapterPosition(i).itemView.setSelected(false);
                view.setSelected(true);*/
                nvListRecyclerAdapter.setSeletedPosition(position);

                load_NvChart(selectChart);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        setjNvData(null,null,nvListRecyclerAdapter.getNodeKey(0),nvListRecyclerAdapter.getPageCnt(0));

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public interface Refresh {
        void refreshData(String date);
    }

    public void onClick_btnRefresh(View v) {
        refreshData();
    }

    public void refreshData(){

        orderTable.refreshData(getSelectedDate());
        waitTable.refreshData(getSelectedDate());
    }
    public void onClick_datePicker(View v) {
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.e("datePicker","date = " + year+month+dayOfMonth);
                Calendar selectCal = Calendar.getInstance();
                selectCal.set(year,month,dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (E)");
                String date = sdf.format(selectCal.getTime());
                dateView.setText(date);
                refreshData();


            }
        });
        datePickerDialog.show();
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

    public void onClick_btnSearch(View v){
        JSONObject rcvData = new JSONObject();

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
            if(adapter.getItemCount()==1){
                load_NvChart(adapter.getChartNo(0));
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,recyclerView,new RecyclerTouchListener.ClickListener(){
                @Override
                public void onClick(View view, int position) {
                    selectChart = adapter.getChartNo(position);
                    load_NvChart(selectChart);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

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

        if(chartNo!=null){
            selectChart=chartNo;
            JSONObject rcvData = SendData.getMessage(this,MakeJSONData.get(MsgType.LOAD_NVLIST,Prefer.getPrefString("DB","1"),chartNo).toString());
            try{
                Toasty.error(this,rcvData.getString("Code"),Toasty.LENGTH_LONG).show();
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
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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
        if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(fragment instanceof NvChart){
            /*selectChart = null;
            loadDBList();
            fragment = new MainFrameTable();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frmMain,fragment);
            ft.commit();*/

            sendMessage(MsgType.NVCHART_RELEASE,true);
            if(findViewById(R.id.backgroundImage).getVisibility()==View.VISIBLE){
                super.onBackPressed();

            }
        }
    }
    private int VersionStringToInteger(String version){
        int nVersion;

        String[] splitVersion = version.split("\\.");
        Log.e("version=","v?"+version);
        for(int i=0;i<splitVersion.length;i++){
            Log.e("version=","v?"+splitVersion[i]);
        }
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
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

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

                    /*if(ivLayer2.getVisibility()==View.GONE)
                    {
                        Animation fadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_right_in);
                        ivLayer2.startAnimation(fadeInAnimation);
                        ivLayer2.setVisibility(View.VISIBLE);
                    }*/

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

                    /*if(ivLayer2.getVisibility()==View.VISIBLE)
                    {
                        Animation fadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_left_out);
                        ivLayer2.startAnimation(fadeInAnimation);
                        ivLayer2.setVisibility(View.GONE);
                    }*/
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


}
