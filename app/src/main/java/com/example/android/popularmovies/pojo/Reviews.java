package com.example.android.popularmovies.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Noah on 3/31/2017.
 */

public class Reviews implements Parcelable{
    private String mId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    public Reviews(String id, String author, String content, String url) {
        mId = id;
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    protected Reviews(Parcel in) {
        mId = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
    }

    public static final Creator<Reviews> CREATOR = new Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    public String getId() { return mId;}
    public String getAuthor() {return mAuthor;}
    public String getContent() {return mContent;}
    public String getUrl() {return mUrl;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeString(mUrl);
    }
}
