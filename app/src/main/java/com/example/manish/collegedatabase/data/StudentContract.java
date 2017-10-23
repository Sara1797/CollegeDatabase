package com.example.manish.collegedatabase.data;

/**
 * Created by manish on 01-Oct-17.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by manish on 06-Jul-17.
 */

public final class StudentContract {


    private StudentContract() {}



    public static final String CONTENT_AUTHORITY = "com.example.manish.collegedatabase";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.students/students/ is a valid path for
     * looking at student data. content://com.example.android.students/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_STUDENTS = "students";

    /**
     * Inner class that defines constant values for the students database table.
     * Each entry in the table represents a single student.
     */
    public static final class StudentEntry implements BaseColumns {
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of students.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single student.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENTS;
        /** The content URI to access the student data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STUDENTS);
        /** Name of database table for students */
        public final static String TABLE_NAME = "students";

        /**
         * Unique ID number for the student (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the student.
         *
         * Type: TEXT
         */
        public final static String COLUMN_STUDENT_NAME ="name";

        /**
         * ID od student.
         *
         * Type: TEXT
         */
        public final static String COLUMN_STUDENT_ID = "id";

        /**
         * Gender of the student.
         *
         * The only possible values are {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_STUDENT_GENDER = "gender";

        /**
         * phone number of student.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_STUDENT_PHONE_NO = "phone no";

        /**
         * Possible values for the gender of the pet.
         */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }
    }

}
