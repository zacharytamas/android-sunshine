package com.zacharytamas.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView forecast = (TextView) view.findViewById(R.id.fragment_detail_forecast);
        forecast.setText(intent.getStringExtra(Intent.EXTRA_TEXT));

        return view;
    }
}
