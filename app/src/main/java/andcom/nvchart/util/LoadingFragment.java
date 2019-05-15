package andcom.nvchart.util;

import android.view.View;

import andcom.nvchart.R;
import androidx.fragment.app.Fragment;

public class LoadingFragment extends Fragment {
    public void showLoader() {

        if (getView() != null) {
            View progressBar = getView().findViewById(R.id.loading);
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    public void hideLoader() {

        if (getView() != null) {
            View progressBar = getView().findViewById(R.id.loading);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
