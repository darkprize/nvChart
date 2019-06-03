package andcom.nvchart.nvChart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;

import com.jpegkit.Jpeg;
import com.samsung.android.sdk.composer.document.textspan.SpenTextSpan;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenObjectImage;
import com.samsung.android.sdk.pen.document.SpenObjectStroke;
import com.samsung.android.sdk.pen.document.SpenObjectTextBox;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.document.textspan.SpenBackgroundColorSpan;
import com.samsung.android.sdk.pen.document.textspan.SpenComposingSpan;
import com.samsung.android.sdk.pen.document.textspan.SpenFontNameSpan;
import com.samsung.android.sdk.pen.document.textspan.SpenFontSizeSpan;
import com.samsung.android.sdk.pen.document.textspan.SpenForegroundColorSpan;
import com.samsung.android.sdk.pen.document.textspan.SpenLineSpacingParagraph;
import com.samsung.android.sdk.pen.document.textspan.SpenTextParagraphBase;
import com.samsung.android.sdk.pen.document.textspan.SpenTextSpanBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import andcom.nvchart.ASyncSocket;
import andcom.nvchart.Prefer;

public class NvData {
    private JSONObject jsonObject;
    private SpenPageDoc spenPageDoc;
    private Context context;
    private byte[] data;
    static private byte[] backImageData;
    static private File backImageFile;
    static private Jpeg jpg;

    static float ScaleLeft =1250f ;
    static float ScaleWidth =4029.681f ;
    static float ScaleTop =-671.6675f ;
    static float ScaleHeight =5698.928f ;
/*
    final static float TEXT_BOX_TOP_PADDING = 5.0f;
    final static float TEXT_BOX_LEFT_PADDING = 15.0f;
    */
    final static float TEXT_BOX_TOP_PADDING = 0f;
    final static float TEXT_BOX_LEFT_PADDING = 0f;

    public byte[] tempImage;

    final String BACK_IMAGE_START = "<---AndcomData_BackImage---\r\n";
    final String BACK_IMAGE_END = "\n---AndcomData_BackImage--->";
    final String IN_IMAGE_START = "<---AndcomData_Image";   //<---AndcomData_Image001---
    final String IN_IMAGE_END = "\n---AndcomData_Image";      //---AndcomData_Image001--->
    final String PACKET_END = "---AndcomData_END---";

    public String cJsonData;
    NvData(Context context, SpenPageDoc spenPageDoc,String msg){
        try{
            this.spenPageDoc = spenPageDoc;
            this.context = context;

            double socketTrasfer=System.currentTimeMillis();

            String ip = Prefer.getPrefString("key_ip","");
            int port = Integer.parseInt(Prefer.getPrefString("key_port","80"));

            ASyncSocket socket = new ASyncSocket((Activity)context,ip,port);
            data = socket.execute(msg).get();

            Log.e("SocketTranferTime","a"+String.format("%.4f",(System.currentTimeMillis()-socketTrasfer)/1000.0));

            String jsonData;

            int len = indexOf(data,BACK_IMAGE_START.getBytes(),0) ;
            if(len == -1 )
                len=data.length;
            jsonData = new String(data,0,len);
            makeBackImage();

            ScaleWidth = jpg.getWidth() * 5.08f;
            ScaleHeight = jpg.getHeight() * 5.08f;
            Log.e("jpgSize","w="+jpg.getWidth()+" h="+jpg.getHeight());
            Log.e("jpgScale","w="+ScaleWidth+" h="+ScaleHeight);
            Log.e("NvDataJsonData","JsonData = "+jsonData);

            this.cJsonData = jsonData;
            this.jsonObject = new JSONObject(jsonData);
        }catch (Exception je){
            je.printStackTrace();
        }
    }
    public NvData(Context context,byte[] data){
        this.data = data;
        this.context = context;
        String jsonData;

        int len = indexOf(data,BACK_IMAGE_START.getBytes(),0) ;
        if(len == -1 )
            len=data.length;
        jsonData = new String(data,0,len);
        makeBackImage();

        ScaleWidth = jpg.getWidth() * 5.08f;
        ScaleHeight = jpg.getHeight() * 5.08f;
        Log.e("jpgSize","w="+jpg.getWidth()+" h="+jpg.getHeight());
        Log.e("jpgScale","w="+ScaleWidth+" h="+ScaleHeight);
        Log.e("NvDataJsonData","JsonData = "+jsonData);

        this.cJsonData = jsonData;
        try {
            this.jsonObject = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String[] getPenData(){
        try{
            JSONArray jsonArray = jsonObject.getJSONArray("PenData");
            ArrayList<String> penData = new ArrayList<>();
            for(int i =0 ; i<jsonArray.length();i++){
                penData.add(jsonArray.getJSONObject(i).getString("BYTE"));
            }
            return penData.toArray(new String[penData.size()]);
        }catch (JSONException je){
            je.getStackTrace();
            return null;
        }
    }
    public String[] getTextData(int type){
        try{
            String jsonType;
            if(type==NvConstant.TEXT_OBJ)
                jsonType="TextBox";
            else
                jsonType="Label";
            JSONArray jsonArray = jsonObject.getJSONArray(jsonType);
            ArrayList<String> textData = new ArrayList<>();
            for(int i =0 ; i<jsonArray.length();i++){
                textData.add(jsonArray.getJSONObject(i).toString());
            }
            return textData.toArray(new String[textData.size()]);
        }catch (JSONException je){
            je.getStackTrace();
            return null;
        }
    }

    public void makeBackImage(){
            try{
            int start=indexOf(data,BACK_IMAGE_START.getBytes(),0)+BACK_IMAGE_START.getBytes().length;
            int end=indexOf(data,BACK_IMAGE_END.getBytes(),0);
            if(end-start <0){
                return;
            }
            backImageData = new byte[end-start];
            System.arraycopy(data,start,backImageData,0,end-start);
            jpg = new Jpeg(backImageData);

            tempImage = new byte[data.length-end];
            System.arraycopy(data,end,tempImage,0,data.length-end);

            long dueStart = System.currentTimeMillis();
            backImageFile = new File(context.getFilesDir()+File.separator+"System.arraycopy.jpg");
            if(!backImageFile.exists())
                backImageFile.createNewFile();
            FileOutputStream fos2 = new FileOutputStream(backImageFile);
            fos2.write(backImageData);
            fos2.flush();
            fos2.close();
            Log.d("arraycopy time","="+(System.currentTimeMillis()-dueStart)/1000.0 );

            /*dueStart = System.currentTimeMillis();
            byte[] backImage;
            backImage = Arrays.copyOfRange(data,start,end);
            File backImageFile = new File(context.getFilesDir()+File.separator+"Arrays.copyOfRange.jpg");
            if(!backImageFile.exists())
                backImageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(backImageFile);
            fos.write(backImage);
            fos.flush();
            fos.close();
            Log.e("path",backImageFile.getPath());
            //spenPageDoc.setBackgroundImage(backImageFile.getPath());
            Log.d("copyOfRange time","="+(System.currentTimeMillis()-dueStart)/1000.0 );*/


            }catch(IOException ie){
                ie.printStackTrace();
            }

    }


    public ArrayList<SpenObjectImage> getImageObjectData(){
        ArrayList<SpenObjectImage> lists = new ArrayList<>();

        //    final String IN_IMAGE_START = "<---AndcomData_Image";   //<---AndcomData_Image001---\r\n
        //    final String IN_IMAGE_END = "\n---AndcomData_Image";      // \n---AndcomData_Image001--->
        try{
            JSONArray array = jsonObject.getJSONArray("Image");

            for(int i =0;i<array.length();i++){
                String cStart = IN_IMAGE_START + array.getJSONObject(i).getString("INDEX")+"---\r\n";
                String cEnd =  IN_IMAGE_END + array.getJSONObject(i).getString("INDEX") +"---";

                int start=indexOf(data,cStart.getBytes(),0)+cStart.getBytes().length;
                int end=indexOf(data,cEnd.getBytes(),0);

                byte[] imageData = new byte[end-start];
                System.arraycopy(data,start,imageData,0, end-start);


                File fTemp = new File(context.getFilesDir()+File.separator+array.getJSONObject(i).getString("INDEX")+".jpg");
                if(!fTemp.exists())
                    fTemp.createNewFile();
                FileOutputStream fos = new FileOutputStream(fTemp);
                fos.write(imageData);
                fos.flush();
                fos.close();
                JSONObject jsonObject = array.getJSONObject(i);
                jsonObject.put("POSITION",(end-start)+"/"+array.getJSONObject(i).getString("POSITION"));
                RectF rect = makePosition(array.getJSONObject(i).getString("POSITION"));
                SpenObjectImage objImg = new SpenObjectImage();
                objImg.setImage(byteArrayToBitmap(imageData));
                //objImg.setImage(fTemp.getAbsolutePath());
                objImg.setRect(rect,true);

                lists.add(objImg);


            }
        }catch (JSONException je){
            je.printStackTrace();
        }catch(Exception ie){
            ie.printStackTrace();
        }

        return lists;
    }

    public static Bitmap byteArrayToBitmap(byte[] byteArray){

        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

        byteArray = null;
        return bitmap;
    }


    public ArrayList<SpenObjectTextBox> getTextObjectData(int type){
        ArrayList<SpenObjectTextBox> lists = new ArrayList<>();
        if(getTextData(type)==null){
            return lists;
        }
        if(!(getTextData(type).length>0)){
            return lists;
        }
        for(String text : getTextData(type)){

            if(text.length()>20){
                try{
                    JSONObject jsonObject = new JSONObject(text);
                    int color = jsonObject.getInt("COLOR");
                    if(type==NvConstant.LABEL_OBJ && color==1){
                        color = 0;
                    }
                    //String[] position = jsonObject.getString("POSITION").split("/");
                    String data = jsonObject.getString("DESC");
                    String line = System.getProperty("line.separator");
                    data = data.replace("\r","");
                    jsonObject.put("POSITION",data.length()+"/"+jsonObject.getString("POSITION"));

                    SpenObjectTextBox textObj = new SpenObjectTextBox(data);
                    //RectF textPoint = getRealPoint(x1,y1,0,0);
                    RectF textPoint = makePosition(jsonObject.getString("POSITION"),type);

                    ArrayList<SpenTextSpanBase> spans = new ArrayList<SpenTextSpanBase>();
                    SpenFontNameSpan fontNameSpan = new SpenFontNameSpan();
                    SpenForegroundColorSpan colorSpan = new SpenForegroundColorSpan();
                    SpenFontSizeSpan sizeSpan = new SpenFontSizeSpan();

                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ File.separator+"BOSANOVN.TTF");

                    fontNameSpan.setName("SECLao-Bold.ttf");
                    //fontNameSpan.setName(file.getAbsolutePath());
                    sizeSpan.setSize(18);
                    colorSpan.setColor(NvConstant.getColor(3,color));

                    spans.add(fontNameSpan);
                    spans.add(colorSpan);
                    spans.add(sizeSpan);

                    for(SpenTextSpanBase span : spans){
                        span.setPosition(0,data.length());
                    }
                    Log.e("RectF-1",data+textPoint.toString());

                    textObj.setFontSize(18);
                    textObj.setRect(textPoint,true);
                    textObj.setMargin(0,0,0,0);

                    ArrayList<SpenTextParagraphBase> paragraphBase = new ArrayList<>();
                    SpenLineSpacingParagraph lineSpacingParagraph = new SpenLineSpacingParagraph();
                    lineSpacingParagraph.setLineSpacingType(SpenLineSpacingParagraph.TYPE_PIXEL);
                    lineSpacingParagraph.setLineSpacing(0);
                    lineSpacingParagraph.setPosition(0,data.length());
                    paragraphBase.add(lineSpacingParagraph);

                    textObj.setTextParagraph(paragraphBase);

                    //textObj.setTextColor(getColor(jsonObject.getInt("COLOR"),3));
                    //Log.e("FontName",textObj.getFont());
                    textObj.setTextSpan(spans);
                    lists.add(textObj);
                }catch (JSONException je){
                    je.printStackTrace();
                }

            }
            /*
            if(text.length()>20){
                try{
                    JSONObject jsonObject = new JSONObject(text);
                    int color = jsonObject.getInt("COLOR");
                    if(type==NvConstant.LABEL_OBJ && color==1){
                        color = 0;
                    }
                    //String[] position = jsonObject.getString("POSITION").split("/");
                    String data = jsonObject.getString("DESC");
                    String line = System.getProperty("line.separator");
                    data = data.replace("\r","");
                    jsonObject.put("POSITION",data.length()+"/"+jsonObject.getString("POSITION"));

                    SpenObjectTextBox textObj = new SpenObjectTextBox(data);
                    //RectF textPoint = getRealPoint(x1,y1,0,0);
                    RectF textPoint = makePosition(jsonObject.getString("POSITION"),type);

                    ArrayList<SpenTextSpanBase> spans = new ArrayList<SpenTextSpanBase>();
                    SpenFontNameSpan fontNameSpan = new SpenFontNameSpan();
                    SpenForegroundColorSpan colorSpan = new SpenForegroundColorSpan();
                    SpenFontSizeSpan sizeSpan = new SpenFontSizeSpan();
                    SpenLineSpacingParagraph lineSpacingParagraph = new SpenLineSpacingParagraph();
                    lineSpacingParagraph.setLineSpacingType(SpenLineSpacingParagraph.TYPE_PIXEL);
                    lineSpacingParagraph.setLineSpacing(0);

                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ File.separator+"BOSANOVN.TTF");

                    fontNameSpan.setName("/system/fonts/ComingSoon.ttf");
                    //fontNameSpan.setName(file.getAbsolutePath());
                    sizeSpan.setSize(18);
                    colorSpan.setColor(NvConstant.getColor(3,color));

                    //spans.add(fontNameSpan);
                    spans.add(colorSpan);
                    spans.add(sizeSpan);

                    for(SpenTextSpanBase span : spans){
                        span.setPosition(0,data.length());
                    }
                    Log.e("RectF-1",data+textPoint.toString());

                    textObj.setFontSize(18);
                    textObj.setRect(textPoint,true);
                    textObj.setMargin(0,0,0,0);


                    //textObj.setTextColor(getColor(jsonObject.getInt("COLOR"),3));
                    //Log.e("FontName",textObj.getFont());
                    textObj.setTextSpan(spans);
                    lists.add(textObj);
                }catch (JSONException je){
                    je.printStackTrace();
                }

            }*/
        }


        return lists;
    }

    public ArrayList<SpenObjectStroke> getPenObjectData(){
        ArrayList<SpenObjectStroke> lists = new ArrayList<>();
        if(getPenData()==null){
            return lists;
        }
        if(!(getPenData().length>0)){
            return lists;
        }
        for(String stroke : getPenData()){
            Log.e("stroke",stroke);
            SpenSettingPenInfo penInfo = new SpenSettingPenInfo();;
            if(stroke.length()>8){
                stroke = stroke.substring(0,stroke.length()-(stroke.length()-4)%8);
                int pointSize = (stroke.length()-4)/8;
                float[][] strokePoint = new float[pointSize][2];
                PointF[] points = new PointF[pointSize];
                float[] pressures = new float[pointSize];
                int[] timestamps = new int[pointSize];

                                /*for (int i = 0; i < pointSize; i++) {
                                    points[i] = new PointF();
                                    points[i].x = strokePoint[i][0];
                                    points[i].y = strokePoint[i][1];
                                    pressures[i] = 1;
                                    timestamps[i] = (int) android.os.SystemClock .uptimeMillis();
                                }*/

                int index=0;
                for(int i=0 ; i<stroke.length();i+=4){
                    if(i>0){

                        if((i/4)%2 == 1){//x 좌표
                            points[index] = new PointF();
                            points[index].x=(getHexToDecInt(stroke.substring(i,i+4))-ScaleLeft)*jpg.getWidth()/ScaleWidth;
                        }else if((i/4)%2 == 0){//y좌표
                            points[index].y=(getHexToDecInt(stroke.substring(i,i+4))-ScaleTop)*jpg.getHeight()/ScaleHeight;
                            pressures[index] = 0;
                            timestamps[index] = (int) android.os.SystemClock .uptimeMillis();
                            index++;
                        }


                    }else{
                        Log.d(i+"",stroke.substring(i,i+4));

                        int size;
                        int pen;
                        int color;

                        size = (int)getHexToDec(stroke.substring(0,2))%10;
                        pen = (int)getHexToDec(stroke.substring(0,2))/10;
                        color = (int)(getHexToDec(stroke.substring(2,4)));
                        Log.d("penTest","size : " + size + " pen : " + pen + " color : "+color);

                        if(pen == 1){  //InkPen
                            penInfo.size = NvConstant.getPenSize(pen,size);
                            penInfo.name = "com.samsung.android.sdk.pen.pen.preload.InkPen";
                            penInfo.color = NvConstant.getColor(pen,color);
                        }else if(pen==2){      //형광펜
                            penInfo.size = NvConstant.getPenSize(pen,size);
                            penInfo.name = "com.samsung.android.sdk.pen.pen.preload.Marker";
                            penInfo.color = NvConstant.getColor(pen,color);
                        }


                    }
                }




                SpenObjectStroke strokeObj = new SpenObjectStroke(penInfo.name,points,pressures,timestamps);
                strokeObj.setPenSize(penInfo.size);
                strokeObj.setColor(penInfo.color);

                lists.add(strokeObj);
                //mSpenSurfaceView.update();
            }

        }
        return lists;
    }
    private float getHexToDec(String hex) {
        float v = Long.parseLong(hex, 16);

        return v;
    }

    private int getHexToDecInt(String hex) {
        int v = (short)Integer.parseInt(hex,16);

        return v;
    }
    public int indexOf(byte[] outerArray, byte[] smallerArray,int from) {
        long start = System.currentTimeMillis();
        for(int i = from; i < outerArray.length - smallerArray.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i+j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                long end = System.currentTimeMillis();

                Log.e("binarySearch",new String(smallerArray)+" " + String.format("%.4f",(end-start)/1000.0000f));
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Integer> indexOfArray(byte[] outerArray, byte[] smallerArray) {
        ArrayList<Integer> array = new ArrayList<>();
        for(int i = 0; i < outerArray.length - smallerArray.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i+j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) array.add(i);
        }
        return array;
    }

    private float convertX(float src){
        float convertValue ;
        convertValue = (src-ScaleLeft)*jpg.getWidth()/ScaleWidth;
        Log.e("convertX",""+convertValue);
        return convertValue;
    }
    private float convertY(float src){
        float convertValue ;
        convertValue = (src-ScaleTop)*jpg.getHeight()/ScaleHeight;
        return convertValue;
    }private float reversConvertX(float src){
        float convertValue ;
        convertValue= src*ScaleWidth/jpg.getWidth()+ScaleLeft ;
        return convertValue;
    }
    private float reversConvertY(float src){
        float convertValue ;
        convertValue= src*ScaleHeight/jpg.getHeight()+ScaleTop ;
        return convertValue;
    }

    public RectF makePosition(String src,int type){
        String[] position = src.split("/");
        RectF rect = new RectF();
        if(type==NvConstant.TEXT_OBJ){
            float x1 = Float.parseFloat(position[1]);
            x1=convertX(x1)-TEXT_BOX_LEFT_PADDING;

            float y1 = Float.parseFloat(position[2]);
            y1=convertY(y1)-TEXT_BOX_TOP_PADDING;

            float x2 = Float.parseFloat(position[3]);
            //x2=convertX(x2,4029,1250);
            x2 = x2*jpg.getWidth()/ScaleWidth+TEXT_BOX_LEFT_PADDING*2;

            float y2 = Float.parseFloat(position[4]);
            //y2=convertY(y2,5698,-670);
            y2 = y2*jpg.getHeight()/ScaleHeight+TEXT_BOX_TOP_PADDING*2;

            rect.set(x1,y1,x1+x2,y1+y2);
        }else{  //Label일 경우
            float x1 = Float.parseFloat(position[1]);
            x1=convertX(x1);

            float y1 = Float.parseFloat(position[2]);
            y1=convertY(y1);

            rect.set(x1-TEXT_BOX_LEFT_PADDING,y1-TEXT_BOX_TOP_PADDING,x1+(Integer.parseInt(position[0])*25),y1+300);
        }
        Log.e("Rect1",rect.toString());
        return rect;
    }
    public RectF makePosition(String src){
        String[] position = src.split("/");
        RectF rect = new RectF();

        float x1 = Float.parseFloat(position[1]);
        x1=convertX(x1);

        float y1 = Float.parseFloat(position[2]);
        y1=convertY(y1);

        float x2 = Float.parseFloat(position[3]);
        //x2=convertX(x2,4029,1250);
        x2 = x2*jpg.getWidth()/ScaleWidth;

        float y2 = Float.parseFloat(position[4]);
        //y2=convertY(y2,5698,-670);
        y2 = y2*jpg.getHeight()/ScaleHeight;

        rect.set(x1,y1,x1+x2,y1+y2);


        return rect;
    }
    public String getOriginPosition(RectF rect,int type){
        String cPos = new String();
        if(type==NvConstant.TEXT_OBJ){
            float x1 = rect.left;
            x1 = reversConvertX(x1+ TEXT_BOX_LEFT_PADDING) ;
            float y1 = rect.top;
            y1 = reversConvertY(y1+ TEXT_BOX_TOP_PADDING) ;
            float x2 = rect.right-rect.left;
            x2 = (x2 - TEXT_BOX_LEFT_PADDING*2)*ScaleWidth/jpg.getWidth();
            float y2 = rect.bottom - rect.top;
            y2 = (y2 - TEXT_BOX_TOP_PADDING*2)*ScaleHeight/jpg.getHeight();
            y2 = rect.bottom;
            cPos = String.format("%.0f",x1) + "/" + String.format("%.0f",y1) + "/" + String.format("%.0f",x2) + "/" + String.format("%.0f",y2) ;
        }else if(type==NvConstant.IMAGE_OBJ){

            float x1 = rect.left;
            x1=reversConvertX(x1);

            float y1 = rect.top;
            y1=reversConvertY(y1);

            float x2 = rect.right-rect.left;
            //x2=convertX(x2,4029,1250);
            x2 = x2*ScaleWidth/jpg.getWidth();

            float y2 = rect.bottom - rect.top;
            //y2=convertY(y2,5698,-670);
            y2 = y2*ScaleHeight/jpg.getHeight();
            cPos = String.format("%.0f",x1) + "/" + String.format("%.0f",y1) + "/" + String.format("%.0f",x2) + "/" + String.format("%.0f",y2) ;

        }

        Log.e("cPos",cPos);
        return cPos;
    }
    public Jpeg getBackImageJpg(){
        if(backImageData == null){
            return null;
        }else{
            return jpg;
        }
    }

    public File getBackImageFile(){
        return backImageFile;
    }

    public double getDataSize(){
        return data.length;
    }


}
