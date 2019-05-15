package andcom.nvchart.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.cardview.widget.CardView;

public class FadeAnimation extends Animation {

    public final static int COLLAPSE = 1;
    public final static int EXPAND = 0;

    private View mView;
    private int mEndWidth;
    private int mType;
    private RelativeLayout.LayoutParams mLayoutParams;

    public FadeAnimation(View view,View oView, int duration, int type) {

        setDuration(duration);
        mView = view;
        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mEndWidth = mView.getMeasuredWidth();
        mLayoutParams = ((RelativeLayout.LayoutParams) view.getLayoutParams());
        mType = type;
        if(mType == EXPAND) {
            mLayoutParams.width = 0;
            mLayoutParams.leftMargin = oView.getLeft();
        } else {
            mLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        view.setVisibility(View.VISIBLE);
    }

    public int getWidth(){
        return mView.getWidth();
}

    public void setWidth(int width){
        mEndWidth = width;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        super.applyTransformation(interpolatedTime, t);
        if (interpolatedTime < 1.0f) {
            if(mType == EXPAND) {
                mView.setAlpha(interpolatedTime);
                mLayoutParams.width =  (int)(mEndWidth * interpolatedTime);
            } else {
                mView.setAlpha(1-interpolatedTime);

                mLayoutParams.width = (int) (mEndWidth * (1 - interpolatedTime));
            }
            mView.requestLayout();
        } else {
            if(mType == EXPAND) {

                mView.setAlpha(1f);
                mLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                mView.requestLayout();
            }else{
                mView.setVisibility(View.GONE);
            }
        }
    }

    protected void initView(){

        RelativeLayout relativeLayout = (RelativeLayout)((CardView)mView).getChildAt(0);
        LinearLayout temp =  (LinearLayout)relativeLayout.getChildAt(0);
        LinearLayout stroke =  (LinearLayout)temp.getChildAt(1);
        LinearLayout color =  (LinearLayout)temp.getChildAt(0);


    }
}