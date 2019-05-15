package andcom.nvchart;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class MyBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    private boolean mLocked = true;

    public MyBottomSheetBehavior() {}

    public MyBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLocked(boolean locked) {
        mLocked = locked;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        boolean handled = false;

        if (!mLocked) {
            handled = super.onInterceptTouchEvent(parent, child, event);
        }

        return handled;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        boolean handled = false;

        if (!mLocked) {
            handled = super.onTouchEvent(parent, child, event);
        }
        if(child.getId()==R.id.bottom_tool_bar){
            handled = super.onTouchEvent(parent, child, event);
            Log.d("bsb","bottom tool bar");
        }

        return handled;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child, View directTargetChild, View target, int nestedScrollAxes) {
        boolean handled = false;

        if (!mLocked) {
            handled = super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
        }

        return handled;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed) {
        if (!mLocked) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        if (!mLocked) {
            super.onStopNestedScroll(coordinatorLayout, child, target);
        }
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY) {
        boolean handled = false;

        if (!mLocked) {
            handled = super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
        }

        return handled;

    }
}