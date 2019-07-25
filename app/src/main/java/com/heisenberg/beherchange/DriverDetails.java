package com.heisenberg.beherchange;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DriverDetails extends AppCompatActivity {
    Button startTrip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);
        startTrip = findViewById(R.id.startTrip);
        startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DriverDetails.this);
                builder.setMessage("Do you really wish to start the trip?")
                        .setTitle("Confirm Trip")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getBaseContext(),StartTrip.class);
                                startActivity(i);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getBaseContext(),MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });

                // Create the AlertDialog object and return it
                builder.create();
                AlertDialog dialog = builder.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
    }
}
