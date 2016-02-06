package com.github.gfranks.expandablerecyclerview.sample;

import android.view.View;
import android.widget.TextView;

import com.github.gfranks.expandablerecyclerview.GFExpandableRecyclerViewAdapter;

/**
 * @author Garrett Franks
 * @version 1.0
 * @since 2/5/16
 */
public class VerticalChildViewHolder extends GFExpandableRecyclerViewAdapter.ViewHolder {

    public TextView mText;

    public VerticalChildViewHolder(View v) {
        super(v);

        mText = (TextView) v;
    }
}
