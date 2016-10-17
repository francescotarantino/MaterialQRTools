package com.franci22.qrtools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends Activity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        if (rawResult.getText() != null) {
            Long tsLong = System.currentTimeMillis() / 1000;
            long dv = tsLong *1000;
            Date df = new java.util.Date(dv);
            String vv = new SimpleDateFormat("dd/MM, HH:mm").format(df);
            new DBAdapter(this).insertDetails(rawResult.getText(), vv, rawResult.getBarcodeFormat().toString());
            Toast.makeText(ScanActivity.this, rawResult.getText(), Toast.LENGTH_SHORT).show();
            final SharedPreferences prfs = getSharedPreferences("settings", Context.MODE_PRIVATE);
            String scangroup = prfs.getString("scan_group", "false");
            if (scangroup.equals("false")){
                Intent i = new Intent(this, QRCodeActivity.class);
                i.putExtra("qrtext", rawResult.getText());
                startActivity(i);
                finish();
            } else {
                onResume();
            }
        }
    }
}