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
public class HorizontalExpandableRecyclerViewAdapter extends GFExpandableRecyclerViewAdapter<HorizontalGroupItem, HorizontalChildItem, HorizontalGroupViewHolder, HorizontalChildViewHolder> {

    private List<HorizontalGroupItem> mGroupItems;

    public HorizontalExpandableRecyclerViewAdapter() {
        generateGroupItems();
    }

    @Override
    public List<HorizontalGroupItem> getGroupItems() {
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
    public HorizontalGroupItem getGroupItem(int groupPosition) {
        return mGroupItems.get(groupPosition);
    }

    @Override
    public HorizontalChildItem getChildItem(int groupPosition, int childPosition) {
        return mGroupItems.get(groupPosition).getChildItems().get(childPosition);
    }

    @Override
    public boolean isGroupInitiallyExpanded(int groupPosition) {
        return groupPosition % 2 == 0;
    }

    @Override
    public HorizontalGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        return new HorizontalGroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_horizontal_group_item, parent, false));
    }

    @Override
    public HorizontalChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return new HorizontalChildViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_horizontal_child_item, parent, false));
    }

    @Override
    public void onBindGroupViewHolder(HorizontalGroupViewHolder holder, int groupPosition, HorizontalGroupItem groupItem, boolean isExpanded) {
        holder.mTitle.setText(groupItem.getTitle());
    }

    @Override
    public void onBindChildViewHolder(HorizontalChildViewHolder holder, int groupPosition, int childPosition, HorizontalChildItem verticalChildItem) {
        holder.mText.setText(verticalChildItem.getText());
    }

    void generateGroupItems() {
        mGroupItems = new ArrayList<>();
        for (int i=0; i<10; i++) {
            mGroupItems.add(new HorizontalGroupItem("Group " + (i+1)));
        }
    }
}
