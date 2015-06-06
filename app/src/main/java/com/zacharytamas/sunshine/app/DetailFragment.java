package com.zacharytamas.sunshine.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zacharytamas.sunshine.app.data.WeatherContract.WeatherEntry;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FORECAST_DETAIL_LOADER = 100;
    private Uri mUri;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(MainActivity.DETAIL_URI);
            getLoaderManager().initLoader(FORECAST_DETAIL_LOADER, null, this);
        }

        return view;
    }

    private void updateUI(Cursor cursor) {
//        TextView forecast = (TextView) getView().findViewById(R.id.fragment_detail_forecast);
//        forecast.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)));

        boolean isMetric = Utility.isMetric(getActivity());

        String date = Utility.getFriendlyDayString(getActivity(),
                cursor.getLong(cursor.getColumnIndex(WeatherEntry.COLUMN_DATE)));
        ((TextView) getView().findViewById(R.id.detail_day_textview)).setText(date);

        String high = Utility.formatTemperature(getActivity(),
                cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP)), isMetric);
        ((TextView) getView().findViewById(R.id.detail_high_textview)).setText(high);

        String low = Utility.formatTemperature(getActivity(),
                cursor.getDouble(cursor.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP)), isMetric);
        ((TextView) getView().findViewById(R.id.detail_low_textview)).setText(low);

        String desc = cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC));
        ((TextView) getView().findViewById(R.id.detail_forecast_textview)).setText(desc);

        int weatherId = cursor.getInt(cursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID));
        ImageView imageView = (ImageView) getView().findViewById(R.id.detail_icon);
        imageView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        // Read humidity from cursor and update view
        float humidity = cursor.getFloat(cursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY));
        TextView humidityView = (TextView) getView().findViewById(R.id.detail_humidity_textview);
        humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

        // Read wind speed and direction from cursor and update view
        float windSpeedStr = cursor.getFloat(cursor.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED));
        float windDirStr = cursor.getFloat(cursor.getColumnIndex(WeatherEntry.COLUMN_DEGREES));
        TextView windView = (TextView) getView().findViewById(R.id.detail_wind_textview);
        windView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

        // Read pressure from cursor and update view
        float pressure = cursor.getFloat(cursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE));
        TextView pressureView = (TextView) getView().findViewById(R.id.detail_pressure_textview);
        pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));
    }

    public void onLocationChanged(String location) {
        Uri uri = mUri;

        if (uri != null) {
            long date = WeatherEntry.getDateFromUri(uri);
            mUri = WeatherEntry.buildWeatherLocationWithDate(location, date);
            getLoaderManager().restartLoader(FORECAST_DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(getActivity(), mUri, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            this.updateUI(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
