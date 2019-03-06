package andcom.nvchart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.evrencoskun.tableview.TableView;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import andcom.nvchart.TableView.MainFrameTable;
import andcom.nvchart.TableView.Order.OrderTable;
import andcom.nvchart.TableView.Wait.WaitTable;
import andcom.nvchart.TableView.holder.OrderTableAdapter;
import andcom.nvchart.TableView.model.CellModel;
import andcom.nvchart.TableView.model.ColumnHeaderModel;
import andcom.nvchart.TableView.model.RowHeaderModel;
import andcom.nvchart.nvChart.NvChart;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
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
    NavigationView navigationView;
    DrawerLayout drawer;
    RelativeLayout bottom_sheet;
    BottomSheetBehavior bottomSheetBehavior;

    //private GestureDetector gesturedetector = null;
    FragmentManager fm2;
    FragmentTransaction ft2;
    Fragment bottomFragment;
    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    OrderTable orderTable;
    WaitTable waitTable;

    TextInputEditText editName,editChartNo;

    public static RecyclerView recyclerViewNvList;
    public static NvListRecyclerAdapter nvListRecyclerAdapter;
    private static JSONObject jNvData;
    public static void setjNvData(@Nullable String db, @Nullable String chartno,@Nullable String nodekey,@Nullable String page){
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
        LinearLayout bottomToolbar = (LinearLayout) findViewById(R.id.bottom_tool_bar);
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


        fragment = new MainFrameTable();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frmMain, fragment);

        ft.commit();




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

    }

    private void loadDBList(){
        JSONObject msg = MakeJSONData.get(MsgType.LOAD_DBLIST);
        JSONObject rcvData = SendData.getMessage(this,msg.toString());
        try{
            rcvData = new JSONObject("{\"User\":\"andcom3\",\"DB\":\"123\"}");
        }catch(Exception e){
            e.printStackTrace();
        }
        LinearLayout ldb = (LinearLayout)findViewById(R.id.layoutDB);
        Button db1 = (Button)findViewById(R.id.btnDB1);
        Button db2 = (Button)findViewById(R.id.btnDB2);
        Button db3 = (Button)findViewById(R.id.btnDB3);

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
                dbBtnSelection(db1);


            }else{  //1개
                dbBtnSelection(db1);

            }

        }catch(JSONException je){
            je.printStackTrace();
        }
    }
    public class dbBtnOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            dbBtnSelection(v);
        }
    }
    private void dbBtnSelection(View v){
        LinearLayout ldb = (LinearLayout)findViewById(R.id.layoutDB);
        Button db1 = (Button)findViewById(R.id.btnDB1);
        Button db2 = (Button)findViewById(R.id.btnDB2);
        Button db3 = (Button)findViewById(R.id.btnDB3);

        db1.setSelected(false);
        db2.setSelected(false);
        db3.setSelected(false);

        View thisBtn = (Button)v;
        thisBtn.setSelected(true);
        Prefer.setPref("DB",thisBtn.getTag().toString());
        setjNvData(thisBtn.getTag().toString(),null,null,null);


        JSONObject msg = MakeJSONData.get(MsgType.LOAD_NVLIST,thisBtn.getTag().toString(),"");

        JSONObject rcvData = SendData.getMessage(context,msg.toString());
        //리스트 만들기

        nvListRecyclerAdapter = new NvListRecyclerAdapter(rcvData.toString());
        recyclerViewNvList = (RecyclerView)findViewById(R.id.recyclerNvList);

        recyclerViewNvList.setAdapter(nvListRecyclerAdapter);
        recyclerViewNvList.setLayoutManager(new LinearLayoutManager(context));
        nvListRecyclerAdapter.notifyDataSetChanged();

        recyclerViewNvList.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerViewNvList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Prefer.setPref("NVLIST",position);
                //setjNvData(null,null,nvListRecyclerAdapter.getNodeKey(position),nvListRecyclerAdapter.getPageCnt(position));
                load_NvChart(selectChart);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        setjNvData(null,null,nvListRecyclerAdapter.getNodeKey(0),nvListRecyclerAdapter.getPageCnt(0));

    }
    public interface Refresh {
        void refreshData();
    }

    public void onClick_btnRefresh(View v) {
        OrderTable orderTable = (OrderTable) pagerAdapter.getItem(1);
        orderTable.refreshData();

        WaitTable waitTable = (WaitTable) pagerAdapter.getItem(0);
        waitTable.refreshData();
    }

    public void onClick_btnSearch(View v){
        JSONObject rcvData = new JSONObject();
        if(!editName.getText().toString().equals("") && editChartNo.getText().toString().equals("")){
            //이름만 넣고 검색
            JSONObject msg = MakeJSONData.get(MsgType.SEARCH_BY_NAME,editName.getText().toString());
            rcvData =  SendData.getMessage(this,msg.toString());


        }else if(!editChartNo.equals("")){
            //차트번호 검색
            JSONObject msg = MakeJSONData.get(MsgType.SEARCH_BY_CHARTNO,editChartNo.getText().toString());
            rcvData =  SendData.getMessage(this,msg.toString());

            //차트번호로 검색
        }else{
            //둘다 공백인채로 눌렀을 때
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

    public void load_NvChart(final String chartNo){
        int nPageCnt = Integer.parseInt(nvListRecyclerAdapter.getPageCnt(Prefer.getPrefInt("NVLIST",0)));
        if(nPageCnt == 0)
            nPageCnt = 1;

        setjNvData(null,chartNo,nvListRecyclerAdapter.getNodeKey(Prefer.getPrefInt("NVLIST",0)),String.valueOf(nPageCnt));


        drawer.closeDrawer(GravityCompat.START, true);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editChartNo.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                fragment = new NvChart();
                Bundle bundle = new Bundle();
                bundle.putString("DATA",getjNvData().toString());
                fragment.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                ft.replace(R.id.frmMain, fragment);
                ft.commit();
            }
        });
        worker.start();
        if(chartNo!=null){
            selectChart=chartNo;
            JSONObject rcvData = SendData.getMessage(this,MakeJSONData.get(MsgType.LOAD_NVLIST,Prefer.getPrefString("DB","1"),chartNo).toString());
            nvListRecyclerAdapter = new NvListRecyclerAdapter(rcvData.toString());
            recyclerViewNvList.setAdapter(nvListRecyclerAdapter);
            nvListRecyclerAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(fragment instanceof NvChart){
            selectChart = null;
            fragment = new MainFrameTable();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.frmMain,fragment);
            ft.commit();
        }
        else {
            super.onBackPressed();
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
