package com.example.android.githubsearchwithsqlite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.githubsearchwithsqlite.utils.GitHubUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements GitHubSearchAdapter.OnSearchItemClickListener, LoaderManager.LoaderCallbacks<String>,
            NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private final static String SEARCH_URL_KEY = "githubSearchURL";

    private final static int GITHUB_SEARCH_LOADER_ID = 0;

    private RecyclerView mSearchResultsRV;
    private EditText mSearchBoxET;
    private GitHubSearchAdapter mGitHubSearchAdapter;
    private ProgressBar mLoadingProgressBar;
    private TextView mLoadingErrorMessage;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingProgressBar = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mLoadingErrorMessage = (TextView)findViewById(R.id.tv_loading_error);

        mSearchBoxET = (EditText)findViewById(R.id.et_search_box);
        mSearchResultsRV = (RecyclerView)findViewById(R.id.rv_search_results);

        mSearchResultsRV.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsRV.setHasFixedSize(true);

        mGitHubSearchAdapter = new GitHubSearchAdapter(this);
        mSearchResultsRV.setAdapter(mGitHubSearchAdapter);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        Button searchButton = (Button)findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = mSearchBoxET.getText().toString();
                if (!TextUtils.isEmpty(searchQuery)) {
                    doGitHubSearch(searchQuery);
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.nv_navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportLoaderManager().initLoader(GITHUB_SEARCH_LOADER_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void doGitHubSearch(String searchQuery) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String sort = sharedPreferences.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default)
        );

        String language = sharedPreferences.getString(
                getString(R.string.pref_language_key),
                getString(R.string.pref_language_default)
        );

        String user = sharedPreferences.getString(
                getString(R.string.pref_user_key), ""
        );

        boolean searchInName = sharedPreferences.getBoolean(
                getString(R.string.pref_in_name_key), true
        );

        boolean searchInDescription = sharedPreferences.getBoolean(
                getString(R.string.pref_in_description_key), true
        );

        boolean searchInReadme = sharedPreferences.getBoolean(
                getString(R.string.pref_in_readme_key), true
        );

        String githubSearchURL = GitHubUtils.buildGitHubSearchURL(searchQuery, sort, language,
                user, searchInName, searchInDescription, searchInReadme);
        Bundle args = new Bundle();
        args.putString(SEARCH_URL_KEY, githubSearchURL);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        getSupportLoaderManager().restartLoader(GITHUB_SEARCH_LOADER_ID, args, this);
    }

    @Override
    public void onSearchItemClick(GitHubUtils.SearchResult searchResult) {
        Intent detailedSearchResultIntent = new Intent(this, SearchResultDetailActivity.class);
        detailedSearchResultIntent.putExtra(GitHubUtils.EXTRA_SEARCH_RESULT, searchResult);
        startActivity(detailedSearchResultIntent);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        String githubSearchURL = null;
        if (args != null) {
            githubSearchURL = args.getString(SEARCH_URL_KEY);
        }
        return new GitHubSearchLoader(this, githubSearchURL);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        Log.d(TAG, "got results from loader");
        if (data != null) {
            ArrayList<GitHubUtils.SearchResult> searchResults = GitHubUtils.parseSearchResultsJSON(data);
            mGitHubSearchAdapter.updateSearchResults(searchResults);
            mLoadingErrorMessage.setVisibility(View.INVISIBLE);
            mSearchResultsRV.setVisibility(View.VISIBLE);
        } else {
            mSearchResultsRV.setVisibility(View.INVISIBLE);
            mLoadingErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        // Nothing to do...
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_search:
                mDrawerLayout.closeDrawers();
                return true;
            case R.id.nav_saved_search_results:
                mDrawerLayout.closeDrawers();
                Intent savedResultsIntent = new Intent(this, SavedSearchResultsActivity.class);
                startActivity(savedResultsIntent);
                return true;
            case R.id.nav_settings:
                mDrawerLayout.closeDrawers();
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return false;
        }
    }
}
