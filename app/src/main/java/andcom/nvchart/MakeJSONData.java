package andcom.nvchart;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by csy on 2018-03-23.
 */

public class MakeJSONData {

    public static JSONObject get(int what, String...params){
        JSONObject jsonObject = new JSONObject();
        String protocol = "andcom3";
        try{
            String uuid = Prefer.getPrefString("uuid","");
            if(uuid.equals("")){
                uuid = PhoneState.getPhoneNumber(MainActivity.context);
            }
            jsonObject.put("UserPhone",uuid);
            switch (what){
                case MsgType.SEARCH_BY_NAME :
                    protocol = "andcom2";
                    jsonObject.put("수진자명",params[0].toString());
                    break;
                case MsgType.SEARCH_BY_CHARTNO :
                    protocol = "andcom2";
                    jsonObject.put("차트번호",params[0].toString());
                    break;
                case MsgType.FIND_AGREE_TOTAL_NUM :
                    jsonObject.put("차트번호",params[0].toString());
                    jsonObject.put("동의서구분",params[1].toString());
                    jsonObject.put("페이지","0");
                    break;
                case MsgType.LOAD_AGREE_BY_NUM :
                    jsonObject.put("차트번호",params[0].toString());
                    jsonObject.put("동의서구분",params[1].toString());
                    jsonObject.put("페이지",params[2].toString());
                    break;
                case MsgType.DELE_AGREE_BY_NUM :
                    jsonObject.put("차트번호",params[0].toString());
                    jsonObject.put("동의서구분",params[1].toString());
                    jsonObject.put("페이지삭제",params[2].toString());
                    break;
                case MsgType.SEND_SIGN_DATA :
                    jsonObject.put("차트번호",params[0].toString());
                    jsonObject.put("동의서구분",params[1].toString());
                    jsonObject.put("서명데이터",params[2].toString());
                    break;
                case MsgType.GET_DOCTOR_LIST:
                    jsonObject.put("치과의사검색","1");
                    break;
                case MsgType.SEARCH_BY_REGISTLIST:
                    jsonObject.put("접수일자",params[0]);
                    break;
                case MsgType.SEND_IMAGE:
                    jsonObject.put("ChartNo",params[0].toString());
                    jsonObject.put("FileLen",params[1].toString());
                    break;
                case MsgType.LOAD_DBLIST:
                    jsonObject.put("구분코드","1");

                    break;
                case MsgType.LOAD_NVLIST:
                    jsonObject.put("구분코드","2");
                    jsonObject.put("DB",params[0].toString());
                    jsonObject.put("ND_CHARTNO","");
                    jsonObject.put("HIDDEN_FORM","0");


                break;
                default:
                    break;
            }
            jsonObject.put("User",protocol);

        }catch(JSONException je){

        }

        return jsonObject;
    }
}
