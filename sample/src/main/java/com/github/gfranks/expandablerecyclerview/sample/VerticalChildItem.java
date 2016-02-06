package com.github.gfranks.expandablerecyclerview.sample;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Type;

/**
 * @author Garrett Franks
 * @version 1.0
 * @since 2/5/16
 */
public class VerticalChildItem implements Parcelable, Type {

    public static final String EXTRA = "child_item";
    public static final Parcelable.Creator<VerticalChildItem> CREATOR = new Parcelable.Creator<VerticalChildItem>() {
        public VerticalChildItem createFromParcel(Parcel in) {
            return new VerticalChildItem(in);
        }

        public VerticalChildItem[] newArray(int size) {
            return new VerticalChildItem[size];
        }
    };

    private String mText;

    public VerticalChildItem() {
        mText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    }

    public VerticalChildItem(Parcel in) {
        readFromParcel(in);
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof VerticalChildItem) {
            return ((VerticalChildItem) o).getText().equals(getText());
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeValue(getText());
    }

    private void readFromParcel(Parcel in) {
        setText((String) in.readValue(String.class.getClassLoader()));
    }
}
