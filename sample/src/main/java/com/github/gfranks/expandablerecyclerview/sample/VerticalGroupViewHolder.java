package com.github.gfranks.expandablerecyclerview.sample;

import android.view.View;
import android.widget.TextView;

import com.github.gfranks.expandablerecyclerview.GFExpandableRecyclerViewAdapter;

/**
 * @author Garrett Franks
 * @version 1.0
 * @since 2/5/16
 */
public class VerticalGroupViewHolder extends GFExpandableRecyclerViewAdapter.ViewHolder {

    public TextView mTitle;

    public VerticalGroupViewHolder(View v) {
        super(v);
        mTitle = (TextView) v;
    }
}
