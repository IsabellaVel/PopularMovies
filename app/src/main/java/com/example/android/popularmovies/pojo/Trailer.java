package com.example.android.popularmovies.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by noahkim on 4/2/17.
 */

public class Trailer implements Parcelable {
    private String mId;
    private String mKey;
    private String mName;
    private String mSite;
    private String mSize;

    public Trailer(String id, String key, String name, String site, String size) {
        mId = id;
        mKey = key;
        mName = name;
        mSite = site;
        mSize = size;
    }

    protected Trailer(Parcel in) {
        mId = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mSite = in.readString();
        mSize = in.readString();
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

    public String getId() {return mId;}
    public String getKey() {return mKey;}
    public String getName() {return mName;}
    public String getSite() {return mSite;}
    public String getSize() {return mSize;}
    public String getThumbnail() {return "http://img.youtube.com/vi/" + mKey + "/0.jpg";}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mKey);
        parcel.writeString(mName);
        parcel.writeString(mSite);
        parcel.writeString(mSize);
    }
}
