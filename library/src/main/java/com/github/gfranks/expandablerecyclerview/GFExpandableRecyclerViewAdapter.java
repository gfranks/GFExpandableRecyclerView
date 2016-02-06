package com.github.gfranks.expandablerecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Garrett Franks
 * @version 1.0
 * @since 2/5/16
 */
public abstract class GFExpandableRecyclerViewAdapter<GI, CI, GVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RecyclerView mRecyclerView;
    private OnGroupExpandCollapseListener mOnGroupExpandCollapseListener;
    private OnGroupClickListener mOnGroupClickListener;
    private OnChildClickListener mOnChildClickListener;
    private RecyclerView.AdapterDataObserver mDataObserver;

    private List<GroupIndexWrapper> mGroupItems;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
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
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (Type.values()[viewType]) {
            case GROUP:
                return onCreateGroupViewHolder(parent, viewType);
            case CHILD:
                return onCreateChildViewHolder(parent, viewType);
        }
        return null;
    }

    @Override
    public final void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
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
                            getGroupItemWrapper(groupPosition).toggle(mOnGroupExpandCollapseListener, groupPosition);
                        }
                        if (mOnGroupClickListener != null) {
                            mOnGroupClickListener.onGroupClick(holder.itemView, groupPosition);
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
                            mOnChildClickListener.onChildClick(holder.itemView, groupPosition, getPosition(position, false));
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

    public void insertGroupItem(GI groupItem, boolean expanded) {
        getGroupItems().add(groupItem);
        mGroupItems.add(new GroupIndexWrapper(expanded));
        notifyItemInserted(getCountLeadingUpToGroup(getGroupCount() - 1));
    }

    public void insertGroupItem(GI groupItem, int position, boolean expanded) {
        getGroupItems().add(position, groupItem);
        mGroupItems.add(position, new GroupIndexWrapper(expanded));
        notifyItemInserted(getCountLeadingUpToGroup(position));
    }

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

    public void collapseGroup(int position) {
        if (isGroupExpanded(position)) {
            getGroupItemWrapper(position).setExpanded(false);
            notifyItemChanged(getCountLeadingUpToGroup(position));
            notifyItemRangeRemoved(getCountLeadingUpToGroup(position) + 1, getChildCount(position));
        }
    }

    public void scrollToGroup(int position) {
        if (mRecyclerView != null) {
            mRecyclerView.getLayoutManager().scrollToPosition(getCountLeadingUpToGroup(position) + 1);
        }
    }

    public boolean isGroupExpanded(int position) {
        return getGroupItemWrapper(position).isExpanded();
    }

    private void ensureGroupItemWrappers() {
        if (mGroupItems == null) {
            mGroupItems = new ArrayList<GroupIndexWrapper>();
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

    private int getGroupPosition(int position) {
        for (int i=0; i<getGroupCount(); i++) {
            int pos = getCountLeadingUpToGroup(i);
            if (position == pos) {
                return i;
            } else {
                if (getGroupItemWrapper(i).isExpanded()) {
                    for (int j = 0; j < getChildCount(i); j++) {
                        ++pos;
                        if (position == pos) {
                            return i;
                        }
                    }
                }
            }
        }

        return -1;
    }

    private int getChildPosition(int position) {
        for (int i=0; i<getGroupCount(); i++) {
            int pos = getCountLeadingUpToGroup(i);
            if (getGroupItemWrapper(i).isExpanded()) {
                for (int j = 0; j < getChildCount(i); j++) {
                    ++pos;
                    if (position == pos) {
                        return j;
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
                    ensureGroupItemWrappers();
                }
            };
        }

        return mDataObserver;
    }

    public enum Type {
        GROUP,
        CHILD
    }

    private class GroupIndexWrapper {

        private boolean mIsExpanded;

        public GroupIndexWrapper() {
        }

        public GroupIndexWrapper(boolean isExpanded) {
            mIsExpanded = isExpanded;
        }

        public boolean isExpanded() {
            return mIsExpanded;
        }

        public void setExpanded(boolean isExpanded) {
            mIsExpanded = isExpanded;
        }

        public void toggle(OnGroupExpandCollapseListener listener, int position) {
            if (isExpanded()) {
                collapseGroup(position);
                if(listener != null) {
                    listener.onGroupCollapse(position);
                }
            } else {
                expandGroup(position);
                if(listener != null) {
                    listener.onGroupExpand(position);
                }
                scrollToGroup(position);
            }
        }
    }

    public abstract List<GI> getGroupItems();
    public abstract GI getGroupItem(int groupPosition);
    public abstract CI getChildItem(int groupPosition, int childPosition);
    public abstract int getGroupCount();
    public abstract int getChildCount(int groupPosition);
    public abstract boolean isGroupInitiallyExpanded(int groupPosition);
    public abstract GVH onCreateGroupViewHolder(ViewGroup parent, int viewType);
    public abstract CVH onCreateChildViewHolder(ViewGroup parent, int viewType);
    public abstract void onBindGroupViewHolder(GVH holder, int groupPosition, GI groupItem, boolean isExpanded);
    public abstract void onBindChildViewHolder(CVH holder, int groupPosition, int childPosition, CI childItem);

    public interface OnGroupExpandCollapseListener {
        boolean isGroupExpandable(int groupPosition);
        void onGroupExpand(int groupPosition);
        void onGroupCollapse(int groupPosition);
    }

    public interface OnGroupClickListener {
        void onGroupClick(View v, int position);
    }

    public interface OnChildClickListener {
        void onChildClick(View v, int groupPosition, int childPosition);
    }
}