package com.travoca.api.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Record implements Parcelable {

    public String lang;

    public int id;

    public String title;

    public String description;

    public String locationName;

    public Double lat;

    public Double lon;

    public String imageUrl;

    public int likes;

    public int unLikes;

    public String date;

    public String recordUrl;

    public String type;

    public String canVote;

    protected Record(Parcel in) {
        id =in.readInt();
        lang = in.readString();
        title = in.readString();
        description = in.readString();
        locationName = in.readString();
        String tmp = in.readString();
        lat = tmp.equals("null") ? 0 : Double.valueOf(tmp);
        tmp = in.readString();
        lon = tmp.equals("null") ? 0 : Double.valueOf(tmp);
        imageUrl = in.readString();
        likes = in.readInt();
        unLikes = in.readInt();
        date = in.readString();
        recordUrl = in.readString();
        type = in.readString();
        canVote = in.readString();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(lang);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(locationName);
        dest.writeString(String.valueOf(lat));
        dest.writeString(String.valueOf(lon));
        dest.writeString(imageUrl);
        dest.writeInt(likes);
        dest.writeInt(unLikes);
        dest.writeString(date);
        dest.writeString(recordUrl);
        dest.writeString(type);
        dest.writeString(canVote);
    }
}