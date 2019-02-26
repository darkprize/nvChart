package andcom.nvchart.TableView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import com.evrencoskun.tableview.TableView;

import andcom.nvchart.R;

public class MainFrameTable extends Fragment {

    private static final String FRAGMENT_NAME = "TABLE";
    private TableView mTableView;
    private ProgressBar mProgressBar;

    public MainFrameTable(){
        Log.e("WaitTableConstruct","Load MainTableFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.main_frame_table, container, false);
        return view;
    }

    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTableView.setVisibility(View.INVISIBLE);
    }

    public void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mTableView.setVisibility(View.VISIBLE);
    }

    public String getName() {
        return FRAGMENT_NAME;
    }
}
