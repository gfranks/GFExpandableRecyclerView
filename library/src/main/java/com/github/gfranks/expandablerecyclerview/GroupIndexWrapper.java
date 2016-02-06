package com.github.gfranks.expandablerecyclerview;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Garrett Franks
 * @version 1.0
 * @since 2/5/16
 */
class GroupIndexWrapper implements Parcelable, java.lang.reflect.Type {

    public final Parcelable.Creator<GroupIndexWrapper> CREATOR = new Parcelable.Creator<GroupIndexWrapper>() {
        public GroupIndexWrapper createFromParcel(Parcel in) {
            return new GroupIndexWrapper(in);
        }

        public GroupIndexWrapper[] newArray(int size) {
            return new GroupIndexWrapper[size];
        }
    };

    private boolean mIsExpanded;

    public GroupIndexWrapper() {
    }

    public GroupIndexWrapper(Parcel in) {
        readFromParcel(in);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(isExpanded() ? 1 : 0);
    }

    private void readFromParcel(Parcel in) {
        setExpanded(in.readValue(Integer.class.getClassLoader()) == 1);
    }
}