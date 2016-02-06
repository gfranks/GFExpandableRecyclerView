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
public class HorizontalGroupItem implements Parcelable, Type {

    public static final String EXTRA = "group_item";
    public static final Parcelable.Creator<HorizontalGroupItem> CREATOR = new Parcelable.Creator<HorizontalGroupItem>() {
        public HorizontalGroupItem createFromParcel(Parcel in) {
            return new HorizontalGroupItem(in);
        }

        public HorizontalGroupItem[] newArray(int size) {
            return new HorizontalGroupItem[size];
        }
    };

    private String mTitle;
    private List<HorizontalChildItem> mVerticalChildItems;

    public HorizontalGroupItem(String title) {
        mTitle = title;
        generateChildItems();
    }

    public HorizontalGroupItem(Parcel in) {
        readFromParcel(in);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setChildItems(List<HorizontalChildItem> verticalChildItems) {
        this.mVerticalChildItems = verticalChildItems;
    }

    public List<HorizontalChildItem> getChildItems() {
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
            mVerticalChildItems.add(new HorizontalChildItem(i+1));
        }
    }
}
