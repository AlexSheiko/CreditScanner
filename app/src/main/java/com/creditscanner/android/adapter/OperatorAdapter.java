package com.creditscanner.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.R;
import com.creditscanner.android.model.Operator;

import java.util.List;

public class OperatorAdapter extends ArrayAdapter<Operator> {

    public OperatorAdapter(Context context, List<Operator> objects) {
        super(context, R.layout.operator_list_item, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setCompoundDrawablesWithIntrinsicBounds(getItem(position).getImageRes(), 0, 0, 0);
        return view;
    }
}
