package com.udacity.androiddeveloper.daviladd.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TrailerList implements Serializable, Parcelable {

    public final static Parcelable.Creator<TrailerList> CREATOR = new Creator<TrailerList>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TrailerList createFromParcel(Parcel in) {
            return new TrailerList(in);
        }

        public TrailerList[] newArray(int size) {
            return (new TrailerList[size]);
        }

    };
    private final static long serialVersionUID = 3114427386563028848L;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<Trailer> results = null;

    protected TrailerList(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.results, (com.udacity.androiddeveloper.daviladd.popularmovies.data.model.Trailer.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public TrailerList() {
    }

    /**
     * @param id
     * @param results
     */
    public TrailerList(Integer id, List<Trailer> results) {
        super();
        this.id = id;
        this.results = results;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(results);
    }

    public int describeContents() {
        return 0;
    }

}

