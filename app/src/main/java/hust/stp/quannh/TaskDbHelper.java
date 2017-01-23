package hust.stp.quannh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by sev_user on 1/20/2017.
 */
public class TaskDbHelper extends SQLiteOpenHelper implements BaseColumns {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TaskData.db";
    public static final String TABLE_NAME = "tasks";

    public static final String COLUMN_NAME_ROWID = "_id";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_NOTES = "notes";
    public static final String COLUMN_NAME_DATE = "task_date";
    public static final String COLUMN_NAME_TIME = "task_time";
    public static final String COLUMN_NAME_STATUS = "task_status";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COLUMN_NAME_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_NAME_NAME + " TEXT NOT NULL, "
                    + COLUMN_NAME_NOTES + " TEXT, "
                    + COLUMN_NAME_DATE + " TEXT, "
                    + COLUMN_NAME_TIME + " TEXT, "
                    + COLUMN_NAME_STATUS + " TEXT);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public long createTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId;

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_NAME, task.getName());
        values.put(COLUMN_NAME_NOTES, task.getNotes());
        values.put(COLUMN_NAME_DATE, task.getDate());
        values.put(COLUMN_NAME_TIME, task.getTime());
        values.put(COLUMN_NAME_STATUS, task.getIsChecked());

        newRowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return newRowId;
    }

    public ArrayList<Task> readAllTasks() {
        ArrayList<Task> taskList = new ArrayList<Task>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if(cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME));
                    String notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NOTES));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DATE));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_TIME));
                    long row_id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_ROWID));
                    boolean status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_STATUS)) > 0;

                    Task task = new Task(name, notes, date, time, row_id, status);
                    taskList.add(0, task);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }


        return taskList;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_NAME, task.getName());
        values.put(COLUMN_NAME_NOTES, task.getNotes());
        values.put(COLUMN_NAME_DATE, task.getDate());
        values.put(COLUMN_NAME_TIME, task.getTime());
        values.put(COLUMN_NAME_STATUS, task.getIsChecked());

        return db.update(TABLE_NAME, values, COLUMN_NAME_ROWID + " = ?",
                new String[] { String.valueOf(task.getDbRowId()) });
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, COLUMN_NAME_ROWID + " = ?",
                new String[] { String.valueOf(task.getDbRowId()) });
        db.close();
    }

    public void updateTaskStatus(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_STATUS, task.getIsChecked());
        db.update(TABLE_NAME, values, COLUMN_NAME_ROWID + " = ?",
                    new String[] { String.valueOf(task.getDbRowId()) });
    }

    public ArrayList<Task> readTasksByStatus(boolean checked) {
        ArrayList<Task> taskList = new ArrayList<Task>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_STATUS + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        if(checked)
            cursor = db.rawQuery(selectQuery, new String[] {"1"});
        else
            cursor = db.rawQuery(selectQuery, new String[] {"0"});

        try {
            if(cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME));
                    String notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NOTES));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DATE));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_TIME));
                    long row_id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_ROWID));
                    boolean status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_STATUS)) > 0;

                    Task task = new Task(name, notes, date, time, row_id, status);
                    taskList.add(0, task);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return taskList;
    }

    public ArrayList<Task> searchTask(String str) {
        ArrayList<Task> taskList = new ArrayList<Task>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_NAME + " LIKE '%" + str + "%'" +
                " OR " + COLUMN_NAME_NOTES + " LIKE '%" + str + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if(cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME));
                    String notes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NOTES));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_DATE));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_TIME));
                    long row_id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_ROWID));
                    boolean status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_STATUS)) > 0;

                    Task task = new Task(name, notes, date, time, row_id, status);
                    taskList.add(0, task);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return taskList;
    }
}
