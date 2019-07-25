package com.heisenberg.beherchange;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static com.heisenberg.beherchange.UnsafeLocations.ALT_HEATMAP_GRADIENT;
import static com.heisenberg.beherchange.UnsafeLocations.ALT_HEATMAP_OPACITY;


public class StartTrip extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        View.OnClickListener{

    private GoogleMap mMap;
    LinearLayout lin2;
    LatLng destn;
    Marker marker1,marker2;
    private double s_longitude;
    private double s_latitude;
    private GoogleApiClient googleApiClient;
    private static final String TAG = "Track Ride";

    String place1;

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private HashMap<String, DataSet> mLists = new HashMap<String, DataSet>();

    WebView webview,webview2;

    public static final int PATTERN_DASH_LENGTH_PX = 15;
    public static final int PATTERN_GAP_LENGTH_PX = 20;
    public static final PatternItem DOT = new Dot();
    public static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    public static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    public static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    private class MyWebViewClient2 extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
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

    private void showPolyLine() {
        List<LatLng> decodedPathsafe = PolyUtil.decode("gihaGnovpLW|@o@_@c@WgAs@g@c@u@{@[e@m@aAk@gAS[UGKEQCeBL]?qA[yBi@]Ic@SeD{BmFmDeC}@_Bi@QCqCeA{Bw@eE{AaCkAIMi@]q@[OMKU_@{@mAaDqB_Ge@qAm@mAaA_B]q@sC_GmBeDm@mAoBmF_CuGISILo@fAsCfF{BsCkCeDm@bAkAvB");

        mMap.addPolyline(new PolylineOptions().color(getResources().getColor(R.color.colorSafe)).pattern(PATTERN_POLYGON_ALPHA).addAll(decodedPathsafe).width(10));


        moveMarkerTwo(marker1,marker2,destn);

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.9244674, 80.2330078), 12));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_start_trip);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_track);
        mapFragment.getMapAsync(this);
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        webview = findViewById(R.id.webView);
        webview2 = findViewById(R.id.webView2);
        webview.setWebViewClient(new MyWebViewClient2());
        webview2.setWebViewClient(new MyWebViewClient());
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //mMap.clear();
                marker2 = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
                destn = place.getLatLng();
                place1=place.getName().toString();
            }

            @Override
            public void onError(Status status) {

            }
        });
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        lin2 = findViewById(R.id.lin_2);
        lin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(destn==null)
                {
                    Toast.makeText(getBaseContext(),"Please select a location...",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    lin2.setVisibility(View.INVISIBLE);
                    if(place1.equalsIgnoreCase("South Boston"))
                    {
                        showPolyLine();
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(),"Unable to find navigation. Please Try after sometime.",Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getBaseContext(), "Ignition Powdered", Toast.LENGTH_SHORT).show();
                    FancyToast.makeText(getBaseContext(),"You will be navigated through the safest route!!",FancyToast.LENGTH_LONG, FancyToast.SUCCESS,false).show();
                    openURLStart();
                    moveMarkerTwo(marker1,marker2,destn);
                }
            }
        });
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

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
    private void getCurrentLocation() {
        mMap.clear();
        s_longitude = -71.063155;
        s_latitude = 42.318385;

        //moving the map to location
        moveMap();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            /*s_longitude = location.getLongitude();
            s_latitude = location.getLatitude();

            //moving the map to location
            moveMap();*/
        }
    }

    private void moveMap() {
        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */
        LatLng latLng = new LatLng(s_latitude, s_longitude);
        marker1 = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                .title("You are at Dorchester - Accurate to 200m"));
        //to set marker info up on create
        marker1.showInfoWindow();
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
        moveToCurrentLocationFirst(latLng);
    }
    private void moveToCurrentLocationFirst(LatLng herLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(herLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    private void moveMarkerTwo(Marker m1,Marker m2,LatLng bs)
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //the include method will calculate the min and max bound.
        builder.include(m1.getPosition());
        builder.include(m2.getPosition());

        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.35); // offset from edges of the map 30% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(bs));
        mMap.animateCamera(cu);
    }

    @Override
    public void onClick(View view) {
        Log.v(TAG,"view click event");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(StartTrip.this, "onMarkerDragStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Toast.makeText(StartTrip.this, "onMarkerDrag", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // getting the Co-ordinates
        s_latitude = marker.getPosition().latitude;
        s_longitude = marker.getPosition().longitude;

        //move to current position
        moveMap();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(StartTrip.this, "onMarkerClick", Toast.LENGTH_SHORT).show();
        return true;
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_UP) {
                    if (event.getEventTime() - event.getDownTime() > ViewConfiguration.getLongPressTimeout()) {
                        //TODO long click action
                        Toast.makeText(getBaseContext(), "Distress/Emergency Situation Sensed", Toast.LENGTH_SHORT).show();
                        openURL();
                    } else {
                        //TODO click action
                    }
                }
                return true;
            /*case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_UP) {
                    if (event.getEventTime() - event.getDownTime() > ViewConfiguration.getLongPressTimeout()) {
                        //TODO long click action
                        Toast.makeText(getBaseContext(), "Long Press Vol down", Toast.LENGTH_SHORT).show();
                    } else {
                        //TODO click action
                    }
                }
                return true;*/
            default:
                return super.dispatchKeyEvent(event);
        }
    }
    private void openURL()
    {
        webview.loadUrl("https://be-her-change.herokuapp.com/ipdata?data=1");
        webview.requestFocus();
    }
    private void openURLStart()
    {
        webview2.loadUrl("https://be-her-change.herokuapp.com/ipdata?data=2");
        webview2.requestFocus();
    }
}
