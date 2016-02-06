package com.github.gfranks.expandablerecyclerview.sample;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.gfranks.expandablerecyclerview.GFExpandableRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Garrett Franks
 * @version 1.0
 * @since 2/5/16
 */
public class VerticalExpandableRecyclerViewAdapter extends GFExpandableRecyclerViewAdapter<VerticalGroupItem, VerticalChildItem, VerticalGroupViewHolder, VerticalChildViewHolder> {

    private List<VerticalGroupItem> mGroupItems;

    public VerticalExpandableRecyclerViewAdapter() {
        generateGroupItems();
    }

    @Override
    public List<VerticalGroupItem> getGroupItems() {
        return mGroupItems;
    }

    @Override
    public int getGroupCount() {
        return mGroupItems.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mGroupItems.get(groupPosition).getChildItems().size();
    }

    @Override
    public VerticalGroupItem getGroupItem(int groupPosition) {
        return mGroupItems.get(groupPosition);
    }

    @Override
    public VerticalChildItem getChildItem(int groupPosition, int childPosition) {
        return mGroupItems.get(groupPosition).getChildItems().get(childPosition);
    }

    @Override
    public boolean isGroupInitiallyExpanded(int groupPosition) {
        return groupPosition % 2 == 0;
    }

    @Override
    public VerticalGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        return new VerticalGroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_vertical_group_item, parent, false));
    }

    @Override
    public VerticalChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return new VerticalChildViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_vertical_child_item, parent, false));
    }

    @Override
    public void onBindGroupViewHolder(VerticalGroupViewHolder holder, int groupPosition, VerticalGroupItem groupItem, boolean isExpanded) {
        holder.mTitle.setText(groupItem.getTitle());
    }

    @Override
    public void onBindChildViewHolder(VerticalChildViewHolder holder, int groupPosition, int childPosition, VerticalChildItem verticalChildItem) {
        holder.mText.setText(verticalChildItem.getText());
    }

    void generateGroupItems() {
        mGroupItems = new ArrayList<>();
        for (int i=0; i<10; i++) {
            mGroupItems.add(new VerticalGroupItem("Group " + (i+1)));
        }
    }
}
