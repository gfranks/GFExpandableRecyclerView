package com.github.gfranks.expandablerecyclerview.sample;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Type;

/**
 * @author Garrett Franks
 * @version 1.0
 * @since 2/5/16
 */
public class HorizontalChildItem implements Parcelable, Type {

    public static final String EXTRA = "child_item";
    public static final Parcelable.Creator<HorizontalChildItem> CREATOR = new Parcelable.Creator<HorizontalChildItem>() {
        public HorizontalChildItem createFromParcel(Parcel in) {
            return new HorizontalChildItem(in);
        }

        public HorizontalChildItem[] newArray(int size) {
            return new HorizontalChildItem[size];
        }
    };

    private String mText;

    public HorizontalChildItem(int index) {
        mText = String.valueOf(index);
    }

    public HorizontalChildItem(Parcel in) {
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
