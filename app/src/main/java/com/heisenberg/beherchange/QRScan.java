package com.heisenberg.beherchange;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class QRScan extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener{
    BarcodeReader barcodeReader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_scanner);
    }
    @Override
    public void onScanned(final Barcode barcode) {

        // playing barcode reader beep sound
        barcodeReader.playBeep();
        this.runOnUiThread(new Runnable() {
            public void run() {
                if(barcode.displayValue.equalsIgnoreCase("dave"))
                {
                    Intent in = new Intent(getBaseContext(), DriverDetails.class);
                    startActivity(in);
                    finish();
                }
            }
        });
    }

    @Override
    public void onScannedMultiple(List<Barcode> list) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onCameraPermissionDenied() {
        finish();
    }

    @Override
    public void onScanError(String s) {
        Toast.makeText(getApplicationContext(), "Error occurred while scanning " + s, Toast.LENGTH_SHORT).show();
    }
}
