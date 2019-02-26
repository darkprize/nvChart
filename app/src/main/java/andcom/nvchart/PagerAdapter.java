package andcom.nvchart;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.NestedScrollView;
import android.view.ViewGroup;

import andcom.nvchart.TableView.Order.OrderTable;
import andcom.nvchart.TableView.Wait.WaitTable;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    WaitTable waitTable;
    OrderTable orderTable;

    public PagerAdapter(FragmentManager fm, int NumOfTabs,WaitTable waitTable, OrderTable orderTable) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.waitTable = waitTable;
        this.orderTable = orderTable;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                WaitTable tab1 = new WaitTable();
                return waitTable;
            case 1:

                OrderTable tab2 = new OrderTable();
                return orderTable;
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}