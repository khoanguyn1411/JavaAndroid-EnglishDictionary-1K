package com.duanthivien1k.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duanthivien1k.tudientienganh.R;

public class TuScanDuocAdapter extends ArrayAdapter<String> {
    Activity context;
    int resource;

    public TuScanDuocAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View customView = inflater.inflate(this.resource,null);

        TextView txtTuScanDuoc = customView.findViewById(R.id.txtTuScanDuoc);

        String tsd = getItem(position);
        txtTuScanDuoc.setText(tsd);

        return customView;
    }
}
