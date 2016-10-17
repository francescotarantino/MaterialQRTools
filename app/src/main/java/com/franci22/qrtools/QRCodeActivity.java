package com.franci22.qrtools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRCodeActivity extends AppCompatActivity {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private Bitmap bitmap;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prfs = getSharedPreferences("settings", Context.MODE_PRIVATE);
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
        final Context ctx = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        FrameLayout statuskk = (FrameLayout) findViewById(R.id.notifybarkk2);
        FrameLayout statuskkred = (FrameLayout) findViewById(R.id.notifybarkkred2);
        FrameLayout statuskkgreen = (FrameLayout) findViewById(R.id.notifybarkkgreen2);
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
        Bundle extras = getIntent().getExtras();
        ImageView imgQR = (ImageView) findViewById(R.id.qrview);
        TextView textqr = (TextView) findViewById(R.id.textqr);
        File destinationFolder = new File(Environment.getExternalStorageDirectory()+"/QRTools");
        if (!destinationFolder.exists()) {
            boolean success = destinationFolder.mkdir();
            if (!success){
                Toast.makeText(QRCodeActivity.this, R.string.errormkdir, Toast.LENGTH_SHORT).show();
            }
        }
        if (extras != null) {
            message = extras.getString("qrtext");
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                final int qrDimension = size.x;
                try {
                    final BitMatrix bitMatrix = qrCodeWriter.encode(message, BarcodeFormat.QR_CODE, qrDimension, qrDimension);
                    final int matrixHeight = bitMatrix.getHeight();
                    final int matrixWidth = bitMatrix.getWidth();
                    int[] pixels = new int[matrixWidth * matrixHeight];
                    for (int y = 0; y < matrixHeight; y++) {
                        final int offset = y * matrixWidth;
                        for (int x = 0; x < matrixWidth; x++) {
                            pixels[offset + x] = bitMatrix.get(x, y) ? BLACK : WHITE;
                        }
                    }
                    bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
                    bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);
                    imgQR.setImageBitmap(bitmap);
                    textqr.setText(message);
                    textqr.setMovementMethod(ScrollingMovementMethod.getInstance());
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            if (Patterns.EMAIL_ADDRESS.matcher(message).matches()) {
                fab1.setImageResource(R.drawable.ic_more);
                fab1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MaterialDialog.Builder(ctx)
                                .title(R.string.chose)
                                .items(R.array.items_mail_qrcreate)
                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                        if (charSequence == getString(R.string.saveaspng)) {
                                            try {
                                                writeFrameBMP(bitmap);
                                            } catch (IOException ignored) {
                                            }
                                        } else if (charSequence == getString(R.string.sendanemail)) {
                                            Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                                            Uri data = Uri.parse("mailto:?body=\n--\n" + getString(R.string.sendedbyapp) + "&to=" + message);
                                            emailIntent.setData(data);
                                            startActivity(emailIntent);
                                        } else if (charSequence == getString(R.string.share)) {
                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            intent.putExtra(Intent.EXTRA_TEXT, message);
                                            startActivity(Intent.createChooser(intent, getString(R.string.share_intent)));
                                        }
                                        return false;
                                    }
                                })
                                .positiveText(R.string.conferma)
                                .show();
                    }
                });
            } else if (Patterns.WEB_URL.matcher(message).matches()) {
                fab1.setImageResource(R.drawable.ic_more);
                fab1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MaterialDialog.Builder(ctx)
                                .title(R.string.chose)
                                .items(R.array.items_link_qrcreate)
                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                        if (charSequence == getString(R.string.saveaspng)) {
                                            try {
                                                writeFrameBMP(bitmap);
                                            } catch (IOException ignored) {
                                            }
                                        } else if (charSequence == getString(R.string.openlink)) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(message)));
                                        } else if (charSequence == getString(R.string.share)) {
                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            intent.putExtra(Intent.EXTRA_TEXT, message);
                                            startActivity(Intent.createChooser(intent, getString(R.string.share_intent)));
                                        }
                                        return false;
                                    }
                                })
                                .positiveText(R.string.conferma)
                                .show();
                    }
                });
            } else {
                fab1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            writeFrameBMP(bitmap);
                        } catch (IOException ignored) {}
                    }
                });
            }
        }
    }

    private void writeFrameBMP(Bitmap ret) throws IOException {
        File sd = Environment.getExternalStorageDirectory();
        FileOutputStream fileOutputStream;
        int quality = 100;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Toast.makeText(QRCodeActivity.this, getString(R.string.imagecreated)+sd.toString()+"/QRTools", Toast.LENGTH_SHORT).show();
        int lenght = message.length();
        if(lenght > 9){
            char [] caratteri = new char[9];
            for (int i=0; i<9; i++) {
                caratteri[i] = message.charAt(i);
            }
            message = new String(caratteri);
        }
        try {
            fileOutputStream = new FileOutputStream(sd.toString() + "/QRTools/" + message +".png");
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            ret.compress(Bitmap.CompressFormat.PNG, quality, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
