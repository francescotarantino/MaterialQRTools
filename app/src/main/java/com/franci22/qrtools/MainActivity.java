package com.franci22.qrtools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String saveonlist;
    private String qst_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prfs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String themechoser = prfs.getString("themeselect", "blue");
        if (Build.VERSION.SDK_INT >= 21){
            switch (themechoser) {
                case "blue":
                    setTheme(R.style.AppTheme_NoActionBar_Tras);
                    break;
                case "red":
                    setTheme(R.style.AppTheme_NoActionBar_Red_Tras);
                    break;
                case "green":
                    setTheme(R.style.AppTheme_NoActionBar_Green_Tras);
                    break;
            }
        } else {
            switch (themechoser) {
                case "blue":
                    setTheme(R.style.AppTheme_NoActionBar);
                    break;
                case "red":
                    setTheme(R.style.AppTheme_NoActionBar_Red);
                    break;
                case "green":
                    setTheme(R.style.AppTheme_NoActionBar_Green);
                    break;
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout statuskk = (FrameLayout) findViewById(R.id.notifybarkk);
        FrameLayout statuskkred = (FrameLayout) findViewById(R.id.notifybarkkred);
        FrameLayout statuskkgreen = (FrameLayout) findViewById(R.id.notifybarkkgreen);
        String statusbarkitkatString = prfs.getString("statusbarkitkat", "true");
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
        }
        String splash_status = prfs.getString("splash_status", "");
        if (splash_status.isEmpty()){
            new MaterialDialog.Builder(this)
                    .title(R.string.attention)
                    .content(R.string.tutorial_qst)
                    .negativeText(R.string.no)
                    .positiveText(R.string.open)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://qrtools.franci22.altervista.org")));
                        }
                    })
                    .show();
            SharedPreferences.Editor editor = prfs.edit();
            editor.putString("splash_status", "true");
            editor.apply();
        }
        saveonlist = prfs.getString("saveonlist", "false");
        qst_close = prfs.getString("qst_close", "true");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Scan();
            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, CreateFragment.newInstance(saveonlist))
                .commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void Scan(){
    Intent Scan = new Intent(this, ScanActivity.class);
    startActivity(Scan);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(qst_close.equals("true")){
                new MaterialDialog.Builder(this)
                        .content(R.string.closeapp_content)
                        .positiveText(R.string.yes)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                finish();
                            }
                        })
                        .negativeText(R.string.no)
                        .show();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            finish();
            return true;
        } else if (id == R.id.action_faq){
            Toast.makeText(MainActivity.this, "Le FAQ saranno disponibili a breve!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.open_web_off){
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://franci22.ml/qrtools")));
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_create) {
            SharedPreferences prfs = getSharedPreferences("settings", Context.MODE_PRIVATE);
            saveonlist = prfs.getString("saveonlist", "false");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, CreateFragment.newInstance(saveonlist))
                    .commit();
        } else if (id == R.id.nav_listqr) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ScanFragment())
                    .commit();
        } else if (id == R.id.nav_share) {
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link_text) +
                    "https://franci22.ml/qrtools");
            startActivity(Intent.createChooser(intent, getString(R.string.share_intent)));
        } else if (id == R.id.nav_help) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new HelpFragment())
                    .commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}