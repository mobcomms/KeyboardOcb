package com.enliple.offerwall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.enliple.keyboard.R;

public class OfferwallLoadingFragment extends Fragment {
    private static OfferwallLoadingFragment instance = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.aikbd_frame_offerwall_loading, container, false);
    }

    public static OfferwallLoadingFragment getInstance() {
        if (instance == null) {
            instance = new OfferwallLoadingFragment();
        }
        return instance;
    }
}
