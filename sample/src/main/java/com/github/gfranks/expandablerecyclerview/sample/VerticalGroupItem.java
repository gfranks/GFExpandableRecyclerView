package com.github.gfranks.expandablerecyclerview.sample;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Garrett Franks
 * @version 1.0
 * @since 2/5/16
 */
public class VerticalGroupItem implements Parcelable, Type {

    public static final String EXTRA = "group_item";
    public static final Parcelable.Creator<VerticalGroupItem> CREATOR = new Parcelable.Creator<VerticalGroupItem>() {
        public VerticalGroupItem createFromParcel(Parcel in) {
            return new VerticalGroupItem(in);
        }

        public VerticalGroupItem[] newArray(int size) {
            return new VerticalGroupItem[size];
        }
    };

    private String mTitle;
    private List<VerticalChildItem> mVerticalChildItems;

    public VerticalGroupItem(String title) {
        mTitle = title;
        generateChildItems();
    }

    public VerticalGroupItem(Parcel in) {
        readFromParcel(in);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setChildItems(List<VerticalChildItem> verticalChildItems) {
        this.mVerticalChildItems = verticalChildItems;
    }

    public List<VerticalChildItem> getChildItems() {
        return mVerticalChildItems;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof VerticalGroupItem) {
            return ((VerticalGroupItem) o).getTitle().equals(getTitle());
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(getTitle());
        out.writeList(getChildItems());
    }

    private void readFromParcel(Parcel in) {
        setTitle((String) in.readValue(String.class.getClassLoader()));
        setChildItems(in.readArrayList(VerticalChildItem.class.getClassLoader()));
    }

    void generateChildItems() {
        mVerticalChildItems = new ArrayList<>();
        for (int i=0; i<5; i++) {
            mVerticalChildItems.add(new VerticalChildItem());
        }
    }
}
