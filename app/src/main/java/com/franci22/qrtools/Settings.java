package com.franci22.qrtools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

public class Settings extends AppCompatActivity {
    final Context ctx = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences prfs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String statusbarkitkatString = prfs.getString("statusbarkitkat", "true");
        String themechoser = prfs.getString("themeselect", "blue");
        switch (themechoser) {
            case "blue":
                setTheme(R.style.AppTheme);
                break;
            case "red":
                setTheme(R.style.AppTheme_Red);
                break;
            case "green":
                setTheme(R.style.AppTheme_Green);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        FrameLayout statuskk = (FrameLayout) findViewById(R.id.notifybarkk3);
        FrameLayout statuskkred = (FrameLayout) findViewById(R.id.notifybarkkred3);
        FrameLayout statuskkgreen = (FrameLayout) findViewById(R.id.notifybarkkgreen3);
        if (themechoser.equals("blue") && Build.VERSION.SDK_INT == 19 && statusbarkitkatString.equals("true")) {
            statuskk.setVisibility(View.VISIBLE);
        } else if (themechoser.equals("red") && Build.VERSION.SDK_INT == 19 && statusbarkitkatString.equals("true")){
            statuskkred.setVisibility(View.VISIBLE);
        } else if (themechoser.equals("green") && Build.VERSION.SDK_INT == 19 && statusbarkitkatString.equals("true")){
            statuskkgreen.setVisibility(View.VISIBLE);
        }
        if (Build.VERSION.SDK_INT == 19 && statusbarkitkatString.equals("false")) {
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativesetting);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 0);
            relativeLayout.setLayoutParams(params);
        }
        SwitchCompat saveonlist = (SwitchCompat) findViewById(R.id.saveonlist);
        SwitchCompat qst_close = (SwitchCompat) findViewById(R.id.qst_close);
        SwitchCompat scan_group = (SwitchCompat) findViewById(R.id.scan_group);
        AppCompatButton delete_list_scan = (AppCompatButton) findViewById(R.id.delete_list_scan);
        AppCompatButton app_theme_select = (AppCompatButton) findViewById(R.id.select_theme_app);
        final SharedPreferences.Editor editor = prfs.edit();
        if(Build.VERSION.SDK_INT == 19) {
            SwitchCompat statusbarkitkat = (SwitchCompat) findViewById(R.id.statusbarkitkat);
            if (statusbarkitkatString.equals("true")){
                statusbarkitkat.setChecked(true);
            } else {
                statusbarkitkat.setChecked(false);
            }
            statusbarkitkat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        editor.putString("statusbarkitkat", "true");
                        editor.apply();
                        Toast.makeText(Settings.this, getString(R.string.modifyonrestart), Toast.LENGTH_SHORT).show();
                    } else {
                        editor.putString("statusbarkitkat", "false");
                        editor.apply();
                        Toast.makeText(Settings.this, getString(R.string.modifyonrestart), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        String saveonlistString = prfs.getString("saveonlist", "false");
        if (saveonlistString.equals("true")){
            saveonlist.setChecked(true);
        } else {
            saveonlist.setChecked(false);
        }
        saveonlist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putString("saveonlist", "true");
                    editor.apply();
                } else {
                    editor.putString("saveonlist", "false");
                    editor.apply();
                }
            }
        });
        String qst_closeString = prfs.getString("qst_close", "true");
        if (qst_closeString.equals("true")){
            qst_close.setChecked(true);
        } else {
            qst_close.setChecked(false);
        }
        qst_close.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("qst_close", "true");
                    editor.apply();
                } else {
                    editor.putString("qst_close", "false");
                    editor.apply();
                }
            }
        });
        String scan_groupString = prfs.getString("scan_group", "false");
        if (scan_groupString.equals("true")){
            scan_group.setChecked(true);
        } else {
            scan_group.setChecked(false);
        }
        scan_group.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString("scan_group", "true");
                    editor.apply();
                } else {
                    editor.putString("scan_group", "false");
                    editor.apply();
                }
            }
        });
        delete_list_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DBAdapter(getApplicationContext()).deleteTable();
                Toast.makeText(getApplicationContext(), getString(R.string.listdeleted), Toast.LENGTH_SHORT).show();
            }
        });
        app_theme_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(ctx)
                        .title(R.string.app_theme_select)
                        .items(R.array.app_theme)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (text.equals(getString(R.string.red))){
                                    editor.putString("themeselect", "red");
                                    editor.apply();
                                    onBackPressed();
                                } else if (text.equals(getString(R.string.green))){
                                    editor.putString("themeselect", "green");
                                    editor.apply();
                                    onBackPressed();
                                } else if (text.equals(getString(R.string.blue))){
                                    editor.putString("themeselect", "blue");
                                    editor.apply();
                                    onBackPressed();
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.conferma)
                        .show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}