package com.example.android.githubsearchwithsqlite;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.githubsearchwithsqlite.utils.GitHubUtils;

public class SearchResultDetailActivity extends AppCompatActivity {

    private TextView mTVSearchResultName;
    private TextView mTVSearchResultStars;
    private TextView mTVSearchResultDescription;
    private ImageView mIVSearchResultBookmark;
    private boolean mIsBookmarked = false;

    private GitHubUtils.SearchResult mSearchResult;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_detail);

        mTVSearchResultName = findViewById(R.id.tv_search_result_name);
        mTVSearchResultStars = findViewById(R.id.tv_search_result_stars);
        mTVSearchResultDescription = findViewById(R.id.tv_search_result_description);
        mIVSearchResultBookmark = findViewById(R.id.iv_search_result_bookmark);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(GitHubUtils.EXTRA_SEARCH_RESULT)) {
            mSearchResult = (GitHubUtils.SearchResult) intent.getSerializableExtra(GitHubUtils.EXTRA_SEARCH_RESULT);
            mTVSearchResultName.setText(mSearchResult.fullName);
            mTVSearchResultStars.setText(String.valueOf(mSearchResult.stars));
            mTVSearchResultDescription.setText(mSearchResult.description);
        }

        mIVSearchResultBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsBookmarked = !mIsBookmarked;
                if (mIsBookmarked) {
                    mIVSearchResultBookmark.setImageResource(R.drawable.ic_bookmark_black_48dp);
                } else {
                    mIVSearchResultBookmark.setImageResource(R.drawable.ic_bookmark_border_black_48dp);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_repo:
                viewRepoOnWeb();
                return true;
            case R.id.action_share:
                shareRepo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void viewRepoOnWeb() {
        if (mSearchResult != null) {
            Uri githubRepoURL = Uri.parse(mSearchResult.htmlURL);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, githubRepoURL);
            if (webIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(webIntent);
            }
        }
    }

    public void shareRepo() {
        if (mSearchResult != null) {
            String shareText = getString(R.string.share_text_prefix) + ": " +
                  mSearchResult.fullName + ", " + mSearchResult.htmlURL;

            ShareCompat.IntentBuilder.from(this)
                    .setChooserTitle(R.string.share_chooser_title)
                    .setType("text/plain")
                    .setText(shareText)
                    .startChooser();
        }
    }
}
