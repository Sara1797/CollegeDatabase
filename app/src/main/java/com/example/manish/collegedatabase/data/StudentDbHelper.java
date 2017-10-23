package com.example.manish.collegedatabase.data;

/**
 * Created by manish on 01-Oct-17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class StudentDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = StudentDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "student.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link StudentDbHelper}.
     *
     * @param context of the app
     */
    public StudentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_STUDENTS_TABLE =  "CREATE TABLE " + StudentContract.StudentEntry.TABLE_NAME + " ("
                + StudentContract.StudentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StudentContract.StudentEntry.COLUMN_STUDENT_NAME + " TEXT NOT NULL, "
                + StudentContract.StudentEntry.COLUMN_STUDENT_ID + " INTEGER NOT NULL, "
                + StudentContract.StudentEntry.COLUMN_STUDENT_GENDER + " INTEGER NOT NULL, "
                + StudentContract.StudentEntry.COLUMN_STUDENT_PHONE_NO + " INTEGER NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_STUDENTS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}