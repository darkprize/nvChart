package andcom.nvchart.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import andcom.nvchart.R;

public class SaveDialog extends Dialog {
    private Button btnSave;
    private Button btnCancel;
    private Button btnNotSave;
    private View.OnClickListener mSaveListener;
    private View.OnClickListener mNotSaveListener;
    private View.OnClickListener mCancelListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("SaveDialog","OnCreate");
        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.save_dialog);

        //셋팅
        btnSave =(Button)findViewById(R.id.save);
        btnNotSave =(Button)findViewById(R.id.notsave);
        btnCancel =(Button)findViewById(R.id.cancel);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        btnSave.setOnClickListener(mSaveListener);
        btnNotSave.setOnClickListener(mNotSaveListener);
        btnCancel.setOnClickListener(mCancelListener);
    }

    //생성자 생성
    public SaveDialog(@NonNull Context context, View.OnClickListener mSaveListener, View.OnClickListener mNotSaveListener,View.OnClickListener mCancelListener) {
        super(context);
        this.mSaveListener = mSaveListener;
        this.mNotSaveListener = mNotSaveListener;
        this.mCancelListener = mCancelListener;
    }


}
