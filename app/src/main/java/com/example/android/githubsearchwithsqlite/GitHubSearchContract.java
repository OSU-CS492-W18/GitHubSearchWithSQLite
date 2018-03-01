package com.example.android.githubsearchwithsqlite;

import android.provider.BaseColumns;

/**
 * Created by hessro on 3/1/18.
 */

public class GitHubSearchContract {
    private GitHubSearchContract() {}
    public static class SavedRepos implements BaseColumns {
        public static final String TABLE_NAME = "savedRepos";
        public static final String COLUMN_FULL_NAME = "fullName";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_STARS = "stars";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
