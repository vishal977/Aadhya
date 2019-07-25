package com.heisenberg.beherchange;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    CardView startTrip,unsafe,opinion,trust;
    //private LatLng destn = new LatLng(12.919530, 80.236030);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setFontPacifico();
        startTrip = findViewById(R.id.card_view_1);
        startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getBaseContext(),ScanQR.class);
                startActivity(i);
                /*Uri navigationIntentUri = Uri.parse("google.navigation:q=" + destn.latitude +"," + destn.longitude+"&mode=d&avoid=t");//creating intent with latlng
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);*/

            }
        });
        unsafe = findViewById(R.id.card_view_2);
        unsafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getBaseContext(),AnalyseLocations.class);
                startActivity(i);
            }
        });
        opinion = findViewById(R.id.card_view_3);
        opinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getBaseContext(),Opinion.class);
                startActivity(i);
            }
        });
        trust = findViewById(R.id.card_view_4);
        trust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getBaseContext(),TrustedContact.class);
                startActivity(i);
            }
        });
    }
    public void setFontPacifico()
    {
        TextView tx = findViewById(R.id.nameBar);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Pacifico.ttf");
        tx.setTypeface(custom_font);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
