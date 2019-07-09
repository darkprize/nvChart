package andcom.nvchart.nvChart;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jpegkit.Jpeg;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenObjectBase;
import com.samsung.android.sdk.pen.document.SpenObjectContainer;
import com.samsung.android.sdk.pen.document.SpenObjectImage;
import com.samsung.android.sdk.pen.document.SpenObjectLine;
import com.samsung.android.sdk.pen.document.SpenObjectShape;
import com.samsung.android.sdk.pen.document.SpenObjectStroke;
import com.samsung.android.sdk.pen.document.SpenObjectTextBox;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.document.textspan.SpenFontSizeSpan;
import com.samsung.android.sdk.pen.document.textspan.SpenLineSpacingParagraph;
import com.samsung.android.sdk.pen.document.textspan.SpenTextParagraphBase;
import com.samsung.android.sdk.pen.document.textspan.SpenTextSpanBase;
import com.samsung.android.sdk.pen.engine.SpenContextMenu;
import com.samsung.android.sdk.pen.engine.SpenContextMenuItemInfo;
import com.samsung.android.sdk.pen.engine.SpenControlBase;
import com.samsung.android.sdk.pen.engine.SpenControlListener;
import com.samsung.android.sdk.pen.engine.SpenFlickListener;
import com.samsung.android.sdk.pen.engine.SpenLongPressListener;
import com.samsung.android.sdk.pen.engine.SpenSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;
import com.samsung.android.sdk.pen.settingui.SpenSettingPenLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingRemoverLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingSelectionLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingTextLayout;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import andcom.nvchart.ASyncImageSocket;
import andcom.nvchart.ASyncSocket;
import andcom.nvchart.ASyncTextSocket;
import andcom.nvchart.MainActivity;
import andcom.nvchart.MakeJSONData;
import andcom.nvchart.MsgType;
import andcom.nvchart.NvListRecyclerAdapter;
import andcom.nvchart.Prefer;
import andcom.nvchart.R;
import andcom.nvchart.ToolButtonListener;
import andcom.nvchart.util.FadeAnimation;
import andcom.nvchart.util.LoadingFragment;
import andcom.nvchart.util.LoadingProgress;
import andcom.nvchart.util.PushAnimation;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import andcom.nvchart.util.SaveDialog;
import es.dmoral.toasty.Toasty;

public class NvChart extends LoadingFragment implements ToolButtonListener,SpenContextMenu.ContextMenuListener {
    private static final String FRAGMENT_NAME="NVCHART";
    private static final String TAG="NVCHART";
    private final int MODE_PEN = 0;
    private final int MODE_IMG_OBJ = 1;
    private final int MODE_TEXT_OBJ = 2;
    private final int MODE_SELECTION = 3;
    int READ_STORAGE_PERMISSION = 11;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private final int CONTEXT_MENU_PASTE_ID = 10;
    private final int CONTEXT_MENU_CUT_ID = 11;
    private final int CONTEXT_MENU_COPY_ID = 12;
    private final int CONTEXT_MENU_DELETE_ID = 13;
    private final int CONTEXT_MENU_SELECTALL_ID = 13;
    private final int CONTEXT_MENU_CLOSE = 14;


    String state ;
    private int mToolType = SpenSurfaceView.TOOL_SPEN;
    private int mMode = MODE_PEN;

    String cData;

    Button btn,btn2,btn3;
    Button test;

    private Context mContext;
    private Activity activity;
    private SpenNoteDoc mSpenNoteDoc;
    private SpenPageDoc mSpenPageDoc;
    static private SpenSurfaceView mSpenSurfaceView;
    private SpenSettingPenLayout mPenSettingView;
    private ImageView imageView;
    private ImageView keyboard,penBtn,image,marker,penSel,eraser;
    private SpenSettingSelectionLayout mSelectionSettingView;
    private SpenSettingRemoverLayout mEraserSettingView;
    private SpenSettingTextLayout mTextSettingView;
    private LinearLayout progressBar;
    private CardView penOption,textOption;
    private ViewTreeObserver vto;
    static SpenContextMenu menu;
    boolean isSpenFeatureEnabled = false;
    PointF[] points;
    float[] pressures;
    int[] timestamps;
    Rect rect;
    Jpeg jpg;
    int layoutWidth;
    NvData nvData;
    File fileClip;
    int objectCnt,imageIndex;

    float longPressRawX, longPressRawY,longPressX,longPressY;

    static Context context;
    Context mainContext;

    int load=2;
    static long transfer;
    boolean mMearsure = false;

    public static Handler handler;
    GestureDetector gestuerDetector = null;

    SaveDialog saveDialog;

    public String getNodekey() {
        return nodekey;
    }

    public void setNodekey(String nodekey) {
        this.nodekey = nodekey;
        Prefer.setPref("NVLIST",NvListRecyclerAdapter.getInstance().getPageIndex(nodekey));
        Prefer.setPref("NODEKEY",nodekey);
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {

        this.pageNo = Integer.parseInt(pageNo);
    }

    public void setPageNo(int pageNo){
        if(pageNo==0)
            pageNo=1;
        this.pageNo=pageNo;
    }

    public int getPageLastNo() {
        return pageLastNo;
    }

    public void setPageLastNo(int pageLastNo) {
        if(pageLastNo==0){
            pageLastNo=1;
        }
        this.pageLastNo = pageLastNo;
    }

    public String getDB() {
        return DB;
    }

    public void setDB(String DB) {
        this.DB = DB;
    }

    public void setPageIndicator(){
        sendMessage(MsgType.UPDATE_PAGE_NO,getPageNo()+"/"+getPageLastNo());
        new ASyncTextSocket((Activity) mContext, new ASyncTextSocket.AsyncResponse() {
            @Override
            public void processFinish(String msg) {
                try {
                    JSONObject jCustInfo = new JSONObject(msg);
                    JSONArray array = jCustInfo.getJSONArray("List");
                    if(array.length()==1){
                        String name = array.getJSONObject(0).getString("CTNAME");
                        String chartno = array.getJSONObject(0).getString("CTCHARTNO");
                        sendMessage(MsgType.SET_CUST_NAME,"( "+name + " / "+chartno+" )");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(MakeJSONData.get(MsgType.SEARCH_BY_CHARTNO,getChartNo()).toString());

    }

    public String getChartNo() {
        return chartNo;
    }

    public void setChartNo(String chartNo) {
        this.chartNo = chartNo;
    }


    private String chartNo;
    private String DB;
    private String nodekey;
    private int pageNo;
    private int pageLastNo;

    private String bLoadBackImage="1";

    private JSONObject jData;

    public NvChart(){}

    @Override
    public void onSelected(int i) {
        //붙여넣기
        Log.e("onItemSeleted","ID "+i);
        switch (i){
            case CONTEXT_MENU_PASTE_ID :
                ArrayList<SpenObjectBase> items= mSpenNoteDoc.restoreObjectList(fileClip.getAbsolutePath());
                RectF total=new RectF();
                total=items.get(0).getRect();

                for(SpenObjectBase item : items){
                    total.left =    total.left>item.getRect().left? item.getRect().left:total.left;
                    total.top =     total.top>item.getRect().top? item.getRect().top:total.top;
                    total.right =   total.right<item.getRect().right? item.getRect().right:total.right;
                    total.bottom =  total.bottom<item.getRect().bottom? item.getRect().bottom:total.bottom;
                }


                float dx,dy;
                dx=longPressX-total.centerX();
                dy=longPressY-total.centerY();

                for(SpenObjectBase item : items){
                    RectF rect = item.getRect();
                    Log.d("onItemSeleted","rect "+rect);
                    Log.e("onItemSeleted","centerX "+rect.centerX()+" centerY "+rect.centerY());
                    Log.e("onItemSeleted","longPressX "+longPressX+" longPressY "+longPressY);

                    RectF newRect = new RectF(rect.left+dx,rect.top+dy,rect.right+dx,rect.bottom+dy);

                    item.setRect(newRect,false);
                    Log.e("onItemSeleted","rect "+newRect);
                    Log.e("onItemSeleted","centerX "+newRect.centerX()+" centerY "+newRect.centerY());

                    mSpenPageDoc.appendObject(item);
                }
                mSpenPageDoc.selectObject(items);
                mSpenSurfaceView.update();
                menu.close();
                break;

            case CONTEXT_MENU_SELECTALL_ID:
                mSpenPageDoc.selectObject(mSpenPageDoc.getObjectList());
                mSpenSurfaceView.update();

                menu.close();
                break;
            case CONTEXT_MENU_CLOSE :
                menu.close();
                break;
        }
    }

    enum TOOL_NAME {
        TEXT(R.string.TOOL_TEXT),
        PEN(R.string.TOOL_PEN),
        IMAGE(R.string.TOOL_IMAGE),
        MARKER(R.string.TOOL_MARKER),
        STROKE(R.string.TOOL_STROKE),
        COLOR(R.string.TOOL_COLOR),
        SELECT(R.string.TOOL_SELECT),
        ERASER(R.string.TOOL_ERASER);

        private int resourceId;

        private TOOL_NAME(int id)  {
            resourceId = id;
        }

        @Override
        public String toString() {
            return NvChart.context.getString(resourceId);

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"OnCreate call");


        /*this.cData = getArguments().getString("DATA");

        try{
            jData = new JSONObject(cData);
            setChartNo(jData.getString("ND_CHARTNO"));
            setDB(jData.getString("DB"));
            setNodekey(jData.getString("ND_NODEKEY"));
            setPageNo(jData.getString("ND_PAGENO"));
            setPageLastNo(NvListRecyclerAdapter.getInstance().getPageCnt(jData.getString("ND_NODEKEY")));

            setPageIndicator();

        }catch (JSONException je ){
            je.printStackTrace();
        }*/

    }
    private void setJData(String jdata){
        try{
            jData = new JSONObject(jdata);


            //setChartNo(jData.getString("ND_CHARTNO"));
            //setDB(jData.getString("DB"));
            Log.e("setJData",jData.getString("ND_NODEKEY") + getNodekey());
            if(jData.getString("ND_NODEKEY").equals(getNodekey())){
                bLoadBackImage = "0";
            }else{
                bLoadBackImage = "1";
            }
            //setNodekey(jData.getString("ND_NODEKEY"));
            //setPageNo(jData.getString("ND_PAGENO"));

            confirmSaveBeforeRefresh(jData.getString("ND_CHARTNO"),
                    jData.getString("DB"),
                    jData.getString("ND_NODEKEY"),
                    Integer.parseInt(jData.getString("ND_PAGENO")));


            //setPageLastNo(NvListRecyclerAdapter.getInstance().getPageCnt(jData.getString("ND_NODEKEY")));
            //refreshChart();

        }catch (JSONException je){
            je.printStackTrace();
        }
    }
    public void refreshChart(){

        try{
            //
            imageIndex =1;
            NvChartAsyncTask task = new NvChartAsyncTask(mSpenSurfaceView,mSpenPageDoc,context);

            File nvFile = new File(context.getFilesDir()+File.separator+getDB()+getNodekey()+".jpg");
            if(nvFile.canRead() ){
                bLoadBackImage = "0";
            }else{
                bLoadBackImage = "1";
            }
            jData.put("ND_CHARTNO",getChartNo());
            jData.put("DB",getDB());
            jData.put("ND_NODEKEY",getNodekey());
            jData.put("ND_PAGENO",getPageNo());
            jData.put("BACK_IMAGE",bLoadBackImage);

            setPageLastNo(NvListRecyclerAdapter.getInstance().getPageCnt(getNodekey()));
            Log.w("lastNo","asd"+NvListRecyclerAdapter.getInstance().getPageCnt(getNodekey()));
            NvListRecyclerAdapter.getInstance().setSeletedPosition(NvListRecyclerAdapter.getInstance().getPageIndex(getNodekey()));
            NvListRecyclerAdapter.getInstance().notifyDataSetChanged();
            //loadNvData(jData.toString());
            task.execute(jData.toString());

            setPageIndicator();


        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    private void confirmSaveBeforeRefresh(final String chartNo_,final String db_,final String nodekey_,final int pageno_){

        //순서
        //바뀐거 있는지 확인
        //true:다이얼로그 띄움->선택에 따라 진행
        //false:그대로 진행

        //refresh_normal 할 경우-> 차트넘버,db,nodekey,pageno,
        if(isChanged()){

            /*AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("저장 확인");
            dialog.setMessage("변경된 내용이 있습니다. 저장하시겠습니까?");
            dialog.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveChart();

                    setChartNo(chartNo_);
                    setDB(db_);
                    setNodekey(nodekey_);
                    setPageNo(pageno_);

                    refreshChart();
                }
            });
            dialog.setNeutralButton("저장 안함", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    setChartNo(chartNo_);
                    setDB(db_);
                    setNodekey(nodekey_);
                    setPageNo(pageno_);

                    refreshChart();
                }
            });
            dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            dialog.setCancelable(false);
            //dialog.show();*/


            saveDialog = new SaveDialog(mContext, new View.OnClickListener() {
                @Override
                public void onClick(View v) {   //저장
                    Log.e("SaveDialog","저장 버튼 클릭");
                    SaveChartAsync saveChartAsync = new SaveChartAsync(new AsyncAfter() {
                        @Override
                        public void afterExcute() {
                            setChartNo(chartNo_);
                            setDB(db_);
                            setNodekey(nodekey_);
                            setPageNo(pageno_);

                            refreshChart();
                            saveDialog.dismiss();

                        }
                    });
                    saveChartAsync.execute();



                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {   //저장안함
                    Log.e("SaveDialog","저장안함 버튼 클릭");

                    setChartNo(chartNo_);
                    setDB(db_);
                    setNodekey(nodekey_);
                    setPageNo(pageno_);

                    refreshChart();
                    saveDialog.dismiss();


                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {   //취소
                    Log.e("SaveDialog","취소 버튼 클릭");
                    saveDialog.dismiss();
                    return;
                }
           });
           saveDialog.show();
        }else{

            setChartNo(chartNo_);
            setDB(db_);
            setNodekey(nodekey_);
            setPageNo(pageno_);

            refreshChart();
        }

    }
    public int indexOf(byte[] outerArray, byte[] smallerArray,int from) {
        for(int i = from; i < outerArray.length - smallerArray.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i+j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG,"OnActivityCreated call");

        initView();

        handler = new UIHandler();
        this.context = getContext();
/*
        BaseFrameLayout asd = (BaseFrameLayout)getView().findViewById(R.id.spenViewContainer);

        final ViewTreeObserver observer = asd.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Log.e("SurfaceViewSize","width "+mSpenSurfaceView.getWidth()+" height "+mSpenSurfaceView.getHeight());
                loadNvData(cData);
                observer.removeOnGlobalLayoutListener(this);
            }
        });*/


    }

    private void close_penOption(View v){

        if(penOption.getVisibility() == View.VISIBLE){
            FadeAnimation a = new FadeAnimation(penOption,v, 500, PushAnimation.COLLAPSE);
            penOption.startAnimation(a);
        }
        if(textOption.getVisibility() == View.VISIBLE){
            FadeAnimation a = new FadeAnimation(penOption,v, 500, PushAnimation.COLLAPSE);
            textOption.startAnimation(a);
        }
    }
    public void onClicked(View v){
        String tag = v.getTag().toString();


        Log.e("NvChart", tag + " clicked");
        if(mSpenSurfaceView==null || mSpenPageDoc==null)
            return;

        LinearLayout vp = (LinearLayout) v.getParent();
        if(vp instanceof LinearLayout && vp.getTag() != null) {
            if (vp.getTag().equals("top_tool_layout")) {

                for (int i = 0; i < vp.getChildCount(); i++) {
                    vp.getChildAt(i).setSelected(false);
                }
                penOption.setVisibility(View.GONE);
                textOption.setVisibility(View.GONE);

                RelativeLayout relativeLayout = (RelativeLayout) penOption.getChildAt(0);
                LinearLayout temp = (LinearLayout) relativeLayout.getChildAt(0);
                LinearLayout stroke = (LinearLayout) temp.getChildAt(1);
                LinearLayout color = (LinearLayout) temp.getChildAt(0);

                TextView close = (TextView) relativeLayout.getChildAt(1);


                textOption.setVisibility(View.GONE);

                RelativeLayout trelativeLayout = (RelativeLayout) textOption.getChildAt(0);
                LinearLayout ttemp = (LinearLayout) trelativeLayout.getChildAt(0);
                LinearLayout tcolor = (LinearLayout) ttemp.getChildAt(0);

                TextView tclose = (TextView) trelativeLayout.getChildAt(1);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FadeAnimation a = new FadeAnimation(penOption, v, 500, PushAnimation.COLLAPSE);
                        penOption.startAnimation(a);
                    }
                });
                tclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FadeAnimation a = new FadeAnimation(textOption, v, 500, PushAnimation.COLLAPSE);
                        textOption.startAnimation(a);
                    }
                });

                for (int i = 0; i < stroke.getChildCount(); i++) {
                    stroke.getChildAt(i).setSelected(false);
                }
                for (int i = 0; i < color.getChildCount(); i++) {
                    color.getChildAt(i).setSelected(false);
                }

                close_penOption(null);

                if (TOOL_NAME.TEXT.toString().equals(tag)) {
                    Log.e("NvChart", tag + " clicked");
                    v.setSelected(true);
                    Prefer.setPref("PenMode", SpenSurfaceView.ACTION_TEXT);

                    mMode = MODE_TEXT_OBJ;

                    if(textOption.getVisibility() != View.VISIBLE){
                        FadeAnimation a = new FadeAnimation(textOption,v,500,PushAnimation.EXPAND);
                        textOption.startAnimation(a);

                        for (int i = 0; i < tcolor.getChildCount(); i++) {
                            ImageButton btnColor = (ImageButton) tcolor.getChildAt(i);

                            btnColor.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LinearLayout vp = (LinearLayout) v.getParent();
                                    for (int i = 0; i < vp.getChildCount(); i++) {
                                        vp.getChildAt(i).setSelected(false);
                                    }
                                    v.setSelected(true);

                                    int textColor = NvConstant.getColor(3, Integer.parseInt(v.getTag().toString()));
                                    Prefer.setPref("TextColor", textColor);
                                    Prefer.setPref("TextColorTag", v.getTag().toString());


                                }
                            });
                            if (btnColor.getTag().toString().equals(Prefer.getPrefString("TextColorTag", "0"))) {
                                btnColor.setSelected(true);
                            }
                        }
                    }

                }
                if (TOOL_NAME.PEN.toString().equals(tag)) {
                    Log.e("NvChart", tag + " clicked");
                    v.setSelected(true);
                    Prefer.setPref("PenMode", SpenSurfaceView.ACTION_STROKE);
                    Prefer.setPref("PenName", "com.samsung.android.sdk.pen.pen.preload.InkPen");
                    mMode = MODE_PEN;


                    if (penOption.getVisibility() != View.VISIBLE) {
                        FadeAnimation a = new FadeAnimation(penOption, v, 500, PushAnimation.EXPAND);
                        penOption.startAnimation(a);


                        for (int i = 0; i < stroke.getChildCount(); i++) {
                            ImageButton btnStroke = (ImageButton) stroke.getChildAt(i);

                            btnStroke.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.w("click", "" + v.getTag().toString());
                                    LinearLayout vp = (LinearLayout) v.getParent();
                                    for (int i = 0; i < vp.getChildCount(); i++) {
                                        vp.getChildAt(i).setSelected(false);
                                    }
                                    v.setSelected(true);

                                    float penSize = NvConstant.getPenSize(NvConstant.PEN, Integer.parseInt(v.getTag().toString()));
                                    Prefer.setPref("PenSize", penSize);
                                    Prefer.setPref("PenSizeTag", v.getTag().toString());
                                    setPenInfo();
                                }
                            });
                            if (btnStroke.getTag().toString().equals(Prefer.getPrefString("PenSizeTag", "1"))) {
                                btnStroke.setSelected(true);
                            }
                        }
                        for (int i = 0; i < color.getChildCount(); i++) {
                            ImageButton btnColor = (ImageButton) color.getChildAt(i);

                            btnColor.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LinearLayout vp = (LinearLayout) v.getParent();
                                    for (int i = 0; i < vp.getChildCount(); i++) {
                                        vp.getChildAt(i).setSelected(false);
                                    }
                                    v.setSelected(true);

                                    int penColor = NvConstant.getColor(NvConstant.PEN, Integer.parseInt(v.getTag().toString()));
                                    Prefer.setPref("PenColor", penColor);
                                    Prefer.setPref("PenColorTag", v.getTag().toString());


                                    setPenInfo();
                                }
                            });
                            if (btnColor.getTag().toString().equals(Prefer.getPrefString("PenColorTag", "0"))) {
                                btnColor.setSelected(true);
                            }
                        }

                    }


                }
                if (TOOL_NAME.MARKER.toString().equals(tag)) {
                    Log.e("NvChart", tag + " clicked");
                    Prefer.setPref("PenMode", SpenSurfaceView.ACTION_STROKE);
                    Prefer.setPref("PenName", "com.samsung.android.sdk.pen.pen.preload.Marker");
                    v.setSelected(true);
                    mMode = MODE_PEN;

                    if (penOption.getVisibility() != View.VISIBLE) {
                        FadeAnimation a = new FadeAnimation(penOption, v, 500, PushAnimation.EXPAND);
                        penOption.startAnimation(a);


                        for (int i = 0; i < stroke.getChildCount(); i++) {
                            ImageButton btnStroke = (ImageButton) stroke.getChildAt(i);

                            btnStroke.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.w("click", "" + v.getTag().toString());
                                    LinearLayout vp = (LinearLayout) v.getParent();
                                    for (int i = 0; i < vp.getChildCount(); i++) {
                                        vp.getChildAt(i).setSelected(false);
                                    }
                                    v.setSelected(true);

                                    float penSize = NvConstant.getPenSize(2, Integer.parseInt(v.getTag().toString()));
                                    Prefer.setPref("MarkerSize", penSize);
                                    Prefer.setPref("MarkerSizeTag", v.getTag().toString());
                                    setPenInfo();
                                }
                            });
                            if (btnStroke.getTag().toString().equals(Prefer.getPrefString("MarkerSizeTag", "1"))) {
                                btnStroke.setSelected(true);
                            }
                        }
                        for (int i = 0; i < color.getChildCount(); i++) {
                            ImageButton btnColor = (ImageButton) color.getChildAt(i);

                            btnColor.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LinearLayout vp = (LinearLayout) v.getParent();
                                    for (int i = 0; i < vp.getChildCount(); i++) {
                                        vp.getChildAt(i).setSelected(false);
                                    }
                                    v.setSelected(true);

                                    int penColor = NvConstant.getColor(2, Integer.parseInt(v.getTag().toString()));
                                    Prefer.setPref("MarkerColor", penColor);
                                    Prefer.setPref("MarkerColorTag", v.getTag().toString());


                                    setPenInfo();
                                }
                            });
                            if (btnColor.getTag().toString().equals(Prefer.getPrefString("MarkerColorTag", "0"))) {
                                btnColor.setSelected(true);
                            }
                        }

                    }


                }
                if (TOOL_NAME.IMAGE.toString().equals(tag)) {
                    Log.e("NvChart", tag + " clicked");
                    v.setSelected(true);
                    Prefer.setPref("PenMode", SpenSurfaceView.ACTION_SELECTION);
                    mMode = MODE_IMG_OBJ;
                    PickSetup setup = new PickSetup()
                            .setTitle("이미지 불러오기")
                            .setProgressText("수행할 작업 선택")
                            .setCancelText("닫기")
                            .setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
                            .setCameraButtonText("카메라")
                            .setGalleryButtonText("갤러리")
                            .setButtonOrientation(LinearLayoutCompat.HORIZONTAL)
                            .setSystemDialog(false).setMaxSize(1000);

                    PickImageDialog.build(setup).setOnPickResult(new IPickResult() {
                        @Override
                        public void onPickResult(PickResult pickResult) {
                            //////////////////////////////////
                            try {
                                File fNode = new File(context.getFilesDir()+File.separator+"testImage.jpg");
                                if(!fNode.canRead()){
                                    try {
                                        fNode.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                byte[] backImageData = bitmapToByteArray(pickResult.getBitmap());
                                FileOutputStream fos = new FileOutputStream(fNode);
                                fos.write(backImageData);
                                fos.flush();
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //////////////////////////////////
                            SpenObjectImage image = new SpenObjectImage();
                            Jpeg jpg = new Jpeg(bitmapToByteArray(pickResult.getBitmap()));

                            RectF rectF = new RectF(0,0,mSpenSurfaceView.getWidth()*0.5f,jpg.getHeight()*0.5f);
                            image.setImage(pickResult.getBitmap());
                            image.setRect(rectF,true);
                            String cImageIndex = String.format("%03d",imageIndex);
                            image.setText("new:"+cImageIndex);
                            mSpenPageDoc.appendObject(image);
                            mSpenSurfaceView.update();

                        }
                    }).show((FragmentActivity) mContext);

                }
                if (TOOL_NAME.STROKE.toString().equals(tag)) {
                    Log.e("NvChart", tag + " clicked");
                    v.setSelected(true);    //사용x

                }
                if (TOOL_NAME.COLOR.toString().equals(tag)) {
                    Log.e("NvChart", tag + " clicked");
                    v.setSelected(true);

                }
                if (TOOL_NAME.SELECT.toString().equals(tag)) {
                    Log.e("NvChart", tag + " clicked");
                    v.setSelected(true);
                    Prefer.setPref("PenMode", SpenSurfaceView.ACTION_SELECTION);

                    mMode = MODE_SELECTION;

                }
                if (TOOL_NAME.ERASER.toString().equals(tag)) {
                    Log.e("NvChart", tag + " clicked");
                    Prefer.setPref("PenMode", SpenSurfaceView.ACTION_STROKE_REMOVER);
                    v.setSelected(true);

                }

            }
        }

        if(tag.equals("first")){
            confirmSaveBeforeRefresh(getChartNo(),getDB(),getNodekey(),1);
            bLoadBackImage ="1";
        }
        if(tag.equals("prev")){
            //이전 페이지
            if(getPageNo()>1){
                bLoadBackImage ="1";
                confirmSaveBeforeRefresh(getChartNo(),getDB(),getNodekey(),getPageNo()-1);
            }

        }
        if(tag.equals("next")){
            if(getPageNo()<getPageLastNo()){
                bLoadBackImage ="1";
                confirmSaveBeforeRefresh(getChartNo(),getDB(),getNodekey(),getPageNo()+1);
            }
        }
        if(tag.equals("last")){
            bLoadBackImage ="1";
            confirmSaveBeforeRefresh(getChartNo(),getDB(),getNodekey(),getPageLastNo());

        }



        if(tag.equals("undo")){
            Log.w("NvChart","click undo");
            if(mSpenPageDoc.isUndoable()){
                SpenPageDoc.HistoryUpdateInfo[] userData = mSpenPageDoc.undo();
                mSpenSurfaceView.updateUndo(userData);
            }
        }

        if(tag.equals("redo")){
            Log.w("NvChart","click redo");
            if(mSpenPageDoc.isRedoable()){
                SpenPageDoc.HistoryUpdateInfo[] userData = mSpenPageDoc.redo();
                mSpenSurfaceView.updateRedo(userData);
            }
        }
        if(tag.equals("new")){
            Log.w("NvChart","click new");

            AlertDialog.Builder newPageDialog = new AlertDialog.Builder(mContext);
            newPageDialog.setTitle("새로운 페이지");
            newPageDialog.setMessage("새로운 페이지를 추가하시겠습니까?");
            newPageDialog.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        addNewPage();
                        ASyncTextSocket socket = new ASyncTextSocket((Activity) mContext );
                        String rcvMsg = socket.execute(MakeJSONData.get(MsgType.LOAD_NVLIST,getDB(),getChartNo()).toString()).get();
                        JSONObject rcvData = new JSONObject(rcvMsg);
                        NvListRecyclerAdapter.getInstance(rcvMsg);
                        NvListRecyclerAdapter.getInstance().notifyDataSetChanged();
                        /*JSONArray list = rcvData.getJSONArray("List");
                        for(int i = 0; i<list.length();i++){
                            Log.w("addNewPage",list.getJSONObject(i).getString("NS_NODEKEY")+" , " + getNodekey());

                            if(list.getJSONObject(i).getString("NS_NODEKEY").equals(getNodekey())){
                                Log.w("addNewPage",list.getJSONObject(i).getString("ND_PAGE_CNT"));
                                setPageLastNo(Integer.parseInt(list.getJSONObject(i).getString("ND_PAGE_CNT")));
                            }
                        }*/
                        //setPageNo(NvListRecyclerAdapter.getInstance().getPageCnt(getNodekey()));
                        confirmSaveBeforeRefresh(getChartNo(),getDB(),getNodekey(),NvListRecyclerAdapter.getInstance().getPageCnt(getNodekey()));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            newPageDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            newPageDialog.show();
            //setPageNo(getPageLastNo());

        }
        if(tag.equals("save")){
            Log.w("NvChart","click save");

            SaveChartAsync saveChartAsync = new SaveChartAsync();
            saveChartAsync.execute();
        }
        setPenInfo();
        mSpenSurfaceView.update();

    }
    private void addNewPage(){
        try {
            JSONObject sendData = MakeJSONData.get(MsgType.SAVE_NVCHART,getDB(),getChartNo(),getNodekey(),String.valueOf(getPageLastNo()+1));
            String cSendData = sendData.toString(1).replace("\\/","/")+"\n";
            byte[] bSendData = cSendData.getBytes("EUC-KR");
            bSendData = appendByte(bSendData,"---AndcomData_END---".getBytes("EUC-KR"));
            ASyncImageSocket socket = new ASyncImageSocket((Activity)mContext,Prefer.getPrefString("key_ip",""),Integer.parseInt(Prefer.getPrefString("key_port", "80")));
            socket.execute(bSendData).get();
            sendMessage(MsgType.LOAD_DBLIST,"");
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (UnsupportedEncodingException uee){
            uee.printStackTrace();
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }catch (ExecutionException ee){
            ee.printStackTrace();
        }

    }
    private void setPenInfo(){
        mSpenSurfaceView.closeControl();

        mSpenSurfaceView.setToolTypeAction(mToolType, Prefer.getPrefInt("PenMode",SpenSurfaceView.ACTION_STROKE));
        SpenSettingPenInfo mPenInfo = new SpenSettingPenInfo();
        String penName = Prefer.getPrefString("PenName","com.samsung.android.sdk.pen.pen.preload.InkPen");
        mPenInfo.name = penName;
        mPenInfo.size = Prefer.getPrefFloat("PenSize",NvConstant.PEN_SIZE_1);
        mPenInfo.color = Prefer.getPrefInt("PenColor",NvConstant.PEN_BLACK);
        mPenInfo.sizeLevel =0;
        mPenInfo.isCurvable = false;


        SpenSettingPenInfo mMarkerInfo = new SpenSettingPenInfo();
        mMarkerInfo.name = penName;
        mMarkerInfo.size = Prefer.getPrefFloat("MarkerSize",NvConstant.PEN_SIZE_1);
        mMarkerInfo.color = Prefer.getPrefInt("MarkerColor",NvConstant.PEN_BLACK);

        Log.w("penColor",penName+ "="+mPenInfo.color);
        Log.w("penColor",penName+ "="+penName.contains("InkPen"));

        if(penName.contains("InkPen")){
            mSpenSurfaceView.setPenSettingInfo(mPenInfo);
            Log.w("penColor",penName+ "="+mPenInfo.color);

        }else{
            mSpenSurfaceView.setPenSettingInfo(mMarkerInfo);

        }
        Log.w("penColor",penName+ "="+mPenInfo.color);
        Log.w("penColor2","="+mSpenSurfaceView.getPenSettingInfo().color);


    }
    private void initSpen(final NvData nvData){

        // Get the dimensions of the screen.
        Display display = activity.getWindowManager().getDefaultDisplay();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ File.separator+"test2.jpg");
        try{
            jpg = nvData.getBackImageJpg();
            rect = new Rect();

            rect.set(0,0,jpg.getWidth(),jpg.getHeight());
        }catch (Exception ie){
            ie.printStackTrace();
        }

        // Create SpenNoteDoc.
        try {
            mSpenNoteDoc =
                    new SpenNoteDoc(mContext, rect.width(), rect.height());
        } catch (IOException e) {
            Log.e("initSpen","error1");
            Toast.makeText(mContext, "Cannot create new NoteDoc.",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // After adding a page to NoteDoc, get an instance and set it
        // as a member variable.
        mSpenPageDoc = mSpenNoteDoc.appendPage();
        //mSpenPageDoc.setBackgroundColor(Color.TRANSPARENT);
        //mSpenPageDoc.clearHistory();

        // Set PageDoc to View.
        mSpenSurfaceView.setPageDoc(mSpenPageDoc,true);

        //initPenSettingInfo();



        if(isSpenFeatureEnabled == false) {
            mToolType = SpenSurfaceView.TOOL_FINGER;
            Toast.makeText(mContext,
                    "Device does not support Spen. \n You can draw stroke by finger.",
                    Toast.LENGTH_SHORT).show();
        }else {
            mToolType = SpenSurfaceView.TOOL_SPEN;
        }
        mSpenPageDoc.setBackgroundImage(nvData.getBackImageFile().getPath());
        mSpenPageDoc.setBackgroundImageMode(SpenPageDoc.BACKGROUND_IMAGE_MODE_FIT);

        Log.e("SurfaceViewSize","width "+mSpenSurfaceView.getWidth()+" height "+mSpenSurfaceView.getHeight());
        Log.e("rect","width "+rect.width());
        //Log.e("mSpenSurfaceView.getWidth()/rect.width()","= "+(float)((float)mSpenSurfaceView.getWidth()/(float)rect.width()));

        mSpenSurfaceView.setZoom(rect.width()/2,rect.height()/2,((float)mSpenSurfaceView.getWidth()/(float)rect.width()));

        mSpenSurfaceView.update();

    }
    private boolean isChanged(){
        if(mSpenPageDoc == null || !mSpenPageDoc.isObjectLoaded()){
            return false;
        }
        ArrayList<SpenObjectBase> objects = mSpenPageDoc.getObjectList();
        boolean isChange=objects.size() != objectCnt;
        Log.w("saveChart","size="+objects.size());

        for(SpenObjectBase objectBase : objects){
            if(objectBase.isChanged()){
                isChange=true;
                //break;
            }
        }
        return isChange;
    }
    interface AsyncAfter {
        void afterExcute();
    }
    private class SaveChartAsync extends AsyncTask<Void,String,byte[]> implements AsyncAfter{
        AsyncAfter delegate=null;
        SaveChartAsync(){}
        SaveChartAsync(AsyncAfter delegate){
            this.delegate = delegate;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoadingProgress.getInstance().progressON((Activity) context,"변경 사항을 저장 합니다.");
        }
        @Override
        protected byte[] doInBackground(Void... voids) {
            byte[] bSendData = new byte[0];
            try {

                JSONObject sendData = MakeJSONData.get(MsgType.SAVE_NVCHART,getDB(),getChartNo(),getNodekey(),String.valueOf(getPageNo()));
                String cSendData = new String();

                JSONArray arrStrokes = new JSONArray();
                JSONArray arrTextboxes = new JSONArray();
                JSONArray arrImage = new JSONArray();
                ArrayList<Bitmap> bitmaps = new ArrayList<>();
                int imageIndex = 1;
                ArrayList<SpenObjectBase> objects = mSpenPageDoc.getObjectList();


                if(!isChanged()){
                    //Toasty.info(context,"변경 사항이 없습니다.").show();
                    Log.w("saveChart","저장할 내용이 없음");

                    sendMessageMe(MsgType.TOAST_MSG,"변경 사항이 없습니다.");
                    return null;
                }
                for(SpenObjectBase objectBase : objects){

                    if(objectBase.getType()==SpenPageDoc.FIND_TYPE_STROKE){
                        SpenObjectStroke stroke = (SpenObjectStroke)objectBase;
                        String cStroke = new String();
                        String penMode = stroke.getPenName().contains("InkPen") ? "1" : "2";
                        String penSize = new String();
                        String penColor = new String();
                        if(penMode.equals("1")){
                            penSize = NvConstant.getPenSizeCode(penMode,stroke.getPenSize());
                            penColor= NvConstant.getPenColorCode(stroke.getColor());

                            Log.w("pen Color","color "+stroke.getColor() + " ->" + penColor);

                        }else{
                            penSize = NvConstant.getPenSizeCode(penMode,stroke.getPenSize());
                            penColor= NvConstant.getPenColorCode(stroke.getColor());
                        }
                        cStroke = getDecToHex(penMode+penSize,2)+getDecToHex(penColor,2);       //4자리 헤더 생성


                        float[] pointsX = stroke.getXPoints();
                        float[] pointsY = stroke.getYPoints();
                        Log.e("saveChart","stroke");

                        for(int i =0 ; i < pointsX.length ; i++){
                            float tempX = pointsX[i]*NvData.ScaleWidth/jpg.getWidth()+NvData.ScaleLeft;
                            float tempY = pointsY[i]*NvData.ScaleHeight/jpg.getHeight()+NvData.ScaleTop;
                            Log.d("saveChart","X="+tempX+", Y="+tempY);
                            cStroke +=getDecToHex(String.valueOf((long)tempX),4);
                            cStroke +=getDecToHex(String.valueOf((long)tempY),4);
                        }
                        JSONObject jStroke = new JSONObject();
                        jStroke.put("BYTE",cStroke);
                        arrStrokes.put(jStroke);

                    }
                    if(objectBase.getType()==SpenPageDoc.FIND_TYPE_TEXT_BOX && objectBase.isSelectable() ){
                        SpenObjectTextBox textBox = (SpenObjectTextBox) objectBase;
                        JSONObject jTextBox = new JSONObject();
                        jTextBox.put("COLOR",NvConstant.getTextColorCode(textBox.getTextColor()));
                        RectF rect = textBox.getRect();
                        String desc = textBox.getText();
                        int count=1;
                        int i=0;

                        if(desc==null || desc.equals("")){
                            continue;
                        }
                        while((i=desc.indexOf("\n",i+1))!=-1){
                            count++;
                        }

                        rect.bottom = (41+nvData.ScaleHeight/69*count);

                        jTextBox.put("POSITION",nvData.getOriginPosition(rect,NvConstant.TEXT_OBJ));

                        jTextBox.put("DESC",textBox.getText().replace("\n","\r\n"));

                        arrTextboxes.put(jTextBox);
                    }
                    if(objectBase.getType()==SpenObjectBase.TYPE_IMAGE ){

                        SpenObjectImage imageObject = (SpenObjectImage) objectBase;
                        JSONObject jImage = new JSONObject();
                        RectF rect = imageObject.getRect();
                        jImage.put("POSITION",nvData.getOriginPosition(rect,NvConstant.IMAGE_OBJ));
                        String cImageIndex = String.format("%03d",imageIndex);
                        jImage.put("INDEX",cImageIndex);
                        imageIndex++;
                        bitmaps.add(imageObject.getImage());
                        arrImage.put(jImage);

                    }
                }
                sendData.put("PenData",arrStrokes);
                sendData.put("TextBox",arrTextboxes);
                sendData.put("Image",arrImage);

                JSONArray image = sendData.getJSONArray("Image");
                cSendData = sendData.toString(1).replace("\\/","/")+"\n";

                bSendData = new byte[0];
                bSendData = cSendData.getBytes("EUC-KR");
                //bSendData = appendByte(bSendData,nvData.tempImage);   //받았던거 그대로 보내기
                //Log.e("temp Length","길이 "+nvData.tempImage.length);
                for(int i=0;i<bitmaps.size();i++){
                    Log.e("Index","index = "+ i);
                    String index = image.getJSONObject(i).getString("INDEX");
                    bSendData = appendByte(bSendData,("<---AndcomData_Image"+index+"---\r\n").getBytes("EUC-KR")) ;

                    bSendData = appendByte(bSendData,bitmapToByteArray(bitmaps.get(i)));
                    ////////////////////////
                    /*try {
                        File fNode = new File(context.getFilesDir()+File.separator+"testImage.jpg");
                        if(!fNode.canRead()){
                            try {
                                fNode.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        byte[] backImageData = bitmapToByteArray(bitmaps.get(i));
                        FileOutputStream fos = new FileOutputStream(fNode);
                        fos.write(backImageData);
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
*/
                    ////////////////////////
                    //bSendData = appendByte(bSendData,new byte[]{1});
                    bSendData = appendByte(bSendData,"\r\n".getBytes("EUC-KR"));

                    bSendData = appendByte(bSendData,("---AndcomData_Image"+index+"--->\r\n\r\n").getBytes("EUC-KR"));
                }
                bSendData = appendByte(bSendData,"---AndcomData_END---".getBytes("EUC-KR"));

                cSendData = new String(bSendData);
                int maxLogSize = 1000;
                for(int i = 0; i <= cSendData.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i+1) * maxLogSize;
                    end = end > cSendData.length() ? cSendData.length() : end;
                    Log.v("SendData", cSendData.substring(start, end));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } finally {
            }
            return bSendData;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //LoadingProgress.getInstance().progressSET(values[0]);
        }
        @Override
        protected void onPostExecute(byte[] bData) {
            super.onPostExecute(bData);
            ASyncImageSocket socket = new ASyncImageSocket((Activity) mContext, Prefer.getPrefString("key_ip", ""), Integer.parseInt(Prefer.getPrefString("key_port", "80")), new ASyncImageSocket.AsyncResponse() {
                @Override
                public void processFinish(String response) {
                    if(response.contains("1")){

                        //Toasty.info(context,"저장 성공").show();
                        sendMessageMe(MsgType.TOAST_MSG,"저장 성공");
                        mSpenPageDoc.clearChangedFlag();
                        objectCnt = mSpenPageDoc.getObjectCount(false);
                        if(delegate!=null){
                            delegate.afterExcute();

                        }
                    }else{
                        JSONObject jResult = null;
                        try {
                            jResult = new JSONObject(response);
                            //Toasty.error(context,"저장 실패. 다시 시도해주세요."+"\r\n원인:"+jResult.getString("Msg")).show();
                            sendMessageMe(MsgType.TOAST_MSG,"저장 실패. 다시 시도해주세요."+"\r\n원인:"+jResult.getString("Msg"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }
            });
            if(bData !=null){
                socket.execute(bData);

            }else{
                LoadingProgress.getInstance().progressOFF();
            }
        }
        @Override
        public void afterExcute(){

        }
    }
    /*@SuppressLint("StaticFieldLeak")
    private void saveChart(){
        AsyncTask saveChart = new AsyncTask<Void,String,byte[]>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                LoadingProgress.getInstance().progressON((Activity) context,"변경 사항을 저장 합니다.");
            }
            @Override
            protected byte[] doInBackground(Void... voids) {
                byte[] bSendData = new byte[0];
                try {

                    JSONObject sendData = MakeJSONData.get(MsgType.SAVE_NVCHART,getDB(),getChartNo(),getNodekey(),String.valueOf(getPageNo()));
                    String cSendData = new String();

                    JSONArray arrStrokes = new JSONArray();
                    JSONArray arrTextboxes = new JSONArray();
                    JSONArray arrImage = new JSONArray();
                    ArrayList<Bitmap> bitmaps = new ArrayList<>();
                    int imageIndex = 0;
                    ArrayList<SpenObjectBase> objects = mSpenPageDoc.getObjectList();


                    if(!isChanged()){
                        //Toasty.info(context,"변경 사항이 없습니다.").show();
                        Log.w("saveChart","저장할 내용이 없음");

                        sendMessageMe(MsgType.TOAST_MSG,"변경 사항이 없습니다.");
                        return null;
                    }
                    for(SpenObjectBase objectBase : objects){

                        if(objectBase.getType()==SpenPageDoc.FIND_TYPE_STROKE){
                            SpenObjectStroke stroke = (SpenObjectStroke)objectBase;
                            String cStroke = new String();
                            String penMode = stroke.getPenName().contains("InkPen") ? "1" : "2";
                            String penSize = new String();
                            String penColor = new String();
                            if(penMode.equals("1")){
                                penSize = NvConstant.getPenSizeCode(penMode,stroke.getPenSize());
                                penColor= NvConstant.getPenColorCode(stroke.getColor());

                                Log.w("pen Color","color "+stroke.getColor() + " ->" + penColor);

                            }else{
                                penSize = NvConstant.getPenSizeCode(penMode,stroke.getPenSize());
                                penColor= NvConstant.getPenColorCode(stroke.getColor());
                            }
                            cStroke = getDecToHex(penMode+penSize,2)+getDecToHex(penColor,2);       //4자리 헤더 생성


                            float[] pointsX = stroke.getXPoints();
                            float[] pointsY = stroke.getYPoints();
                            Log.e("saveChart","stroke");

                            for(int i =0 ; i < pointsX.length ; i++){
                                float tempX = pointsX[i]*NvData.ScaleWidth/jpg.getWidth()+NvData.ScaleLeft;
                                float tempY = pointsY[i]*NvData.ScaleHeight/jpg.getHeight()+NvData.ScaleTop;
                                Log.d("saveChart","X="+tempX+", Y="+tempY);
                                cStroke +=getDecToHex(String.valueOf((long)tempX),4);
                                cStroke +=getDecToHex(String.valueOf((long)tempY),4);
                            }
                            JSONObject jStroke = new JSONObject();
                            jStroke.put("BYTE",cStroke);
                            arrStrokes.put(jStroke);

                        }
                        if(objectBase.getType()==SpenPageDoc.FIND_TYPE_TEXT_BOX && objectBase.isSelectable() ){
                            SpenObjectTextBox textBox = (SpenObjectTextBox) objectBase;
                            JSONObject jTextBox = new JSONObject();
                            jTextBox.put("COLOR",NvConstant.getTextColorCode(textBox.getTextColor()));
                            RectF rect = textBox.getRect();
                            String desc = textBox.getText();
                            int count=new Integer(1);
                            int i=new Integer(0);
                            if(desc.equals("")){
                                break;
                            }
                            while((i=desc.indexOf("\n",i+1))!=-1){
                                count++;
                            }

                            rect.bottom = (41+nvData.ScaleHeight/69*count);

                            jTextBox.put("POSITION",nvData.getOriginPosition(rect,NvConstant.TEXT_OBJ));

                            jTextBox.put("DESC",textBox.getText().replace("\n","\r\n"));

                            arrTextboxes.put(jTextBox);
                        }
                        if(objectBase.getType()==3){

                            SpenObjectImage imageObject = (SpenObjectImage) objectBase;
                            JSONObject jImage = new JSONObject();
                            RectF rect = imageObject.getRect();
                            jImage.put("POSITION",nvData.getOriginPosition(rect,NvConstant.IMAGE_OBJ));
                            String index=String.format("%03d",++imageIndex);
                            Log.e("Imege","Index "+index);

                                *//*while(index.length()<3){
                                    index = "0"+index;
                                }*//*
                            jImage.put("INDEX",index);
                            bitmaps.add(imageObject.getImage());
                            arrImage.put(jImage);
                        }
                    }
                    sendData.put("PenData",arrStrokes);
                    sendData.put("TextBox",arrTextboxes);
                    sendData.put("Image",arrImage);

                    JSONArray image = sendData.getJSONArray("Image");
                    cSendData = sendData.toString(1).replace("\\/","/")+"\n";

                    bSendData = new byte[0];
                    bSendData = cSendData.getBytes("EUC-KR");
                    //bSendData = appendByte(bSendData,nvData.tempImage);   //받았던거 그대로 보내기
                    //Log.e("temp Length","길이 "+nvData.tempImage.length);
                    for(int i=0;i<bitmaps.size();i++){
                        Log.e("Index","index = "+ i);
                        String index = image.getJSONObject(i).getString("INDEX");
                        bSendData = appendByte(bSendData,("<---AndcomData_Image"+index+"---\r\n").getBytes()) ;

                        bSendData = appendByte(bSendData,bitmapToByteArray(bitmaps.get(i)));
                        bSendData = appendByte(bSendData,"\r\n".getBytes("EUC-KR"));

                        bSendData = appendByte(bSendData,("---AndcomData_Image"+index+"--->\r\n").getBytes("EUC-KR"));
                    }
                    bSendData = appendByte(bSendData,"---AndcomData_END---".getBytes("EUC-KR"));
                    int maxLogSize = 1000;
                    for(int i = 0; i <= cSendData.length() / maxLogSize; i++) {
                        int start = i * maxLogSize;
                        int end = (i+1) * maxLogSize;
                        end = end > cSendData.length() ? cSendData.length() : end;
                        Log.v("SendData", cSendData.substring(start, end));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } finally {
                }
                return bSendData;
            }
            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                //LoadingProgress.getInstance().progressSET(values[0]);
            }
            @Override
            protected void onPostExecute(byte[] bData) {
                super.onPostExecute(bData);
                ASyncImageSocket socket = new ASyncImageSocket((Activity) mContext, Prefer.getPrefString("key_ip", ""), Integer.parseInt(Prefer.getPrefString("key_port", "80")), new ASyncImageSocket.AsyncResponse() {
                    @Override
                    public void processFinish(String response) {
                        if(response.contains("1")){

                            //Toasty.info(context,"저장 성공").show();
                            sendMessageMe(MsgType.TOAST_MSG,"저장 성공");
                            mSpenPageDoc.clearChangedFlag();
                            objectCnt = mSpenPageDoc.getObjectCount(false);
                        }else{
                            JSONObject jResult = null;
                            try {
                                jResult = new JSONObject(response);
                                //Toasty.error(context,"저장 실패. 다시 시도해주세요."+"\r\n원인:"+jResult.getString("Msg")).show();
                                sendMessageMe(MsgType.TOAST_MSG,"저장 실패. 다시 시도해주세요."+"\r\n원인:"+jResult.getString("Msg"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                });
                if(bData !=null){
                    socket.execute(bData);

                }else{
                    LoadingProgress.getInstance().progressOFF();
                }
            }
        };

    }*/
    public byte[] bitmapToByteArray( Bitmap bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }
    public byte[] appendByte(byte[] original, byte[] append){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try{
            outputStream.write( original );
            outputStream.write( append );
        }catch (IOException ie){
            ie.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    public byte[] getBase64String(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return imageBytes;
        //return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    private String getDecToHex(String dec,int length){

        Long intDec = Long.parseLong(dec);

        String returnValue =  Long.toHexString(intDec).toUpperCase();
        while(returnValue.length()<length){
            returnValue = "0" + returnValue;
        }
        if(returnValue.length()>4){
            returnValue = returnValue.substring(returnValue.length()-4);
        }

        return returnValue;
    }
    boolean menuCreate = false;
    private void initView(){
        mContext = getContext();
        activity = (Activity)mContext;
        btn2 = (Button)getView().findViewById(R.id.btn2);
        progressBar = (LinearLayout)getView().findViewById(R.id.progressBar);

        /*keyboard = (ImageView)getView().findViewById(R.id.text);
        penBtn = (ImageView)getView().findViewById(R.id.pen);
        image = (ImageView)getView().findViewById(R.id.image);
        marker = (ImageView)getView().findViewById(R.id.marker);
        penSel = (ImageView)getView().findViewById(R.id.select);
        eraser = (ImageView)getView().findViewById(R.id.eraser);

        keyboard.setId(NvConstant.KEYBOARD);
        penBtn.setId(NvConstant.PEN);
        image.setId(NvConstant.IMAGE);
        marker.setId(NvConstant.MARKER);
        penSel.setId(NvConstant.SELECT);
        eraser.setId(NvConstant.ERAASER);

        penBtn.setOnClickListener(mPenBtnClickListener);
        penSel.setOnClickListener(mSelectionBtnClickListener);
        eraser.setOnClickListener(mEraserBtnClickListener);
        keyboard.setOnClickListener(mTextObjBtnClickListener);
        marker.setOnClickListener(mMarkerBtnClickListener);*/

        penOption = (CardView)getActivity().findViewById(R.id.pen_option);
        textOption = (CardView)getActivity().findViewById(R.id.text_option);
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) mContext,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_STORAGE_PERMISSION);
        }


        release();
        //spen 초기화
        isSpenFeatureEnabled = false;
        Spen spenPackage = new Spen();

        try{
            spenPackage.initialize(mContext);
            isSpenFeatureEnabled = spenPackage.isFeatureEnabled(Spen.DEVICE_PEN);
        }catch (SsdkUnsupportedException e){
            Toast.makeText(mContext,"지원 X",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }catch (Exception e1){
            Toast.makeText(mContext,"초기화 실패",Toast.LENGTH_SHORT).show();
            e1.getStackTrace();
        }

        RelativeLayout spenViewLayout = (RelativeLayout) getView().findViewById(R.id.spenViewLayout);
        FrameLayout spenViewContainer = (FrameLayout)getView().findViewById(R.id.spenViewContainer);

        //mPenSettingView = new SpenSettingPenLayout(mContext, new String(), spenViewLayout);
        //mEraserSettingView = new SpenSettingRemoverLayout(mContext, "", spenViewLayout);

        //SpenView

        mSpenSurfaceView = new SpenSurfaceView(mContext);
        mSpenSurfaceView.setTouchListener(mPenTouchListener);
        mSpenSurfaceView.setPreTouchListener(onPreTouchSurfaceViewListener);
        mSpenSurfaceView.setFlickListener(onFlickListener);
        mSpenSurfaceView.setControlListener(spenControlListener);

        try{
            fileClip = new File(mContext.getFilesDir()+File.separator+"clip");
            if(!fileClip.canRead()){
                fileClip.createNewFile();
            }


        }catch (IOException ie){
            ie.printStackTrace();
        }
        mSpenSurfaceView.setLongPressListener(new SpenLongPressListener() {
            @Override
            public void onLongPressed(MotionEvent motionEvent) {
                Log.d("onItem","x="+motionEvent.getX()+"y="+motionEvent.getY());
                Log.d("onItem","rawx="+motionEvent.getRawX()+"rawy="+motionEvent.getRawY());
                Log.d("onItem","mMode="+mMode);
                menuCreate = false;
                if(menu!=null && menu.isShowing()){
                    menu.close();
                }
                if(mMode==MODE_SELECTION && motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    PointF canvasPoint=getCanvasPoint(motionEvent);
                    longPressX=canvasPoint.x;
                    longPressY=canvasPoint.y;
                    longPressRawX = motionEvent.getRawX();
                    longPressRawY = motionEvent.getRawY();

                    ArrayList<SpenContextMenuItemInfo> items = new ArrayList<>();
                    SpenContextMenuItemInfo item = new SpenContextMenuItemInfo();
                    item.id=CONTEXT_MENU_PASTE_ID;
                    item.name="붙여넣기";
                    SpenContextMenuItemInfo select_all = new SpenContextMenuItemInfo();
                    select_all.id=CONTEXT_MENU_SELECTALL_ID;
                    select_all.name="전체 선택";
                    SpenContextMenuItemInfo close = new SpenContextMenuItemInfo();
                    close.id=CONTEXT_MENU_CLOSE;
                    close.name="취소";


                    items.add(item);
                    items.add(select_all);
                    items.add(close);

                    menu = new SpenContextMenu(mContext,mSpenSurfaceView,items,NvChart.this);

                    Rect rect = menu.getRect();
                    rect.left+= longPressRawX;
                    rect.right+= longPressRawX;
                    rect.top+= longPressRawY;
                    rect.bottom+= longPressRawY;
                    menu.setRect(rect);



                    menu.show();


                }

            }
        });
        if(mSpenSurfaceView == null){
            Toast.makeText(mContext, "Cannot create new SpenView.",
                    Toast.LENGTH_SHORT).show();
        }


        // Create SelectionSettingView
        //mSelectionSettingView = new SpenSettingSelectionLayout(mContext, "", spenViewLayout);

        // Create TextSettingView
        /*HashMap<String, String> hashMapFont = new HashMap<String, String>();
        hashMapFont.put("Droid Sans Georgian", "/system/fonts/DroidSansGeorgian.ttf");
        hashMapFont.put("Droid Serif", "/system/fonts/DroidSerif-Regular.ttf");
        hashMapFont.put("Droid Sans", "/system/fonts/DroidSans.ttf");
        hashMapFont.put("Droid Sans Mono", "/system/fonts/DroidSansMono.ttf");
        mTextSettingView = (SpenSettingTextLayout) getView().findViewById(R.id.settingTextLayout);
        mTextSettingView.initialize("", hashMapFont, spenViewLayout);*/

        //spenViewContainer.addView(mPenSettingView);
        //spenViewContainer.addView(mSelectionSettingView);
        //spenViewContainer.addView(mEraserSettingView);
        //spenViewContainer.addView(mTextSettingView);

        spenViewLayout.addView(mSpenSurfaceView);
        mSpenSurfaceView.setScrollBarEnabled(true);
        mSpenSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        //mPenSettingView.setCanvasView(mSpenSurfaceView);
        //mTextSettingView.setCanvasView(mSpenSurfaceView);

        gestuerDetector = new GestureDetector(mContext, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.e("gestureD","distanceX ="+distanceX + ", distanceY ="+distanceY+" , scrollbar pos=");

                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.e("onFling","e1.getY() - e2.getY() ="+(e1.getY() - e2.getY())+", Math.abs(velocityY) ="+Math.abs(velocityY));

                if(!e1.getDevice().getName().contains("sec_touchscreen")){
                    return false;
                }
                if(mSpenPageDoc == null){
                    return false;
                }
                try {
                    /*if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){

                        return false;
                    }*/
                    // right to left swipe
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                            && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        Log.e("onFling","leftSwipe");
                        return false;
                    }
                    // left to right swipe
                    else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                            && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        Log.e("onFling","rightSwipe");
                        return false;

                    }

                    // down to up swipe
                    if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                            && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                        Log.e("onFling","upSwipe");

                        NvListRecyclerAdapter nvListRecyclerAdapter = NvListRecyclerAdapter.getInstance();
                        int nodeKey = nvListRecyclerAdapter.getSeletedPosition();
                        if(nodeKey<nvListRecyclerAdapter.getItemCount()){
                            //setNodekey(nvListRecyclerAdapter.getNodeKey(nodeKey+1));
                            //setPageNo(nvListRecyclerAdapter.getPageCnt(nodeKey+1));
                            //bLoadBackImage="1";
                            //refreshChart();

                            confirmSaveBeforeRefresh(getChartNo(),getDB(),nvListRecyclerAdapter.getNodeKey(nodeKey+1),Integer.parseInt(nvListRecyclerAdapter.getPageCnt(nodeKey+1)));
                        }else{

                        }
                        return false;

                    }
                    // up to down swipe
                    else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                            && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                        Log.e("onFling","downSwipe");

                        NvListRecyclerAdapter nvListRecyclerAdapter = NvListRecyclerAdapter.getInstance();
                        int nodeKey = nvListRecyclerAdapter.getSeletedPosition();
                        if(nodeKey!=0){
                            //setNodekey(nvListRecyclerAdapter.getNodeKey(nodeKey-1));
                            //setPageNo(nvListRecyclerAdapter.getPageCnt(nodeKey-1));
                            //bLoadBackImage="1";
                            //refreshChart();

                            confirmSaveBeforeRefresh(getChartNo(),getDB(),nvListRecyclerAdapter.getNodeKey(nodeKey-1),Integer.parseInt(nvListRecyclerAdapter.getPageCnt(nodeKey-1)));

                        }else{

                        }
                        return false;


                    }


                } catch (Exception e) {

                }
                return false;
            }
        });

    }
    private void setSmartScroll(boolean enable){

        // Define the region for Smart Scroll.
        int width, height, w1, h1, w9, h9;
        width = mSpenSurfaceView.getWidth();
        height = mSpenSurfaceView.getHeight();
        w1 = (int) (width * 0.1);
        h1 = (int) (height * 0.1);
        w9 = (int) (width * 0.9);
        h9 = (int) (height * 0.9);

        // Define the region for horizontal Smart Scroll.
        Rect leftRegion = new Rect(0, 0, w1, height);
        Rect rightRegion = new Rect(w9, 0, width, height);
        mSpenSurfaceView.setHorizontalSmartScrollEnabled(enable,
                leftRegion, rightRegion, 500, 10);

        // Define the region for vertical Smart Scroll.
        Rect topRegion = new Rect(0, 0, width, h1);
        Rect bottomRegion = new Rect(0, h9, width, height);
        mSpenSurfaceView.setVerticalSmartScrollEnabled(enable,
                topRegion, bottomRegion, 500, 10);

    }

    private void sendMessage(int msgWhat,Object msg){
        Message message = MainActivity.handler.obtainMessage();
        message.what=msgWhat;
        message.obj=msg;
        MainActivity.handler.sendMessage(message);
    }
    private void sendMessageMe(int msgWhat,Object msg){
        Message message = handler.obtainMessage();
        message.what=msgWhat;
        message.obj=msg;
        handler.sendMessage(message);
    }
    private void loadNvData(NvData nvData){
        long start = System.currentTimeMillis();
        double packetLength =0;
        double pointStrokeTime=0;
        double pointTextTime=0;
        double pointLabelTime=0;
        double pointImageTime=0;
        double update=0;
        try{
            objectCnt=0;
            //nvData = new NvData(mContext,mSpenPageDoc,Data);

            initSpen(nvData);
            setSmartScroll(true);

            packetLength = nvData.getDataSize();
            transfer =  System.currentTimeMillis()-start;

            String[] arrStroke = nvData.getPenData();

            Log.e("NvChartTask","3");

            double textStart = System.currentTimeMillis();
            ArrayList<SpenObjectBase> lists = nvData.getSignData();
            if(lists!=null){
                //SpenObjectContainer signData =  mSpenPageDoc.groupObject(lists,false);
                //mSpenPageDoc.appendObject(signData);
                //mSpenSurfaceView.update();

                for(SpenObjectBase object : lists){
                    object.setSelectable(false);
                    mSpenPageDoc.appendObject(object);

                }

                mSpenPageDoc.groupObject(lists,false).setSelectable(false);

            }
            for(SpenObjectTextBox object : nvData.getTextObjectData(NvConstant.TEXT_OBJ)){
                object.setRotatable(false);

                mSpenPageDoc.appendObject(object);
                ///Rect test
                /*RectF rect = object.getRect();
                PointF p1 = new PointF(rect.left,rect.top);
                PointF p2 = new PointF(rect.left,rect.bottom);
                PointF p3 = new PointF(rect.right,rect.top);
                PointF p4 = new PointF(rect.right,rect.bottom);

                SpenObjectLine line1 = new SpenObjectLine(0,p1,p2);
                SpenObjectLine line2 = new SpenObjectLine(0,p1,p3);
                SpenObjectLine line3 = new SpenObjectLine(0,p2,p4);
                SpenObjectLine line4 = new SpenObjectLine(0,p3,p4);

                mSpenPageDoc.appendObject(line1);
                mSpenPageDoc.appendObject(line2);
                mSpenPageDoc.appendObject(line3);
                mSpenPageDoc.appendObject(line4);*/





                //mSpenPageDoc.selectObject(object);
                //mSpenSurfaceView.update();
                Log.e("RectF-2",object.getRect().toString()+object.getFont());

            }
            pointTextTime = (System.currentTimeMillis()-textStart)/1000.0000;

            double labelStart = System.currentTimeMillis();
            for(SpenObjectTextBox object : nvData.getTextObjectData(NvConstant.LABEL_OBJ)){
                mSpenPageDoc.appendObject(object);
                mSpenPageDoc.selectObject(object);
                object.setSelectable(false);
                object.setTextStyle(SpenObjectShape.HINT_TEXT_STYLE_BOLD);
            }
            pointLabelTime = (System.currentTimeMillis()-labelStart)/1000.0000;


            double imageStart = System.currentTimeMillis();
            for(SpenObjectImage imageObject : nvData.getImageObjectData()){
                mSpenPageDoc.appendObject(imageObject);
                //mSpenSurfaceView.update();
            }
            pointImageTime = (System.currentTimeMillis()-imageStart)/1000.0000;

            /*for(SpenObjectBase object : nvData.getTextObjectData2(NvConstant.TEXT_OBJ)){
                mSpenPageDoc.appendObject(object);
            }*/


            long pointStart = System.currentTimeMillis();
            for(SpenObjectStroke object :nvData.getPenObjectData()){

                mSpenPageDoc.appendObject(object);
            }
            pointStrokeTime = (System.currentTimeMillis()-pointStart)/1000.0000;

            double updateStart = System.currentTimeMillis();
            SpenObjectTextBox dummy = new SpenObjectTextBox();
            mSpenPageDoc.appendObject(dummy);
            mSpenPageDoc.selectObject(dummy);
            mSpenSurfaceView.update();
            mSpenPageDoc.removeSelectedObject();
            mSpenSurfaceView.update();
            Log.e("NvChartTask","4");

            objectCnt = mSpenPageDoc.getObjectCount(false);
            mSpenPageDoc.clearChangedFlag();

            update = (System.currentTimeMillis()-updateStart)/1000.0000;

            setPenInfo();

            Message msg = handler.obtainMessage();
            msg.what = MsgType.NVCHART_RELEASE;
            msg.obj = false;
            handler.sendMessage(msg);

        }catch(Exception e){
            Log.e("error",e.getMessage());
            e.printStackTrace();
            for(int i = 0 ; i < e.getStackTrace().length ; i++){
                Log.e("error",e.getStackTrace()[i].toString());

            }
        }
        long end = System.currentTimeMillis();
        String toast = "byte 길이 = 약 "+packetLength+"KB" +
                "\n전송시간 : "+ String.format("%.4f",transfer /1000.0000)+"(초)" +
                "\n펜 그리는 시간 : "+String.format("%.4f",pointStrokeTime) +"(초)" +
                "\n텍스트박스 시간 : "+String.format("%.4f",pointTextTime)+"(초)" +
                "\n레이블 시간 : "+String.format("%.4f",pointLabelTime)+"(초)" +
                "\n이미지 시간 : "+String.format("%.4f",pointImageTime)+"(초)" +
                "\n화면에 추가 시간 : "+String.format("%.4f",update) + "(초)" +
                "\n전체 소요 시간 : "+String.format("%.4f",(end-start)/1000.0000) + "(초)" ;

        //Toast.makeText(mContext,toast,Toast.LENGTH_LONG).show();
        Log.e("TimeDebug",toast);
        mSpenPageDoc.clearHistory();
        LoadingProgress.getInstance().progressOFF();

    }

    private SpenFlickListener onFlickListener = new SpenFlickListener() {
        @Override
        public boolean onFlick(int i) {
            if(mSpenPageDoc == null){
                return false;
            }
            if(i==0){
                //이전 페이지
                if(getPageNo()>1){
                    //setPageNo(getPageNo()-1);
                    //refreshChart();
                    confirmSaveBeforeRefresh(getChartNo(),getDB(),getNodekey(),getPageNo()-1);
                }
            }
            if(i==1){
                if(getPageNo()<getPageLastNo()){
                    //setPageNo(getPageNo()+1);
                    //refreshChart();

                    confirmSaveBeforeRefresh(getChartNo(),getDB(),getNodekey(),getPageNo()+1);

                }
            }
            return false;
        }
    };
    private SpenTouchListener onPreTouchSurfaceViewListener = new SpenTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub.
            Log.e("HZ", "HZ onPreTouchSurfaceViewListener: " + event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    penOption.setVisibility(View.GONE);
                    textOption.setVisibility(View.GONE);
                case MotionEvent.ACTION_POINTER_DOWN:

                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    break;
            }
            gestuerDetector.onTouchEvent(event);
            return false;
        }
    };




    private final SpenTouchListener mPenTouchListener = new SpenTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            Log.e("event","1");

            closeSettingView();

            if (event.getAction() == MotionEvent.ACTION_UP && event.getToolType(0) == mToolType) {
                // Check if the control is created.
                SpenControlBase control = mSpenSurfaceView.getControl();
                if (control == null) {

                    // When Pen touches the display while it is in Add ObjectImage mode
                    if (mMode == MODE_IMG_OBJ) {
                        // Set a bitmap file to ObjectImage.

                        return true;
                        // When Pen touches the display while it is in Add ObjectTextBox mode
                    } else if (mSpenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_TEXT) {
                        // Set the location to insert ObjectTextBox and add it to PageDoc.
                        Log.e("touchEvent","text");
                        Bitmap bitmap =mSpenPageDoc.getBackgroundImage();
                        SpenObjectTextBox textObj = new SpenObjectTextBox();
                        PointF canvasPos = getCanvasPoint(event);
                        float x = canvasPos.x;
                        float y = canvasPos.y;
                        float height,width;
                        int point = bitmap.getPixel((int)x,(int)y);

                        float textBoxHeight = 24.0f;

                        Log.e("touchEvent",x+", "+y+" ; point "+point);
                        while(x>0){
                            if(point != bitmap.getPixel((int)x,(int)y)){
                                //Log.e("touchEvent",x+", "+y+" ; point "+point + bitmap.getPixel((int)x,(int)y));
                                break;
                            }
                            x=x-1;

                        }
                        float tempX=canvasPos.x;
                        Log.e("touchEvent","tempX="+tempX);

                        while (tempX<mSpenPageDoc.getWidth()){
                            Log.e("touchEvent",canvasPos.x+", tempX="+tempX+", "+y+" ; point "+point + bitmap.getPixel((int)tempX,(int)y));

                            if(point != bitmap.getPixel((int)tempX,(int)canvasPos.y) ){
                                break;
                            }
                            tempX=tempX+1;
                        }
                        width=tempX;
                        Log.e("touchEvent","x="+x+", tempX="+tempX+", width="+width);

                        while(y>0){
                            if(point != bitmap.getPixel((int)canvasPos.x,(int)y) || mSpenPageDoc.findObjectAtPosition(SpenPageDoc.FIND_TYPE_TEXT_BOX,canvasPos.x,y,10).size()>0){
                                //Log.e("touchEvent",x+", "+y+" ; point "+point + bitmap.getPixel((int)x,(int)y));
                                break;
                            }
                            y=y-1;

                        }
                        float tempY = (int)canvasPos.y;
                        boolean fixedBox = false;

                        while(tempY<mSpenPageDoc.getHeight() && tempY-y <textBoxHeight*2){
                            if(point != bitmap.getPixel((int)canvasPos.x,(int)tempY)){
                                //Log.e("touchEvent",x+", "+y+" ; point "+point + bitmap.getPixel((int)x,(int)y));
                                fixedBox = true;
                                break;
                            }
                            tempY=tempY+1;
                        }
                        Log.w("textBoxY","textBoxHeight = "+ textBoxHeight +", drawnRect = "+(textObj.getRect().bottom-textObj.getRect().top)+ ", tempY-y = "+(tempY-y)+", canvasHeight = "+ mSpenPageDoc.getHeight());

                        Log.w("textBoxY","fixedBox = "+ fixedBox);
                        if(fixedBox){
                            //y= y+(tempY-y)/2 - textBoxHeight/2;
                            y= y+(tempY-y)/2 - (textObj.getRect().bottom-textObj.getRect().top)/2;
                        }else{
                            if(canvasPos.y-textBoxHeight/2<y){
                                y = y+10;
                            }else{
                                y= canvasPos.y-textBoxHeight/2;

                            }
                        }
                        /*
                        float tempY=canvasPos.y;

                        while(tempY<mSpenPageDoc.getHeight()){
                            if(point !=bitmap.getPixel((int)canvasPos.x,(int)tempY)){
                                break;
                            }
                            tempY=tempY+1;
                        }
                        height=tempY;
                        Log.e("touchEvent","x="+x+", y="+y+", width="+width+", height="+height);
                        */
                        textObj.setMargin(0,0,0,0);

                        if ((y + textBoxHeight) > mSpenPageDoc.getHeight()) {
                            y = mSpenPageDoc.getHeight() - textBoxHeight;
                        }
                        RectF rect = new RectF(x, y, width, y);
                        Log.d("textObj",rect.toString());
                        textObj.setRect(rect, true);

                        textObj.setRotatable(false);
                        textObj.setFontSize(18);
                        textObj.setTextColor(Prefer.getPrefInt("TextColor",0));


                        mSpenPageDoc.appendObject(textObj);
                        mSpenPageDoc.selectObject(textObj);
                        mSpenSurfaceView.update();
                        Log.d("textObj",textObj.getDrawnRect().toString());

                        rect = textObj.getDrawnRect();
                        float fixedY = rect.top- (textObj.getDrawnRect().bottom-textObj.getDrawnRect().top)/2;
                        if(fixedY<y && !fixedBox){
                            fixedY = y+3;
                        }else{
                            if(fixedBox){
                                //fixedY = rect.top;
                            }else{
                                fixedY= canvasPos.y-textBoxHeight/2;

                            }

                        }
                        rect.top = fixedY;
                        textObj.setRect(rect,true);
                        mSpenSurfaceView.update();

                    }
                }
            }
            return false;
        }
    };
    private PointF getCanvasPoint(MotionEvent event) {
        float panX = mSpenSurfaceView.getPan().x;
        float panY = mSpenSurfaceView.getPan().y;
        float zoom = mSpenSurfaceView.getZoomRatio();
        return new PointF(event.getX() / zoom + panX, event.getY() / zoom + panY);
    }
    private float getTextBoxDefaultHeight(SpenObjectTextBox textBox) {
        if (textBox == null) {
            return 0;
        }

        float height = 0, lineSpacing = 0, lineSpacePercent = 1.3f;
        float margin = textBox.getTopMargin() + textBox.getBottomMargin();

        ArrayList<SpenTextParagraphBase> pInfo = textBox.getTextParagraph();
        if (pInfo != null) {
            for (SpenTextParagraphBase info : pInfo) {
                if (info instanceof SpenLineSpacingParagraph) {
                    if (((SpenLineSpacingParagraph) info).getLineSpacingType() ==
                            SpenLineSpacingParagraph.TYPE_PERCENT) {
                        lineSpacePercent = ((SpenLineSpacingParagraph) info).getLineSpacing();
                    } else if (((SpenLineSpacingParagraph) info).getLineSpacingType() ==
                            SpenLineSpacingParagraph.TYPE_PIXEL) {
                        lineSpacing = ((SpenLineSpacingParagraph) info).getLineSpacing();
                    }
                }
            }
        }

        if (lineSpacing != 0){
            height = lineSpacing + margin;
        } else {
            float fontSize = mSpenPageDoc.getWidth()/20;
            ArrayList<SpenTextSpanBase> sInfo =
                    textBox.findTextSpan(textBox.getCursorPosition(), textBox.getCursorPosition());
            if (sInfo != null) {
                for (SpenTextSpanBase info : sInfo) {
                    if (info instanceof SpenFontSizeSpan) {
                        fontSize = ((SpenFontSizeSpan) info).getSize();
                        break;
                    }
                }
            }
            height = fontSize * lineSpacePercent;
        }

        return height;
    }

    private final SpenControlListener spenControlListener = new SpenControlListener() {
        @Override
        public boolean onClosed(ArrayList<SpenObjectBase> arrayList) {
            return false;
        }

        @Override
        public boolean onCreated(ArrayList<SpenObjectBase> arrayList, ArrayList<Rect> arrayList1, ArrayList<SpenContextMenuItemInfo> menu,
                                 ArrayList<Integer> arrayList3, int i, PointF pointF) {
            menu.add(new SpenContextMenuItemInfo(CONTEXT_MENU_CUT_ID,"잘라내기", true));
            menu.add(new SpenContextMenuItemInfo(CONTEXT_MENU_COPY_ID ,"복사", true));
            menu.add(new SpenContextMenuItemInfo(CONTEXT_MENU_DELETE_ID,"삭제", true));


            return true;
        }

        @Override
        public void onObjectChanged(ArrayList<SpenObjectBase> arrayList) {

        }

        @Override
        public void onRectChanged(RectF rectF, SpenObjectBase spenObjectBase) {

        }

        @Override
        public void onRotationChanged(float v, SpenObjectBase spenObjectBase) {

        }

        @Override
        public boolean onMenuSelected(ArrayList<SpenObjectBase> objectList, int itemId) {
            switch (itemId) {
                // Remove the selected object.
                case CONTEXT_MENU_DELETE_ID:
                    // mSpenPageDoc.removeSelectedObject();
                    for (SpenObjectBase obj : objectList) {
                        mSpenPageDoc.removeObject(obj);
                    }
                    mSpenSurfaceView.closeControl();
                    mSpenSurfaceView.update();
                    break;

                case CONTEXT_MENU_COPY_ID :
                    //ArrayList<SpenObjectBase> items = new ArrayList<>();
                    //items=mSpenPageDoc.getObjectList();

                    mSpenNoteDoc.backupObjectList(objectList,fileClip.getAbsolutePath());

                    mSpenSurfaceView.closeControl();
                    mSpenSurfaceView.update();
                    break;
                case CONTEXT_MENU_CUT_ID :

                    mSpenNoteDoc.backupObjectList(objectList,fileClip.getAbsolutePath());
                    mSpenPageDoc.removeSelectedObject();
                    mSpenSurfaceView.closeControl();
                    mSpenSurfaceView.update();
                    break;
            }
            return true;
        }
    };
    private final View.OnClickListener mTextObjBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e("text","1");
            mSpenSurfaceView.closeControl();
            closeSettingView();
            mSpenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_TEXT);
            mMode = MODE_TEXT_OBJ;
            selectButton(keyboard);
        }
    };
    private final View.OnClickListener mPenBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSpenSurfaceView.closeControl();
            selectButton(penBtn);
            // When Spen is in stroke (pen) mode
            if (mSpenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_STROKE) {
                // If PenSettingView is open, close it.
                if (mPenSettingView.isShown()) {
                    mPenSettingView.setVisibility(View.GONE);
                    // If PenSettingView is not open, open it.
                } else {
                    mPenSettingView.setViewMode(SpenSettingPenLayout.VIEW_MODE_NORMAL);
                    mPenSettingView.setVisibility(View.VISIBLE);
                }
                // If Spen is not in stroke (pen) mode, change it to stroke mode.
            } else {
                mMode = MODE_PEN;

                mSpenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_STROKE);
            }

        }
    };

    private final View.OnClickListener mMarkerBtnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            mSpenSurfaceView.closeControl();
            closeSettingView();
            mSpenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_STROKE);
            SpenSettingPenInfo mPenInfo = new SpenSettingPenInfo();
            mPenInfo.name = "com.samsung.android.sdk.pen.pen.preload.Marker";
            selectButton(v);
            mSpenSurfaceView.setPenSettingInfo(mPenInfo);

        }
    };
    private final View.OnClickListener mSelectionBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSpenSurfaceView.closeControl();

            // When Spen is in selection mode
            if (mSpenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_SELECTION) {
                // If SelectionSettingView is open, close it.
                if (mSelectionSettingView.isShown()) {
                    mSelectionSettingView.setVisibility(View.GONE);
                    // If SelectionSettingView is not open, open it.
                } else {
                    mSelectionSettingView.setVisibility(View.VISIBLE);
                }
                // If Spen is not in selection mode, change it to selection mode.
            } else {
                mMode = MODE_SELECTION;
                selectButton(v);
                mSpenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_SELECTION);
            }
        }
    };

    private final View.OnClickListener mEraserBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // When Spen is in eraser mode
            if (mSpenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_STROKE_REMOVER) {
                // If EraserSettingView is open, close it.
                if (mEraserSettingView.isShown()) {
                    mEraserSettingView.setVisibility(View.GONE);
                    // If EraserSettingView is not open, open it.
                } else {
                    mEraserSettingView.setVisibility(View.VISIBLE);
                }
                // If Spen is not in eraser mode, change it to eraser mode.
            } else {
                selectButton(eraser);
                mSpenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_STROKE_REMOVER);
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.main_frame_nvchart, container, false);

        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    private void release(){

        /*if(mSpenSurfaceView != null) {
            mSpenSurfaceView.close();
            mSpenSurfaceView = null;
        }*/
        if(mSpenPageDoc != null){
            mSpenPageDoc = null;
        }
        if(mSpenNoteDoc != null) {
            try {
                mSpenNoteDoc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSpenNoteDoc = null;
        }


    }

    public String getName(){
        return FRAGMENT_NAME;
    }


    private float convertX(float src,float scaleWidth,float scaleLeft){
        float convertValue ;
        convertValue = (src-scaleLeft)*rect.width()/scaleWidth;
        return convertValue;
    }
    private float convertY(float src,float scaleHeight,float scaleTop){
        float convertValue ;
        convertValue = (src-scaleTop)*rect.height()/scaleHeight;
        return convertValue;
    }


    private void initPenSettingInfo() {
        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
        penInfo.color = NvConstant.PEN_BLACK;
        penInfo.size = 2;
        mSpenSurfaceView.setPenSettingInfo(penInfo);
        mPenSettingView.setInfo(penInfo);
    }
    private void enableButton(boolean isEnable) {
        penBtn.setEnabled(isEnable);
        image.setEnabled(isEnable);
        keyboard.setEnabled(isEnable);
        marker.setEnabled(isEnable);
        penSel.setEnabled(isEnable);
        eraser.setEnabled(isEnable);
    }
    private void selectButton(View v) {
        // Enable or disable the button according to the current mode.
        penSel.setSelected(false);
        penBtn.setSelected(false);
        eraser.setSelected(false);
        marker.setSelected(false);
        keyboard.setSelected(false);
        //mStrokeObjBtn.setSelected(false);
        //mShapeLineObjBtn.setSelected(false);

        v.setSelected(true);

        closeSettingView();
    }

    private void closeSettingView() {
        // Close all the setting views.
        close_penOption(null);

    }

    private class UIHandler extends Handler{
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /*switch (msg.arg1) {
                case 1:
                    if(state.equals("DeActive")) //Fragment가 숨겨진 상태일 때
                        break;
                    //Fragment의 UI를 변경하는 작업을 수행합니다.
                    NvData nvData = (NvData)msg.obj;
                    initSpen(nvData);
            }
            */
            switch (msg.what){
                case MsgType.REFRESH_NVCHART :
                    //setJData(msg.obj.toString());
                    try{
                        jData = new JSONObject(msg.obj.toString());

                        confirmSaveBeforeRefresh(jData.getString("ND_CHARTNO"),
                                                    jData.getString("DB"),
                                                    jData.getString("ND_NODEKEY"),
                                                    Integer.parseInt(jData.getString("ND_PAGENO")));


                    }catch (JSONException je){
                        je.printStackTrace();
                    }
                    break;

                case MsgType.NVCHART_RELEASE :

                    ImageView background = (ImageView)getView().findViewById(R.id.backgroundImage);
                    if((boolean)msg.obj){
                        background.setVisibility(View.VISIBLE);
                        NvChart.this.sendMessage(MsgType.CLOSE_CHART,"");
                        release();
                    }else{
                        //initView();
                        background.setVisibility(View.GONE);
                    }

                    break;

                case MsgType.CLOSE_CHART :  //닫기전 저장 확인
                    final Message message = this.obtainMessage();
                    message.what=MsgType.NVCHART_RELEASE;
                    message.obj=true;

                    if(isChanged()){

                        saveDialog = new SaveDialog(mContext, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {   //저장
                                SaveChartAsync saveChartAsync = new SaveChartAsync(new AsyncAfter() {
                                    @Override
                                    public void afterExcute() {
                                        handler.sendMessage(message);

                                    }
                                });
                                saveChartAsync.execute();

                                saveDialog.dismiss();

                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {   //저장안함
                                handler.sendMessage(message);

                                saveDialog.dismiss();

                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {   //취소
                                saveDialog.dismiss();
                                return;
                            }
                        });

                        saveDialog.show();
                    }else{
                        handler.sendMessage(message);
                    }

                    break;
                    case MsgType.TOAST_MSG:

                        Toasty.info(context,msg.obj.toString()).show();

                        break;
            }
        }


    }

    private class SpenObjectThread extends Thread{
        private ArrayList<SpenObjectBase> lists;
        private SpenPageDoc pageDoc;
        private SpenSurfaceView surfaceView;
        public SpenObjectThread(ArrayList e,SpenPageDoc pageDoc,SpenSurfaceView surfaceView){
            this.lists = e;
            this.pageDoc = pageDoc;
            this.surfaceView = surfaceView;
        }

        public void run(){
            for(SpenObjectBase objectBase : lists){
                pageDoc.appendObject(objectBase);
                surfaceView.update();
            }
        }
    }


    public class NvChartAsyncTask extends AsyncTask<String,Void,String> {
        SpenSurfaceView _mSpenSurfaceView;
        SpenPageDoc _mSpenPageDoc;
        Context _context;

        public NvChartAsyncTask(SpenSurfaceView _mSpenSurfaceView,
                                SpenPageDoc _mSpenPageDoc,
                                Context _context) {
            super();
            this._mSpenSurfaceView = _mSpenSurfaceView;
            this._mSpenPageDoc = _mSpenPageDoc;
            this._context = _context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("NvChartTask","onPreExcute");
            LoadingProgress.getInstance().progressON((Activity)context,"불러오는 중");
        }
        @Override
        protected String doInBackground(String... strings) {
            Log.e("NvChartTask","doInBackground "+strings[0]);
            final String[] cDbInfo= new String[2];

            try {
                JSONObject temp = new JSONObject(strings[0]);
                cDbInfo[0] = temp.getString("DB");
                cDbInfo[1] = temp.getString("ND_NODEKEY");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String ip = Prefer.getPrefString("key_ip","");
            int port = Integer.parseInt(Prefer.getPrefString("key_port","80"));
            ASyncSocket socket = new ASyncSocket((Activity) _context, ip, port, new ASyncSocket.AsyncResponse() {
                @Override
                public void processFinish(byte[] output) {
                    if(output==null){
                        Toasty.error(_context,"문제가 발생했습니다. 접속상태를 확인하고 다시 시도하세요").show();
                        LoadingProgress.getInstance().progressOFF();
                        return;
                    }
                    nvData = new NvData(_context,output,cDbInfo);
                    loadNvData(nvData);
                }
            });
            socket.execute(strings[0]);
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("NvChartTask","onPostExecute \r\n" + s);

        }


    }

    public void onStop(){
        super.onStop();
        state = "DeActive";
        closeSettingView();
    }

    public void onResume(){
        super.onResume();
        state = "Active";
    }

}
