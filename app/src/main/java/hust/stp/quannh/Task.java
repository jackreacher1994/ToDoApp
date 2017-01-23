package hust.stp.quannh;

/**
 * Created by sev_user on 1/20/2017.
 */
public class Task {
    private String mName;
    private String mNotes;
    private String mDate;
    private String mTime;
    private long mDbRowId;
    private boolean mIsChecked;

    public Task(String name, String notes, String date, String time,
                long dbRowId, boolean status) {
        super();
        this.mName = name;
        this.mNotes = notes;
        this.mDate = date;
        this.mTime = time;
        this.mDbRowId = dbRowId;

        this.mIsChecked = status;
    }

    public Task(String name, String notes, String date, String time) {
        super();
        this.mName = name;
        this.mNotes = notes;
        this.mDate = date;
        this.mTime = time;

        this.mIsChecked = false;
    }

    public String getName() {
        return mName;
    }
    public String getNotes() {
        return mNotes;
    }
    public String getDate() {
        return mDate;
    }
    public String getTime() {
        return mTime;
    }
    public boolean getIsChecked() {return mIsChecked; }
    public long getDbRowId() { return mDbRowId; }

    public void setName(String newName) {
        mName = newName;
    }
    public void setNotes(String newNotes) {
        mNotes = newNotes;
    }
    public void setDate(String newDate) {
        mDate = newDate;
    }
    public void setTime(String newTime) {
        mTime = newTime;
    }
    public void setIsChecked(boolean newIsChecked) { mIsChecked = newIsChecked; }
    public void setDbRowId(long newDbRowId) { mDbRowId = newDbRowId; }
}
