package andcom.nvchart.nvChart;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jpegkit.Jpeg;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenObjectBase;
import com.samsung.android.sdk.pen.document.SpenObjectImage;
import com.samsung.android.sdk.pen.document.SpenObjectShape;
import com.samsung.android.sdk.pen.document.SpenObjectStroke;
import com.samsung.android.sdk.pen.document.SpenObjectTextBox;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.document.textspan.SpenFontSizeSpan;
import com.samsung.android.sdk.pen.document.textspan.SpenLineSpacingParagraph;
import com.samsung.android.sdk.pen.document.textspan.SpenTextParagraphBase;
import com.samsung.android.sdk.pen.document.textspan.SpenTextSpanBase;
import com.samsung.android.sdk.pen.engine.SpenControlBase;
import com.samsung.android.sdk.pen.engine.SpenSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;
import com.samsung.android.sdk.pen.settingui.SpenSettingPenLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingRemoverLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingSelectionLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingTextLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import andcom.nvchart.ASyncImageSocket;
import andcom.nvchart.R;

public class NvChart extends Fragment {
    private static final String FRAGMENT_NAME="NVCHART";
    private static final String TAG="NVCHART";
    private final int MODE_PEN = 0;
    private final int MODE_IMG_OBJ = 1;
    private final int MODE_TEXT_OBJ = 2;
    private final int MODE_SELECTION = 3;
    int READ_STORAGE_PERMISSION = 11;

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
    private SpenSurfaceView mSpenSurfaceView;
    private SpenSettingPenLayout mPenSettingView;
    private ImageView imageView;
    private ImageView keyboard,penBtn,image,marker,penSel,eraser;
    private SpenSettingSelectionLayout mSelectionSettingView;
    private SpenSettingRemoverLayout mEraserSettingView;
    private SpenSettingTextLayout mTextSettingView;
    private LinearLayout progressBar;
    private ViewTreeObserver vto;
    boolean isSpenFeatureEnabled = false;
    PointF[] points;
    float[] pressures;
    int[] timestamps;
    Rect rect;
    Jpeg jpg;

    int load=2;
    static long transfer;
    boolean mMearsure = false;

    public static Handler handler;

    public NvChart(){
        Log.e("NvChartConstruct","Load NvChartFragment");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"OnCreate call");


        this.cData = getArguments().getString("DATA");

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

        Log.e("colorValue",""+NvConstant.TEXT_BLACK);
        Log.e("colorValue",""+NvConstant.TEXT_RED);
        Log.e("colorValue",""+NvConstant.TEXT_BLUE);
        Log.e("colorValue",""+NvConstant.TEXT_GREEN);
        Log.e("colorValue",""+NvConstant.TEXT_YELLOW);
        Log.e("colorValue",""+NvConstant.TEXT_PURPLE);
        Log.e("colorValue",""+NvConstant.TEXT_PINK);

        initView();

        handler = new UIHandler();

        BaseFrameLayout asd = (BaseFrameLayout)getView().findViewById(R.id.spenViewContainer);
        final ViewTreeObserver observer = asd.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Log.e("SurfaceViewSize","width "+mSpenSurfaceView.getWidth()+" height "+mSpenSurfaceView.getHeight());
                loadNvData(cData);
                observer.removeOnGlobalLayoutListener(this);
            }
        });


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
        initPenSettingInfo();

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

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("SurfaceViewSize","width "+mSpenSurfaceView.getWidth()+" height "+mSpenSurfaceView.getHeight());
                /*if(load==1){
                    loadNvData();
                    load=2;
                }else{
                    loadNvData2();
                    load=1;
                }*/
                try{
                    JSONObject sendData = new JSONObject();
                    String cSendData = new String();

                    sendData.put("User","andcom3");
                    sendData.put("UserPhone","010-1234-5678");
                    sendData.put("구분코드","4");
                    sendData.put("DB","1");
                    sendData.put("ND_CHARTNO","00000001");
                    sendData.put("ND_NODEKEY","C0101");
                    sendData.put("ND_PAGENO","001");
                    JSONArray arrStrokes = new JSONArray();
                    JSONArray arrTextboxes = new JSONArray();
                    JSONArray arrImage = new JSONArray();
                    ArrayList<Bitmap> bitmaps = new ArrayList<>();
                    int imageIndex = 0;
                    ArrayList<SpenObjectBase> objects = mSpenPageDoc.getObjectList();


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

                            }else{
                                penSize = NvConstant.getPenSizeCode(penMode,stroke.getPenSize());
                                penColor= NvConstant.getPenColorCode(stroke.getColor());
                            }
                            cStroke = getDecToHex(penMode+penSize,2)+getDecToHex(penColor,2);       //4자리 헤더 생성


                            float[] pointsX = stroke.getXPoints();
                            float[] pointsY = stroke.getYPoints();

                            for(int i =0 ; i < pointsX.length ; i++){
                                float tempX = pointsX[i]*NvData.ScaleWidth/jpg.getWidth()+NvData.ScaleLeft;
                                float tempY = pointsY[i]*NvData.ScaleHeight/jpg.getHeight()+NvData.ScaleTop;
                                cStroke +=getDecToHex(String.valueOf((long)tempX),4);
                                cStroke +=getDecToHex(String.valueOf((long)tempY),4);
                            }
                            JSONObject jStroke = new JSONObject();
                            jStroke.put("BYTE",cStroke);
                            arrStrokes.put(jStroke);

                        }
                        if(objectBase.getType()==SpenPageDoc.FIND_TYPE_TEXT_BOX && objectBase.isSelectable()){
                            SpenObjectTextBox textBox = (SpenObjectTextBox) objectBase;
                            JSONObject jTextBox = new JSONObject();
                            jTextBox.put("COLOR",NvConstant.getPenColorCode(textBox.getTextColor()));
                            RectF rect = textBox.getRect();
                            String desc = textBox.getText();
                            int count=new Integer(1);
                            int i=new Integer(0);

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

                            /*while(index.length()<3){
                                index = "0"+index;
                            }*/
                            jImage.put("INDEX",index);
                            bitmaps.add(imageObject.getImage());
                            arrImage.put(jImage);
                        }
                    }
                    /*
                    sendData.put("PenData",arrStrokes);
                    sendData.put("TextBox",arrTextboxes);
                    sendData.put("Image",arrImage);*/

                    JSONObject temp = new JSONObject(nvData.cJsonData);
                    sendData.put("PenData",temp.getJSONArray("PenData"));
                    sendData.put("TextBox",temp.getJSONArray("TextBox"));
                    sendData.put("Image",temp.getJSONArray("Image"));

                    JSONArray image = sendData.getJSONArray("Image");
                    cSendData = sendData.toString(1).replace("\\/","/")+"\n";

                    byte[] bSendData = new byte[0];
                    //bSendData=bitmapToByteArray(bitmaps.get(0));
                    bSendData = cSendData.getBytes("EUC-KR");
                    //bSendData = appendByte(bSendData,nvData.tempImage);   //받았던거 그대로 보내기
                    Log.e("temp Length","길이 "+nvData.tempImage.length);
                    /*for(int i=0;i<image.length();i++){
                        String index = image.getJSONObject(i).getString("INDEX");
                        cSendData = cSendData + "<---AndcomData_Image"+index+"---\n";
                        cSendData = cSendData + new String(getBase64String(bitmaps.get(i))) + "\n";

                        cSendData = cSendData + "---AndcomData_Image"+index+"--->\n";
                    }*/
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
                    //Log.e("sendData",cSendData.toString().replace("\\/","/"));

                    //test

                    //

                    //전송하기
                    //ASyncTextSocket aSyncTextSocket = new ASyncTextSocket((Activity) mContext,"192.168.10.109",80);
                    //String result = aSyncTextSocket.execute(cSendData).get();

                    //ASyncImageSocket socket = new ASyncImageSocket((Activity) mContext,"192.168.10.186",1235);
                    //ASyncImageSocket socket = new ASyncImageSocket((Activity) mContext,"192.168.10.109",80);
                    //String result = socket.execute(bSendData).get();
                        /*File fTemp = new File(mContext.getFilesDir()+File.separator+"file");
                        byte[] btemp = new byte[(int)fTemp.length()];

                        FileInputStream fis = new FileInputStream(fTemp);
                        fis.read(btemp);
                        fis.close();
                        bSendData = appendByte(bSendData,btemp);
*/
                    ASyncImageSocket socket = new ASyncImageSocket((Activity) mContext,"192.168.10.109",80);
                    String result = socket.execute(bSendData).get();

                    //ASyncImageSocket socket1 = new ASyncImageSocket((Activity) mContext,"192.168.10.186",1235);
                    //String result1 = socket1.execute(bSendData).get();

                }catch (JSONException je){
                    je.printStackTrace();
                }catch (ExecutionException ee){
                    ee.printStackTrace();
                }catch (InterruptedException ie){
                    ie.printStackTrace();
                }catch (UnsupportedEncodingException uee){
                    uee.printStackTrace();
                }/*catch (FileNotFoundException ffe){
                    ffe.printStackTrace();
                }*/catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });
    }
    public byte[] bitmapToByteArray( Bitmap $bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
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



    private void initSpen2(NvData2 nvData){

        // Get the dimensions of the screen.
        Display display = activity.getWindowManager().getDefaultDisplay();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ File.separator+"test2.jpg");
        try{
            jpg = nvData.getBackImageJpg();
            rect = new Rect();

            rect.set(0,0,(int)(jpg.getWidth()*5.08f),(int)(jpg.getHeight()*5.08f));
        }catch (Exception ie){
            ie.printStackTrace();
        }

        // Create SpenNoteDoc.
        try {
            mSpenNoteDoc =
                    new SpenNoteDoc(mContext, rect.width(), rect.height());
        } catch (IOException e) {
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
        initPenSettingInfo();

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
    private String getDecToHex(String dec,int length){

        Long intDec = Long.parseLong(dec);
        String returnValue =  Long.toHexString(intDec).toUpperCase();
        while(returnValue.length()<length){
            returnValue = "0" + returnValue;
        }
        return returnValue;
    }
    private void initView(){
        mContext = getContext();
        activity = (Activity)mContext;
        btn2 = (Button)getView().findViewById(R.id.btn2);
        progressBar = (LinearLayout)getView().findViewById(R.id.progressBar);

        keyboard = (ImageView)getView().findViewById(R.id.text);
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
        marker.setOnClickListener(mMarkerBtnClickListener);

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
        Toast.makeText(mContext,"초기화 성공" + isSpenFeatureEnabled,Toast.LENGTH_SHORT).show();

        RelativeLayout spenViewLayout = (RelativeLayout) getView().findViewById(R.id.spenViewLayout);
        FrameLayout spenViewContainer = (FrameLayout)getView().findViewById(R.id.spenViewContainer);

        mPenSettingView = new SpenSettingPenLayout(mContext, new String(), spenViewLayout);
        mEraserSettingView = new SpenSettingRemoverLayout(mContext, "", spenViewLayout);

        //SpenView

        mSpenSurfaceView = new SpenSurfaceView(mContext);
        mSpenSurfaceView.setTouchListener(mPenTouchListener);
        mSpenSurfaceView.setPreTouchListener(onPreTouchSurfaceViewListener);

        if(mSpenSurfaceView == null){
            Toast.makeText(mContext, "Cannot create new SpenView.",
                    Toast.LENGTH_SHORT).show();
        }
        // Create SelectionSettingView
        mSelectionSettingView = new SpenSettingSelectionLayout(mContext, "", spenViewLayout);

        // Create TextSettingView
        HashMap<String, String> hashMapFont = new HashMap<String, String>();
        hashMapFont.put("Droid Sans Georgian", "/system/fonts/DroidSansGeorgian.ttf");
        hashMapFont.put("Droid Serif", "/system/fonts/DroidSerif-Regular.ttf");
        hashMapFont.put("Droid Sans", "/system/fonts/DroidSans.ttf");
        hashMapFont.put("Droid Sans Mono", "/system/fonts/DroidSansMono.ttf");
        mTextSettingView = (SpenSettingTextLayout) getView().findViewById(R.id.settingTextLayout);
        mTextSettingView.initialize("", hashMapFont, spenViewLayout);

        spenViewContainer.addView(mPenSettingView);
        spenViewContainer.addView(mSelectionSettingView);
        spenViewContainer.addView(mEraserSettingView);
        //spenViewContainer.addView(mTextSettingView);

        spenViewLayout.addView(mSpenSurfaceView);

        mPenSettingView.setCanvasView(mSpenSurfaceView);
        mTextSettingView.setCanvasView(mSpenSurfaceView);

    }

    private void loadNvData(String Data){
        long start = System.currentTimeMillis();
        double packetLength =0;
        double pointStrokeTime=0;
        double pointTextTime=0;
        double pointLabelTime=0;
        double pointImageTime=0;
        double update=0;
        try{


            NvData nvData = new NvData(mContext,mSpenPageDoc,Data);
            initSpen(nvData);

            packetLength = nvData.getDataSize();
            transfer =  System.currentTimeMillis()-start;

            String[] arrStroke = nvData.getPenData();


            SpenObjectTextBox textObj = new SpenObjectTextBox("12345");
            RectF textPoint = new RectF(20,100,400,1800);
            textObj.setRect(textPoint,false);
            textObj.setFontSize(18);
            //mSpenPageDoc.appendObject(textObj);
            //mSpenPageDoc.selectObject(textObj);
            //mSpenSurfaceView.update();

            //Log.e("TestTextRect",            mSpenPageDoc.getObject(0).getRect().toString());

            double textStart = System.currentTimeMillis();
            for(SpenObjectTextBox object : nvData.getTextObjectData(NvConstant.TEXT_OBJ)){
                mSpenPageDoc.appendObject(object);
                //mSpenPageDoc.selectObject(object);
                mSpenSurfaceView.update();
                Log.e("RectF-2",object.getRect().toString());

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
                mSpenSurfaceView.update();
            }
            pointImageTime = (System.currentTimeMillis()-imageStart)/1000.0000;


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

            update = (System.currentTimeMillis()-updateStart)/1000.0000;

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

        Toast.makeText(mContext,toast,Toast.LENGTH_LONG).show();
        Log.e("TimeDebug",toast);
        //progressBar.setVisibility(View.GONE);

    }private void loadNvData2(){
        long start = System.currentTimeMillis();
        double packetLength =0;
        double pointStrokeTime=0;
        double pointTextTime=0;
        double pointLabelTime=0;
        double pointImageTime=0;
        double update=0;
        try{


            NvData2 nvData = new NvData2(mContext,mSpenPageDoc);
            initSpen2(nvData);

            packetLength = nvData.getDataSize();
            transfer =  System.currentTimeMillis()-start;

            String[] arrStroke = nvData.getPenData();


            SpenObjectTextBox textObj = new SpenObjectTextBox("Width : " + jpg.getWidth() + "\nHeight : " + jpg.getHeight()+"\n");
            RectF textPoint = new RectF(20,100,400,800);
            textObj.setRect(textPoint,true);
            textObj.setFontSize(18);
            mSpenPageDoc.appendObject(textObj);
            //mSpenPageDoc.selectObject(textObj);
            mSpenSurfaceView.update();
            double textStart = System.currentTimeMillis();
            for(SpenObjectTextBox object : nvData.getTextObjectData(NvConstant.TEXT_OBJ)){
                mSpenPageDoc.appendObject(object);
            }
            pointTextTime = (System.currentTimeMillis()-textStart)/1000.0000;

            double labelStart = System.currentTimeMillis();
            for(SpenObjectTextBox object : nvData.getTextObjectData(NvConstant.LABEL_OBJ)){
                mSpenPageDoc.appendObject(object);
                object.setSelectable(false);
                object.setTextStyle(SpenObjectShape.HINT_TEXT_STYLE_BOLD);
            }
            pointLabelTime = (System.currentTimeMillis()-labelStart)/1000.0000;

            double imageStart = System.currentTimeMillis();
            for(SpenObjectImage imageObject : nvData.getImageObjectData()){
                mSpenPageDoc.appendObject(imageObject);
                mSpenSurfaceView.update();
            }
            pointImageTime = (System.currentTimeMillis()-imageStart)/1000.0000;


            long pointStart = System.currentTimeMillis();
            for(SpenObjectStroke object :nvData.getPenObjectData()){
                mSpenPageDoc.appendObject(object);
            }
            pointStrokeTime = (System.currentTimeMillis()-pointStart)/1000.0000;

            double updateStart = System.currentTimeMillis();
            mSpenSurfaceView.update();
            //mSpenSurfaceView.updateScreen();

            update = (System.currentTimeMillis()-updateStart)/1000.0000;

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

        Toast.makeText(mContext,toast,Toast.LENGTH_LONG).show();
        Log.e("TimeDebug",toast);
        //progressBar.setVisibility(View.GONE);

    }

    private SpenTouchListener onPreTouchSurfaceViewListener = new SpenTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub.
            Log.e("HZ", "HZ onPreTouchSurfaceViewListener: " + event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    enableButton(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    enableButton(true);
                    break;
            }
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
                Log.e("event","2");
                if (control == null) {
                    // When Pen touches the display while it is in Add ObjectImage mode
                    if (mMode == MODE_IMG_OBJ) {
                        // Set a bitmap file to ObjectImage.

                        return true;
                        // When Pen touches the display while it is in Add ObjectTextBox mode
                    } else if (mSpenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_TEXT) {
                        // Set the location to insert ObjectTextBox and add it to PageDoc.
                        Log.e("event","text");
                        SpenObjectTextBox textObj = new SpenObjectTextBox();
                        PointF canvasPos = getCanvasPoint(event);
                        float x = canvasPos.x;
                        float y = canvasPos.y;
                        float textBoxHeight = getTextBoxDefaultHeight(textObj);
                        if ((y + textBoxHeight) > mSpenPageDoc.getHeight()) {
                            y = mSpenPageDoc.getHeight() - textBoxHeight;
                        }
                        RectF rect = new RectF(x, y, x + 350, y + textBoxHeight);
                        textObj.setRect(rect, true);
                        mSpenPageDoc.appendObject(textObj);
                        mSpenPageDoc.selectObject(textObj);
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

        if(mSpenSurfaceView != null) {
            mSpenSurfaceView.close();
            mSpenSurfaceView = null;
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
        mPenSettingView.setVisibility(SpenSurfaceView.GONE);
        mTextSettingView.setVisibility(SpenSurfaceView.GONE);
        mSelectionSettingView.setVisibility(SpenSurfaceView.GONE);
    }

    private class UIHandler extends Handler{
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case 1:
                    if(state.equals("DeActive")) //Fragment가 숨겨진 상태일 때
                        break;
                    //Fragment의 UI를 변경하는 작업을 수행합니다.
                    NvData nvData = (NvData)msg.obj;
                    initSpen(nvData);
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
    public void onStop(){
        super.onStop();
        state = "DeActive";
    }

    public void onResume(){
        super.onResume();
        state = "Active";
    }

}
