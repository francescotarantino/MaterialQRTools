package com.franci22.qrtools;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

public class ScanFragment extends Fragment {

    LinearLayoutManager layoutManager;
    RecyclerView list;
    View rootView;

    public ScanFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scan, container, false);
        layoutManager = new LinearLayoutManager(getContext());
        list = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        ItemClickSupport.addTo(list).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                TextView txt = (TextView) recyclerView.getChildAt(position - firstVisiblePosition).findViewById(R.id.textqrv);
                final String keyword = txt.getText().toString();
                TextView idqrv = (TextView) recyclerView.getChildAt(position - firstVisiblePosition).findViewById(R.id.idqrv);
                final String idqr = idqrv.getText().toString();
                new MaterialDialog.Builder(getContext())
                        .title(R.string.chose)
                        .items(R.array.items_delete)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                String txt = String.valueOf(text);
                                if (txt.equals(getString(R.string.delete))) {
                                    int idqrint = Integer.parseInt(idqr);
                                    new DBAdapter(getContext()).deletOneRecord(idqrint);
                                    updatelw();
                                } else if (txt.equals(getString(R.string.share))) {
                                    final Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("text/plain");
                                    intent.putExtra(Intent.EXTRA_TEXT, keyword);
                                    startActivity(Intent.createChooser(intent, getString(R.string.share_intent)));
                                } else if (txt.equals(getString(R.string.ricreaqr))) {
                                    Intent i = new Intent(getContext(), QRCodeActivity.class);
                                    i.putExtra("qrtext", keyword);
                                    startActivity(i);
                                } else if (txt.equals(getString(R.string.copy))){
                                    Utilities.copyTextToClipboard(keyword, getContext());
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.conferma)
                        .show();
                return false;
            }
        });
        ItemClickSupport.addTo(list).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                TextView txt = (TextView) recyclerView.getChildAt(position - firstVisiblePosition).findViewById(R.id.textqrv);
                final String keyword = txt.getText().toString();
                if (Patterns.EMAIL_ADDRESS.matcher(keyword).matches()) {
                    Snackbar.make(rootView, getString(R.string.sendemail), Snackbar.LENGTH_SHORT).setAction(getText(R.string.yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                            Uri data = Uri.parse("mailto:?body=\n--\n" + getString(R.string.sendedbyapp) + "&to=" + keyword);
                            emailIntent.setData(data);
                            startActivity(emailIntent);
                        }
                    }).show();
                } else if (Patterns.WEB_URL.matcher(keyword).matches()) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(keyword)));
                } else {
                    Snackbar.make(rootView, getString(R.string.ricreate), Snackbar.LENGTH_LONG).setAction(getText(R.string.yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getContext(), QRCodeActivity.class);
                            i.putExtra("qrtext", keyword);
                            startActivity(i);
                        }
                    }).show();
                }
            }
        });
        updatelw();
        return rootView;
        }

    @Override
    public void onResume(){
        updatelw();
        super.onResume();
    }

    private void updatelw(){
        DBAdapter adapter_ob = new DBAdapter(getContext());
        String[] from = {DBHelper.text, DBHelper.date, DBHelper.KEY_ID, DBHelper.format };
        int[] to = { R.id.textqrv, R.id.dateqrv, R.id.idqrv, R.id.typeqrv };
        Cursor cursor = adapter_ob.queryName();
        list.setAdapter(new SimpleCursorRecyclerAdapter(R.layout.item_todo, cursor, from, to));
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(layoutManager);
        if (list.getAdapter().getItemCount() == 0){
            TextView listempty = (TextView) rootView.findViewById(R.id.lstEmpty);
            listempty.setVisibility(View.VISIBLE);
        }
    }

    public abstract class CursorRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected boolean mDataValid;
    protected Cursor mCursor;
    protected int mRowIDColumn;

    public CursorRecyclerAdapter(Cursor c) {
        init(c);
    }

    void init(Cursor c) {
        boolean cursorPresent = c != null;
        mCursor = c;
        mDataValid = cursorPresent;
        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
        setHasStableIds(true);
    }

    @Override
    public final void onBindViewHolder (VH holder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        onBindViewHolder(holder, mCursor);
    }

    public abstract void onBindViewHolder(VH holder, Cursor cursor);

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount () {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId (int position) {
        if(hasStableIds() && mDataValid && mCursor != null){
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return RecyclerView.NO_ID;
            }
        } else {
            return RecyclerView.NO_ID;
        }
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }
    public CharSequence convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }
}

    public class SimpleCursorRecyclerAdapter extends CursorRecyclerAdapter<SimpleViewHolder> {

        private int mLayout;
        private int[] mFrom;
        private int[] mTo;
        private String[] mOriginalFrom;

        public SimpleCursorRecyclerAdapter (int layout, Cursor c, String[] from, int[] to) {
            super(c);
            mLayout = layout;
            mTo = to;
            mOriginalFrom = from;
            findColumns(c, from);
        }

        @Override
        public SimpleViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(mLayout, parent, false);
            return new SimpleViewHolder(v, mTo);
        }

        @Override
        public void onBindViewHolder (SimpleViewHolder holder, Cursor cursor) {
            final int count = mTo.length;
            final int[] from = mFrom;

            for (int i = 0; i < count; i++) {
                holder.views[i].setText(cursor.getString(from[i]));
            }
        }

        private void findColumns(Cursor c, String[] from) {
            if (c != null) {
                int i;
                int count = from.length;
                if (mFrom == null || mFrom.length != count) {
                    mFrom = new int[count];
                }
                for (i = 0; i < count; i++) {
                    mFrom[i] = c.getColumnIndexOrThrow(from[i]);
                }
            } else {
                mFrom = null;
            }
        }

        @Override
        public Cursor swapCursor(Cursor c) {
            findColumns(c, mOriginalFrom);
            return super.swapCursor(c);
        }
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        public TextView[] views;

        public SimpleViewHolder (View itemView, int[] to)
        {
            super(itemView);
            views = new TextView[to.length];
            for(int i = 0 ; i < to.length ; i++) {
                views[i] = (TextView) itemView.findViewById(to[i]);
            }
        }
    }

}