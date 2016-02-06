package com.github.gfranks.expandablerecyclerview;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.github.gfranks.expandablerecyclerview.animator.FadeInItemAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Garrett Franks
 * @version 1.0
 * @since 2/5/16
 */
public abstract class GFExpandableRecyclerViewAdapter<GI, CI, GVH extends GFExpandableRecyclerViewAdapter.ViewHolder, CVH extends GFExpandableRecyclerViewAdapter.ViewHolder> extends RecyclerView.Adapter<GFExpandableRecyclerViewAdapter.ViewHolder> {

    private static final String GROUP_ITEMS = "group_items";

    private RecyclerView mRecyclerView;
    private OnGroupExpandCollapseListener mOnGroupExpandCollapseListener;
    private OnGroupClickListener mOnGroupClickListener;
    private OnChildClickListener mOnChildClickListener;
    private RecyclerView.AdapterDataObserver mDataObserver;

    private ArrayList<GroupIndexWrapper> mGroupItems;
    private boolean mRestoringState;

    private enum Type {
        GROUP,
        CHILD
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.setItemAnimator(new FadeInItemAnimator());
        ensureGroupItemWrappers();
        registerAdapterDataObserver(getDataObserver());
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(getDataObserver());
        mRecyclerView = null;
    }

    @Override
    public final long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public final int getItemCount() {
        if (getGroupCount() != mGroupItems.size()) {
            ensureGroupItemWrappers();
        }
        int totalCount = getGroupCount();
        for (int i=0; i<getGroupCount(); i++) {
            if (getGroupItemWrapper(i).isExpanded()) {
                totalCount += getChildCount(i);
            }
        }
        return totalCount;
    }

    @Override
    public final int getItemViewType(int position) {
        for (int i=0; i<getGroupCount(); i++) {
            int pos = getCountLeadingUpToGroup(i);
            if (position == pos) {
                return Type.GROUP.ordinal();
            } else {
                if (getGroupItemWrapper(i).isExpanded()) {
                    for (int j = 0; j < getChildCount(i); j++) {
                        ++pos;
                        if (position == pos) {
                            return Type.CHILD.ordinal();
                        }
                    }
                }
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public final GFExpandableRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (Type.values()[viewType]) {
            case GROUP: {
                GVH holder = onCreateGroupViewHolder(parent, viewType);
                holder.setAdapter(this);
                return holder;
            }
            case CHILD: {
                CVH holder = onCreateChildViewHolder(parent, viewType);
                holder.setAdapter(this);
                return holder;
            }
        }
        return null;
    }

    @Override
    public final void onBindViewHolder(final GFExpandableRecyclerViewAdapter.ViewHolder holder, final int position) {
        final int groupPosition = getPosition(position, true);
        switch (Type.values()[getItemViewType(position)]) {
            case GROUP:
                onBindGroupViewHolder((GVH) holder, groupPosition, getGroupItem(groupPosition), getGroupItemWrapper(groupPosition).isExpanded());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isExpandable = true;
                        if (mOnGroupExpandCollapseListener != null) {
                            isExpandable = mOnGroupExpandCollapseListener.isGroupExpandable(groupPosition);
                        }
                        if (isExpandable) {
                            toggle(getGroupItemWrapper(groupPosition), groupPosition);
                        }
                        if (mOnGroupClickListener != null) {
                            mOnGroupClickListener.onGroupClick(GFExpandableRecyclerViewAdapter.this, holder.itemView, groupPosition);
                        }
                    }
                });
                break;
            case CHILD:
                int childPosition = getPosition(position, false);
                onBindChildViewHolder((CVH) holder, groupPosition, childPosition, getChildItem(groupPosition, childPosition));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnChildClickListener != null) {
                            mOnChildClickListener.onChildClick(GFExpandableRecyclerViewAdapter.this, holder.itemView, groupPosition, getPosition(position, false));
                        }
                    }
                });
                break;
        }
    }

    public void setOnGroupExpandCollapseListener(OnGroupExpandCollapseListener onGroupExpandCollapseListener) {
        mOnGroupExpandCollapseListener = onGroupExpandCollapseListener;
    }

    public void setOnGroupClickListener(OnGroupClickListener onGroupClickListener) {
        mOnGroupClickListener = onGroupClickListener;
    }

    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        mOnChildClickListener = onChildClickListener;
    }

    /**
     * Inserts a new group item into the RecyclerView
     *
     * @param groupItem The new group item to be added
     * @param expanded If the group should be expanded
     */
    public void insertGroupItem(GI groupItem, boolean expanded) {
        getGroupItems().add(groupItem);
        mGroupItems.add(new GroupIndexWrapper(expanded));
        notifyItemInserted(getCountLeadingUpToGroup(getGroupCount() - 1));
    }

    /**
     * Inserts a new group item into the RecyclerView
     *
     * @param groupItem The new group item to be added
     * @param position Position the group item should be places in relevence to the group items
     * @param expanded If the group should be expanded
     */
    public void insertGroupItem(GI groupItem, int position, boolean expanded) {
        getGroupItems().add(position, groupItem);
        mGroupItems.add(position, new GroupIndexWrapper(expanded));
        notifyItemInserted(getCountLeadingUpToGroup(position));
    }

    /**
     * Removes a group item from the RecyclerView
     *
     * @param groupItem The group item to be removed
     */
    public void removeGroupItem(GI groupItem) {
        int position = getGroupItems().indexOf(groupItem);
        if (position != -1) {
            getGroupItems().remove(groupItem);
            mGroupItems.remove(position);
            int itemCount = 1;
            if (getGroupItemWrapper(position).isExpanded()) {
                itemCount += getChildCount(position);
            }
            notifyItemRangeRemoved(getCountLeadingUpToGroup(position), itemCount);
        } else {
            notifyDataSetChanged();
        }
    }

    /**
     * Expand a group in the grouped list view
     *
     * @param position the group to be expanded
     */
    public void expandGroup(int position) {
        if (!isGroupExpanded(position)) {
            if (mOnGroupExpandCollapseListener != null && !mOnGroupExpandCollapseListener.isGroupExpandable(position)) {
                return;
            }
            getGroupItemWrapper(position).setExpanded(true);
            notifyItemChanged(getCountLeadingUpToGroup(position));
            notifyItemRangeInserted(getCountLeadingUpToGroup(position) + 1, getChildCount(position));
        }
    }

    /**
     * Collapse a group in the grouped list view
     *
     * @param position position of the group to collapse
     */
    public void collapseGroup(int position) {
        if (isGroupExpanded(position)) {
            getGroupItemWrapper(position).setExpanded(false);
            notifyItemChanged(getCountLeadingUpToGroup(position));
            notifyItemRangeRemoved(getCountLeadingUpToGroup(position) + 1, getChildCount(position));
        }
    }

    /**
     * Scrolls to the specified group.
     *
     * @param position The position of the group that should be scrolled to.
     */
    public void scrollToGroup(int position) {
        if (mRecyclerView != null) {
            mRecyclerView.getLayoutManager().scrollToPosition(getCountLeadingUpToGroup(position) + 1);
        }
    }

    /**
     * Determines if the group is expanded
     *
     * @param position The position of the group
     * @return If the group is expanded
     */
    public boolean isGroupExpanded(int position) {
        return getGroupItemWrapper(position).isExpanded();
    }

    /**
     * Handled saving the expanded state. Call from your activity or fragment
     *
     * @param outState Bundle to save the expansion state
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(GROUP_ITEMS, mGroupItems);
    }

    /**
     * Restored the expanded state. Call from your activity or fragment
     *
     * @param savedInstanceState Bundle to check the expansion state
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(GROUP_ITEMS)) {
            mRestoringState = true;
            mGroupItems = savedInstanceState.getParcelableArrayList(GROUP_ITEMS);
            notifyDataSetChanged();
            mRestoringState = false;
        }
    }

    private void toggle(GroupIndexWrapper groupIndexWrapper, int position) {
        if (groupIndexWrapper.isExpanded()) {
            collapseGroup(position);
            if(mOnGroupExpandCollapseListener != null) {
                mOnGroupExpandCollapseListener.onGroupCollapse(position);
            }
        } else {
            expandGroup(position);
            if(mOnGroupExpandCollapseListener != null) {
                mOnGroupExpandCollapseListener.onGroupExpand(position);
            }
            scrollToGroup(position);
        }
    }

    private void ensureGroupItemWrappers() {
        if (mGroupItems == null) {
            mGroupItems = new ArrayList<>();
        }

        mGroupItems.clear();
        for (int i=0; i<getGroupCount(); i++) {
            mGroupItems.add(new GroupIndexWrapper(isGroupInitiallyExpanded(i)));
        }
    }

    private int getPosition(int position, boolean getGroup) {
        for (int i=0; i<getGroupCount(); i++) {
            int pos = getCountLeadingUpToGroup(i);
            if (position == pos && getGroup) {
                return i;
            } else {
                if (getGroupItemWrapper(i).isExpanded()) {
                    for (int j = 0; j < getChildCount(i); j++) {
                        ++pos;
                        if (position == pos) {
                            if (getGroup) {
                                return i;
                            }
                            return j;
                        }
                    }
                }
            }
        }

        return -1;
    }

    private int getCountLeadingUpToGroup(int group) {
        int total = 0;
        for (int i=0; i<group; i++) {
            ++total;
            if (getGroupItemWrapper(i).isExpanded()) {
                total += getChildCount(i);
            }
        }
        return total;
    }

    private GroupIndexWrapper getGroupItemWrapper(int position) {
        return mGroupItems.get(position);
    }

    private RecyclerView.AdapterDataObserver getDataObserver() {
        if (mDataObserver == null) {
            mDataObserver = new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    if (!mRestoringState) {
                        ensureGroupItemWrappers();
                    }
                }
            };
        }

        return mDataObserver;
    }

    /**
     * Gets the group items.
     *
     * @return the List of groups, used to insert items when called
     * @see #insertGroupItem(Object, boolean)
     * @see #insertGroupItem(Object, int, boolean)
     */
    public abstract List<GI> getGroupItems();

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    public abstract int getGroupCount();

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children
     *            count should be returned
     * @return the children count in the specified group
     */
    public abstract int getChildCount(int groupPosition);

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */
    public abstract GI getGroupItem(int groupPosition);

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *            children in the group
     * @return the data of the child
     */
    public abstract CI getChildItem(int groupPosition, int childPosition);

    /**
     * Determines if the specified group position should be initially expanded
     *
     * @param groupPosition the position of the group
     * @return If the specified group should be initially expanded
     */
    public abstract boolean isGroupInitiallyExpanded(int groupPosition);

    /**
     * Called when RecyclerView needs a new {@link GVH} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindGroupViewHolder(ViewHolder, int, Object, boolean)}. Since it will be re-used to display
     * different group items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindGroupViewHolder(ViewHolder, int, Object, boolean)
     */
    public abstract GVH onCreateGroupViewHolder(ViewGroup parent, int viewType);

    /**
     * Called when RecyclerView needs a new {@link CVH} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given group. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindChildViewHolder(ViewHolder, int, int, Object)}. Since it will be re-used to display
     * different child items in the group data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindChildViewHolder(ViewHolder, int, int, Object)
     */
    public abstract CVH onCreateChildViewHolder(ViewGroup parent, int viewType);

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the group
     * item at the given position.
     * <p>
     * Note that unlike {@link android.widget.ListView}, RecyclerView will not call this method
     * again if the position of the group item changes in the data set unless the group item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>groupPosition</code>, <code>groupItem</code>, <code>isExpanded</code> parameters while
     * acquiring the related data item inside this method and should not keep a copy of it. If you need the
     * position of an item later on (e.g. in a click listener), use
     * {@link ViewHolder#getGroupPosition()} ()} which will have the
     * updated adapter position.
     *
     * @param holder The Group ViewHolder which should be updated to represent the contents of the
     *        group item at the given position in the data set.
     * @param groupPosition The position of the group item within the adapter's data set.
     * @param groupItem The group item associated with the current {@link ViewHolder}
     * @param isExpanded If the group item is expanded
     */
    public abstract void onBindGroupViewHolder(GVH holder, int groupPosition, GI groupItem, boolean isExpanded);

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the
     * group item at the given position.
     * <p>
     * Note that unlike {@link android.widget.ListView}, RecyclerView will not call this method
     * again if the position of the group item changes in the data set unless the group item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>groupPosition</code>, <code>childPosition</code>, <code>childItem</code> parameters while
     * acquiring the related data item inside this method and should not keep a copy of it. If you need the
     * position of an item later on (e.g. in a click listener), use
     * {@link ViewHolder#getChildPosition()} ()} which will have the
     * updated adapter position.
     *
     * @param holder The Group ViewHolder which should be updated to represent the contents of the
     *        group item at the given position in the data set.
     * @param groupPosition The position of the group item within the adapter's data set.
     * @param childPosition The position of the child item within the group item's data set
     * @param childItem The child item associated with the current {@link ViewHolder}
     */
    public abstract void onBindChildViewHolder(CVH holder, int groupPosition, int childPosition, CI childItem);

    /** Used for being notified when a group is expanded or collapsed */
    public interface OnGroupExpandCollapseListener {
        /**
         * Callback method to be invoked when determining if the group in this
         * RecyclerView.Adapter can be expanded
         *
         * @param groupPosition The group position that may be expanded
         *
         * @return If the specified group be expanded
         */
        boolean isGroupExpandable(int groupPosition);

        /**
         * Callback method to be invoked when a group in this RecyclerView.Adapter has
         * been expanded.
         *
         * @param groupPosition The group position that was expanded
         */
        void onGroupExpand(int groupPosition);

        /**
         * Callback method to be invoked when a group in this RecyclerView.Adapter has
         * been collapsed.
         *
         * @param groupPosition The group position that was collapsed
         */
        void onGroupCollapse(int groupPosition);
    }

    /** Used for being notified when a group is clicked */
    public interface OnGroupClickListener {
        /**
         * Callback method to be invoked when a group item is clicked
         *
         * @param adapter The GFExpandableRecyclerViewAdapter
         * @param v The group view that was clicked
         * @param groupPosition The group position
         */
        void onGroupClick(GFExpandableRecyclerViewAdapter adapter, View v, int groupPosition);
    }

    /** Used for being notified when a child is clicked */
    public interface OnChildClickListener {
        /**
         * Callback method to be invoked when a child item is clicked
         *
         * @param adapter The GFExpandableRecyclerViewAdapter
         * @param v The child view that was clicked
         * @param groupPosition The group position the child resides in
         * @param childPosition The child position within the group
         */
        void onChildClick(GFExpandableRecyclerViewAdapter adapter, View v, int groupPosition, int childPosition);
    }

    /**
     * GFExpandableRecyclerViewAdapter.ViewHolder that handles getAdapterPosition
     */
    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        private GFExpandableRecyclerViewAdapter mAdapter;

        public ViewHolder(View v) {
            super(v);
        }

        public final GFExpandableRecyclerViewAdapter getAdapter() {
            return mAdapter;
        }

        public final void setAdapter(GFExpandableRecyclerViewAdapter adapter) {
            mAdapter = adapter;
        }

        /**
         *
         * @return the position of the group.
         */
        public final int getGroupPosition() {
            return mAdapter.getPosition(getAdapterPosition(), true);
        }

        public final int getChildPosition() {
            return mAdapter.getPosition(getAdapterPosition(), false);
        }
    }
}