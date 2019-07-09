package andcom.nvchart.TableView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import andcom.nvchart.MainActivity;
import andcom.nvchart.PagerAdapter;
import andcom.nvchart.R;
import andcom.nvchart.TableView.Order.OrderTable;
import andcom.nvchart.TableView.Wait.WaitTable;
import andcom.nvchart.util.ClickItem;
import andcom.nvchart.util.SocketCheck;
import es.dmoral.toasty.Toasty;

public class WaitingBoardDialog extends DialogFragment implements ClickItem {
    public WaitingBoardDialog() {}


    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    OrderTable orderTable;
    WaitTable waitTable;

    Context context;

    static DatePickerDialog datePickerDialog;
    TextView dateView;
    ImageButton btnRefresh;
    View view;
    int height;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.waiting_board, container);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.context = getContext();

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getDialog().getWindow().setLayout(getActivity().getWindow().getAttributes().width,view.getMeasuredHeight());
                Log.e("height","width " +getActivity().getWindow().getAttributes().width +" height " + view.getMeasuredHeight());
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if(SocketCheck.connectionCheck()){
                    initView();
                }else{

                    Toasty.error(context,"서버에 접속 할 수 없습니다.").show();
                }


            }
        });

    }

    public void initView(){
        datePickerDialog = new DatePickerDialog(context);
        dateView =getView().findViewById(R.id.date);
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.e("datePicker","date = " + year+month+dayOfMonth);
                        Calendar selectCal = Calendar.getInstance();
                        selectCal.set(year,month,dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (E)");
                        String date = sdf.format(selectCal.getTime());
                        dateView.setText(date);
                        refresh();
                    }
                });
                datePickerDialog.show();
            }
        });
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (E)");
        dateView.setText(sdf.format(date));

        tabLayout = getView().findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText("대기현황"));
        tabLayout.addTab(tabLayout.newTab().setText("진료현황"));

        viewPager = getView().findViewById(R.id.viewpager);
        waitTable = new WaitTable();
        orderTable = new OrderTable();
        waitTable.setClickItem(this);

        orderTable.setClickItem(this);
        pagerAdapter = new PagerAdapter(getChildFragmentManager(), tabLayout.getTabCount(), waitTable, orderTable);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                refresh();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    waitTable.refreshDataAsync(getSelectedDate());

                }else{
                    orderTable.refreshDataAsync(getSelectedDate());

                }
            }
        });
        waitTable = (WaitTable) pagerAdapter.getItem(0);
        orderTable = (OrderTable) pagerAdapter.getItem(1);

        btnRefresh = getView().findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    public void refresh(){
        if(SocketCheck.connectionCheck()){

            waitTable.refreshDataAsync(getSelectedDate());
            orderTable.refreshDataAsync(getSelectedDate());
        }else{

            Toasty.error(context,"서버에 접속 할 수 없습니다.").show();
        }
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

    @Override
    public void click(){
        dismiss();
        Log.e("wait click","wait click delegate");
    }
}
