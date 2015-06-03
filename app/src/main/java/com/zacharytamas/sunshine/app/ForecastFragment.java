package com.zacharytamas.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mArrayAdapter;
    private SharedPreferences mSharedPreferences;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            this.fetchWeather();
            return true;
        } else if (id == R.id.action_map) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String geo = preferences.getString(getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default));

            Uri uri = new Uri.Builder()
                    .scheme("geo")
                    .appendPath("0,0")
                    .appendQueryParameter("q", geo)
                    .build();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);

            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                // There is an available application to handle this
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "You don't seem to have an app for that.",
                        Toast.LENGTH_LONG).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mArrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>()
        );

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listview_forecast);
        listView.setAdapter(mArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String forecast = (String) adapterView.getAdapter().getItem(i);

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });

        this.fetchWeather();

        return view;
    }

    public void fetchWeather() {
        new FetchWeatherTask(getActivity(), mArrayAdapter);
    }

}
