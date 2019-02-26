package andcom.nvchart.nvChart;

import android.graphics.Color;

class NvConstant {
    static final int KEYBOARD=0;
    static final int PEN =1;
    static final int IMAGE=2;
    static final int MARKER=3;
    static final int SELECT=4;
    static final int ERAASER=5;

    static final int TEXT_OBJ = 11;
    static final int LABEL_OBJ = 12;
    static final int IMAGE_OBJ = 13;

    static final int MARKER_BLACK = 1277502757;
    static final int MARKER_RED = 1291076406;
    static final int MARKER_BLUE = 1275279568;
    static final int MARKER_GREEN = 1279050555;
    static final int MARKER_YELLOW = 1291840315;
    static final int MARKER_PURPLE = 1286013904;
    static final int MARKER_PINK = 1291811019;

    static final int PEN_BLACK = 0;
    static final int PEN_RED = 16711680;
    static final int PEN_BLUE = 255;
    static final int PEN_GREEN = 65280;
    static final int PEN_YELLOW = 16776960;
    static final int PEN_PURPLE = 9699539;
    static final int PEN_PINK = 16739761;

    static final int TEXT_BLACK = -16777216;
    static final int TEXT_RED = -65536;
    static final int TEXT_BLUE = -16776961;
    static final int TEXT_GREEN = -16711936;
    static final int TEXT_YELLOW = -256;
    static final int TEXT_PURPLE = -7077677;
    static final int TEXT_PINK = -38476;

    static final float PEN_SIZE_1 = 2;
    static final float PEN_SIZE_2 = 4;
    static final float PEN_SIZE_3 = 6;

    static final float MARKER_SIZE_1 = 7;
    static final float MARKER_SIZE_2 = 14;
    static final float MARKER_SIZE_3 = 21;

    public static String getPenColorCode(int value){
        String code = new String();
        switch (value){
            case PEN_BLACK :code="0";
                break;
            case PEN_RED :code="12";
                break;
            case PEN_BLUE :code="9";
                break;
            case PEN_GREEN :code="10";
                break;
            case PEN_YELLOW :code="14";
                break;
            case PEN_PURPLE :code="7";
                break;
            case PEN_PINK :code="15";
                break;
            case MARKER_BLACK :code="0";
                break;
            case MARKER_RED :code="12";
                break;
            case MARKER_BLUE :code="9";
                break;
            case MARKER_GREEN :code="10";
                break;
            case MARKER_YELLOW :code="14";
                break;
            case MARKER_PURPLE :code="7";
                break;
            case MARKER_PINK :code="15";
                break;
            case TEXT_BLACK :code="0";
                break;
            case TEXT_RED :code="1";
                break;
            case TEXT_BLUE :code="2";
                break;
            case TEXT_GREEN :code="3";
                break;
            case TEXT_YELLOW :code="4";
                break;
            case TEXT_PURPLE :code="5";
                break;
            case TEXT_PINK :code="6";
                break;
            default:code="0";
                break;
        }
        return code;
    }

    public static int getColor(int value,int type){
        int color=1;
        switch (type){
            case 1 : //pen  //
                switch (value){
                    case 7 :    //보라
                        color = NvConstant.PEN_PURPLE;
                        break;
                    case 9 :    //파랑
                        color = NvConstant.PEN_BLUE;
                        break;
                    case 10 :   //초록
                        color = NvConstant.PEN_GREEN;
                        break;
                    case 12 :   //빨강
                        color = NvConstant.PEN_RED;
                        break;
                    case 14 :   //노랑
                        color = NvConstant.PEN_YELLOW;
                        break;
                    case 15 :   //핑크
                        color = NvConstant.PEN_PINK;
                        break;
                    case 0 :    //검정
                        color = NvConstant.PEN_BLACK;
                        break;
                    default:
                        break;
                }
                break;
            case 2 : //marker
                switch (value){
                    case 7 :    //보라
                        color = NvConstant.MARKER_PURPLE;
                        break;
                    case 9 :    //파랑
                        color = NvConstant.MARKER_BLUE;
                        break;
                    case 10 :   //초록
                        color = NvConstant.MARKER_GREEN;
                        break;
                    case 12 :   //빨강
                        color = NvConstant.MARKER_RED;
                        break;
                    case 14 :   //노랑
                        color = NvConstant.MARKER_YELLOW;
                        break;
                    case 15 :   //핑크
                        color = NvConstant.MARKER_PINK;
                        break;
                    case 0 :    //검정
                        color = NvConstant.MARKER_BLACK;
                        break;
                    default:
                        break;
                }
                break;
            case 3 : //text
                switch (value){
                    case 0 :    //검정
                        color = NvConstant.TEXT_BLACK;
                        break;
                    case 1 :    //빨강
                        color = NvConstant.TEXT_RED;

                        break;
                    case 2 :   //파랑
                        color = NvConstant.TEXT_BLUE;

                        break;
                    case 3 :   //초록
                        color = NvConstant.TEXT_GREEN;

                        break;
                    case 4 :   //노랑
                        color = NvConstant.TEXT_YELLOW;

                        break;
                    case 5 :   //보라
                        color = NvConstant.TEXT_PURPLE;

                        break;
                    case 6 :    //핑크
                        color = NvConstant.TEXT_PINK;

                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return color;
    }

    public static float getPenSize(int pen, int value){
        float size = new Float(1);
        switch(pen){
            case 1 :    //pen
                switch (value){
                    case 1 :    size = PEN_SIZE_1;
                        break;
                    case 2:     size = PEN_SIZE_2;
                        break;
                    case 3:     size = PEN_SIZE_3;
                        break;
                }
                break;
            case 2 :    //marker
                switch (value){
                    case 1 :    size = MARKER_SIZE_1;
                        break;
                    case 2:     size = MARKER_SIZE_2;
                        break;
                    case 3:     size = MARKER_SIZE_3;
                        break;
                }
            break;
        }
        return size;
    }

    public static String getPenSizeCode(String pen, float size){
        String sizeCode = new String("1");
        switch(pen){
            case "1" :    //pen
                switch ((int)size){
                    case (int)PEN_SIZE_1 :    sizeCode = "1";
                        break;
                    case (int)PEN_SIZE_2:     sizeCode = "2";
                        break;
                    case (int)PEN_SIZE_3:     sizeCode = "3";
                        break;
                }
            case "2" :    //marker
                switch ((int)size){
                    case (int)MARKER_SIZE_1 :   sizeCode = "1";
                        break;
                    case (int)MARKER_SIZE_2:    sizeCode = "2";
                        break;
                    case (int)MARKER_SIZE_3:    sizeCode = "3";
                        break;
                }

        }
        return sizeCode;
    }
}
