package com.jeff_fennell.dataEntities;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

/**
 * Created by jeff on 6/20/15.
 */
public class Task  implements Parcelable{
    private String title;
    private Long dateCreated;
    private String details;
    private String complete;
    public static final String TASK_COMPLETE = "T";
    public static final String TASK_NOT_COMPLETE = "F";

    public Task(String title, Long dateCreated, String details, String complete) {
        this.title = title;
        this.dateCreated = dateCreated;
        this.details = details;
        this.complete = complete;
    }

    private Task(Parcel in) {
        title = in.readString();
        dateCreated = in.readLong();
        details = in.readString();
        complete = in.readString();
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeLong(dateCreated);
        out.writeString(details);
        out.writeString(complete);
    }

    // this is used to regenerate Task object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel in) {
        return new Task(in.readString(), in.readLong(), in.readString(), in.readString());
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String toString() {
        return ("{title: " + title
                + ", details: " + details
                + ", dateCreated: " + dateCreated
                + ", complete: " + complete + "}");
    }
}
