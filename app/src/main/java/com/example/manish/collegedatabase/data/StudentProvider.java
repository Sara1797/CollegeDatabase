package com.example.manish.collegedatabase.data;

/**
 * Created by manish on 01-Oct-17.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
/**
 * {@link ContentProvider} for Pets app.
 */
public class StudentProvider extends ContentProvider {
    /** URI matcher code for the content URI for the pets table */
    private static final int STUDENTS = 100;
    private StudentDbHelper mDbHelper ;
    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int STUDENT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(StudentContract.CONTENT_AUTHORITY,StudentContract.PATH_STUDENTS,STUDENTS);
        sUriMatcher.addURI(StudentContract.CONTENT_AUTHORITY,StudentContract.PATH_STUDENTS +"/#",STUDENT_ID);
    }

    /** Tag for the log messages */
    public static final String LOG_TAG = StudentProvider.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new StudentDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null ;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STUDENTS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(StudentContract.StudentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case STUDENT_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = StudentContract.StudentEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(StudentContract.StudentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STUDENTS:
                return StudentContract.StudentEntry.CONTENT_LIST_TYPE;
            case STUDENT_ID:
                return StudentContract.StudentEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STUDENTS:
                return insertStudent(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertStudent(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(StudentContract.StudentEntry.COLUMN_STUDENT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Student requires a name");
        }
        // Check that the gender is valid
        Integer gender = values.getAsInteger(StudentContract.StudentEntry.COLUMN_STUDENT_GENDER);
        if (gender == null || !StudentContract.StudentEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Student requires valid gender");
        }

        Integer phoneNo = values.getAsInteger(StudentContract.StudentEntry.COLUMN_STUDENT_PHONE_NO);
        if (phoneNo == null) {
            throw new IllegalArgumentException("Student requires a phone No");
        }
        if (phoneNo != null && phoneNo < 0) {
            throw new IllegalArgumentException("Student requires valid phone no");
        }
        Integer rollNo = values.getAsInteger(StudentContract.StudentEntry.COLUMN_STUDENT_ID);
        if (rollNo == null) {
            throw new IllegalArgumentException("Student requires a roll No");
        }
        if (rollNo != null && rollNo < 0) {
            throw new IllegalArgumentException("Student requires valid roll no");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(StudentContract.StudentEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);

    }



    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STUDENTS:
                return updateStudent(uri, contentValues, selection, selectionArgs);
            case STUDENT_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = StudentContract.StudentEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateStudent(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateStudent(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(StudentContract.StudentEntry.COLUMN_STUDENT_NAME)) {
            String name = values.getAsString(StudentContract.StudentEntry.COLUMN_STUDENT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Student requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(StudentContract.StudentEntry.COLUMN_STUDENT_GENDER)) {
            Integer gender = values.getAsInteger(StudentContract.StudentEntry.COLUMN_STUDENT_GENDER);
            if (gender == null || !StudentContract.StudentEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Student requires valid gender");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(StudentContract.StudentEntry.COLUMN_STUDENT_PHONE_NO)) {
            Long phoneNo = values.getAsLong(StudentContract.StudentEntry.COLUMN_STUDENT_PHONE_NO);
            if (phoneNo != null && phoneNo < 0) {
                throw new IllegalArgumentException("Student requires valid phone no");
            }
        }

        if (values.containsKey(StudentContract.StudentEntry.COLUMN_STUDENT_ID)) {
            Integer rollNo = values.getAsInteger(StudentContract.StudentEntry.COLUMN_STUDENT_ID);
            if (rollNo != null && rollNo < 0) {
                throw new IllegalArgumentException("Student requires valid roll no");
            }
        }
        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(StudentContract.StudentEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STUDENTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(StudentContract.StudentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STUDENT_ID:
                // Delete a single row given by the ID in the URI
                selection = StudentContract.StudentEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(StudentContract.StudentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

}