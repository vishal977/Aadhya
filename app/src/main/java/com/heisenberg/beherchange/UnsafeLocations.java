package com.heisenberg.beherchange;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class UnsafeLocations extends AppCompatActivity implements OnMapReadyCallback {

//    private static final int ALT_HEATMAP_RADIUS = 10;

    static final double ALT_HEATMAP_OPACITY = 1.5;

    static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.argb(0, 255, 0, 0),// transparent
            Color.argb(255 / 3 * 2, 255, 0, 0),
            Color.rgb(255, 0, 192),
            Color.rgb(127, 0, 0),
            Color.rgb(0, 0, 255)
    };

    public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    };

    public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
            ALT_HEATMAP_GRADIENT_START_POINTS);

    private boolean mDefaultGradient = true;
    private boolean mDefaultRadius = true;
    private boolean mDefaultOpacity = true;

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private GoogleMap mMap;

    private HashMap<String, DataSet> mLists = new HashMap<String, DataSet>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsafe_locations);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_unsafe_loc);
        mapFragment.getMapAsync(this);
    }

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

        /*try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }*/
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.changeGrad) {
            if (mDefaultGradient) {
                mProvider.setGradient(ALT_HEATMAP_GRADIENT);
            } else {
                mProvider.setGradient(HeatmapTileProvider.DEFAULT_GRADIENT);
            }
            mOverlay.clearTileCache();
            mDefaultGradient = !mDefaultGradient;
        }
        else if(id == R.id.changeOpac)
        {
            if (mDefaultOpacity) {
                mProvider.setOpacity(ALT_HEATMAP_OPACITY);
            } else {
                mProvider.setOpacity(HeatmapTileProvider.DEFAULT_OPACITY);
            }
            mOverlay.clearTileCache();
            mDefaultOpacity = !mDefaultOpacity;
        }
        else if(id == R.id.changeRad)
        {
            if (mDefaultRadius) {
                mProvider.setRadius(ALT_HEATMAP_RADIUS);
            } else {
                mProvider.setRadius(HeatmapTileProvider.DEFAULT_RADIUS);
            }
            mOverlay.clearTileCache();
            mDefaultRadius = !mDefaultRadius;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            String la = object.getString("Lat");
            String lo = object.getString("Long");
            if(la==null || lo==null)
            {
                break;
            }
            else
            {
                double lat = Double.parseDouble(la);
                double lng = Double.parseDouble(lo);
                list.add(new LatLng(lat, lng));
            }
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
}
