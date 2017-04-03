package com.example.android.popularmovies.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by noahkim on 4/2/17.
 */

public class Trailer implements Parcelable {
    private String mName;
    private String mSize;
    private String mSource;
    private String mType;

    public Trailer(String name, String size, String source, String type) {
        mName = name;
        mSize = size;
        mSource = source;
        mType = type;
    }

    protected Trailer(Parcel in) {
        mName = in.readString();
        mSize = in.readString();
        mSource = in.readString();
        mType = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public String getName() {return mName;}
    public String getSize() {return mSize;}
    public String getThumbnail() {return "http://img.youtube.com/vi/" + mSource + "/0.jpg";}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mSize);
        parcel.writeString(mSize);
        parcel.writeString(mType);
    }
}
