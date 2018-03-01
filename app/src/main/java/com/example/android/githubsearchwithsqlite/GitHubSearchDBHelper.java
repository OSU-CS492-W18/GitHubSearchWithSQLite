package com.example.android.githubsearchwithsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hessro on 3/1/18.
 */

public class GitHubSearchDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "githubSearch.db";
    private static int DATABASE_VERSION = 1;

    public GitHubSearchDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SAVED_REPOS_TABLE =
                "CREATE TABLE " + GitHubSearchContract.SavedRepos.TABLE_NAME + "(" +
                        GitHubSearchContract.SavedRepos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        GitHubSearchContract.SavedRepos.COLUMN_FULL_NAME + " TEXT NOT NULL, " +
                        GitHubSearchContract.SavedRepos.COLUMN_DESCRIPTION + " TEXT, " +
                        GitHubSearchContract.SavedRepos.COLUMN_URL + " TEXT NOT NULL, " +
                        GitHubSearchContract.SavedRepos.COLUMN_STARS + " INTEGER DEFAULT 0, " +
                        GitHubSearchContract.SavedRepos.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");";
        db.execSQL(SQL_CREATE_SAVED_REPOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GitHubSearchContract.SavedRepos.TABLE_NAME + ";");
        onCreate(db);
    }
}
