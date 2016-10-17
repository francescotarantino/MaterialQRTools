package com.franci22.qrtools;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateFragment extends Fragment {

    private TextView txtMessage;
    private View rootView;

    public CreateFragment() {
    }

    public static CreateFragment newInstance(String saveonlist) {
        CreateFragment fragment = new CreateFragment();
        Bundle args = new Bundle();
        args.putString("saveonlist", saveonlist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Button btnCrea = (Button) rootView.findViewById(R.id.crea);
        txtMessage = (TextView) rootView.findViewById(R.id.txtMessage);
        btnCrea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkString();
            }
        });
        return rootView;
    }

    private void checkString() {
        String saveonlist = getArguments().getString("saveonlist");
        final String message = txtMessage.getText().toString();
        if (message.length() > 0) {
            Intent i = new Intent(getContext(), QRCodeActivity.class);
            i.putExtra("qrtext", message);
            startActivity(i);
            if (saveonlist != null && saveonlist.equals("true")){
                Long tsLong = System.currentTimeMillis() / 1000;
                long dv = tsLong *1000;
                Date df = new java.util.Date(dv);
                String vv = new SimpleDateFormat("dd/MM, HH:mm").format(df);
                new DBAdapter(getContext()).insertDetails(message, vv, getString(R.string.createdbyuser));
            }
        } else {
            Snackbar.make(rootView, getString(R.string.error_create), Snackbar.LENGTH_SHORT).show();
            Utilities.closeKeyboard(getContext(), txtMessage.getWindowToken());
        }
    }
}