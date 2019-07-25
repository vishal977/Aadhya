package com.heisenberg.beherchange;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static com.heisenberg.beherchange.UnsafeLocations.ALT_HEATMAP_GRADIENT;
import static com.heisenberg.beherchange.UnsafeLocations.ALT_HEATMAP_OPACITY;

public class AnalyseLocations extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private HashMap<String, DataSet> mLists = new HashMap<String, DataSet>();
    LinearLayout short_analysis;
    Button det_rep;
    TextView place_name,crime_label,user_label;
    public String finalRes = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_analyse_locations);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_analyse_location);
        mapFragment.getMapAsync(this);

        place_name = findViewById(R.id.place_name);
        crime_label = findViewById(R.id.crime_label);
        user_label = findViewById(R.id.user_label);
        short_analysis = findViewById(R.id.short_analysis);
        det_rep = findViewById(R.id.det_rep);

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.analyse_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //mMap.clear();
                short_analysis.setVisibility(View.VISIBLE);
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.5f));
                showAnalysisDetail(place.getName().toString());
                /*destn = place.getLatLng();
                place1=place.getName().toString();*/
            }

            @Override
            public void onError(Status status) {

            }
        });
        det_rep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialogFragment();
            }
        });
    }


    private void showAnalysisDetail(String s) {
        /*mMap.setMaxZoomPreference(12.5f);
        mMap.setMinZoomPreference(8.5f);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);*/
        place_name.setText(""+s);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        InputStream is = null;
        String line = null;
        String result = null;
        String lvl = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://be-her-change.herokuapp.com/search?location="+s);
//            httpPost.setParams();
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
            result = "["+result+"]";
            is.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            JSONArray jArray = new JSONArray(result);
            int count = jArray.length();
            for (int i = 0; i < count; i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                lvl += json_data.getString("result");
            }
            crime_label.setText("Crime Based Review - "+lvl+"/5");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(42.315276,  -71.080842), 11));
        try {
            mLists.put(getString(R.string.unsafelocation), new DataSet(readItems(R.raw.unsafeboston)));
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show();
        }

        if (mProvider == null) {
            mProvider = new HeatmapTileProvider.Builder().data(
                    mLists.get(getString(R.string.unsafelocation)).getData()).build();
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            mProvider.setGradient(ALT_HEATMAP_GRADIENT);
            mProvider.setOpacity(ALT_HEATMAP_OPACITY);
        } else {
            mProvider.setGradient(ALT_HEATMAP_GRADIENT);
            mProvider.setOpacity(ALT_HEATMAP_OPACITY);
            try {
                mProvider.setData(mLists.get(new DataSet(readItems(R.raw.unsafeboston))).getData());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mOverlay.clearTileCache();
        }
    }
    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = Double.parseDouble(object.getString("Lat"));
            double lng = Double.parseDouble(object.getString("Long"));
            list.add(new LatLng(lat, lng));
        }
        return list;
    }

    private class DataSet {
        private ArrayList<LatLng> mDataset;
        public DataSet(ArrayList<LatLng> dataSet) {
            this.mDataset = dataSet;
        }
        public ArrayList<LatLng> getData() {
            return mDataset;
        }
    }
    public void showBottomSheetDialogFragment() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

}
