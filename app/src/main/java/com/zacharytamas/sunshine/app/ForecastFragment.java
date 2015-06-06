package com.zacharytamas.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.zacharytamas.sunshine.app.data.WeatherContract.WeatherEntry;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ForecastAdapter mAdapter;
    private SharedPreferences mSharedPreferences;

    public static final int FORECAST_LOADER = 2610;

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

        mAdapter = new ForecastAdapter(getActivity(), null, 0);
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) view.findViewById(R.id.listview_forecast);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);

                if (cursor != null) {
                    Uri uri = WeatherEntry.buildWeatherLocationWithDate(
                            Utility.getPreferredLocation(getActivity()),
                            cursor.getLong(ForecastAdapter.COL_WEATHER_DATE));
                    ((Callback) getActivity()).onItemSelected(uri);
                }
            }
        });

        return view;
    }

    public void onLocationChanged() {
//        fetchWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    public void fetchWeather() {
        new FetchWeatherTask(getActivity()).execute(Utility.getPreferredLocation(getActivity()));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String preferredLocation = Utility.getPreferredLocation(getActivity());
        String orderBy = WeatherEntry.COLUMN_DATE + " ASC";

        Uri uri = WeatherEntry.buildWeatherLocationWithStartDate(preferredLocation,
                System.currentTimeMillis());

        return new CursorLoader(getActivity(), uri,
                ForecastAdapter.FORECAST_COLUMNS, null, null, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }
}
