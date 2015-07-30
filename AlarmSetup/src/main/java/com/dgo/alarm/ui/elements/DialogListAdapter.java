package com.dgo.alarm.ui.elements;

import android.content.Context;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dgo.alarm.R;

import java.util.ArrayList;

/**
 * Created by david.gonzalez on 10/07/2015.
 */
public class DialogListAdapter extends ArrayAdapter<Pair<Integer, String>>{

    public DialogListAdapter(Context context){
        super(context, R.layout.dialog_item, R.id.item_description, new ArrayList<Pair<Integer, String>>());
    }

    public void add(int iconId, String text){
        add(new Pair<Integer, String>(iconId, text));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v =  super.getView(position, convertView, parent);

        Pair<Integer, String> item = getItem(position);
        ImageView image = (ImageView) v.findViewById(R.id.item_picture);
        image.setImageResource(item.first);
        TextView tv = (TextView) v.findViewById(R.id.item_description);
        tv.setText(item.second);

        return v;
    }
}
