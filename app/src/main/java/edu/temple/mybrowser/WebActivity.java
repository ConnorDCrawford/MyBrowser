package edu.temple.mybrowser;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class WebActivity extends AppCompatActivity implements WebViewFragment.OnWebViewLoadListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private WebPagerAdapter mWebPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int currentPage = 0;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of web page.
        mWebPagerAdapter = new WebPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mWebPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Do nothing
            }

            @Override
            public void onPageSelected(int position) {
                searchView.setQuery(mWebPagerAdapter.getPageTitle(position), false);
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Do nothing
            }
        });

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) findViewById(R.id.search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.search:
                break;
            case R.id.prev:
                if (currentPage > 0) {
                    mViewPager.setCurrentItem(currentPage - 1, true);
                }
                break;
            case R.id.next:
                if (currentPage < mViewPager.getAdapter().getCount() - 1) {
                    mViewPager.setCurrentItem(currentPage + 1, true);
                }
                break;
            case R.id.add:
                int index = mWebPagerAdapter.addItem("");
                currentPage = index;
                mViewPager.setCurrentItem(index, true);
                searchView.setQuery("", false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleIntent(Intent intent) {

        if (intent.getCategories() != null && intent.getCategories().contains(Intent.CATEGORY_BROWSABLE)) {
            Uri data = intent.getData();
            if (data != null) {
                String url = data.toString();
                int index = mWebPagerAdapter.addItem(url);
                mViewPager.setCurrentItem(index, true);
                currentPage = index;
                searchView.setQuery(url, false);
            }
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            String googleQuery = null;
            int index = mViewPager.getCurrentItem();
            if (query.contains(" ") || !query.contains(".com")) {
                googleQuery = "http://www.google.com/search?q=" + query.replaceAll(" ", "+");
            }
            if (!query.contains("http"))
                query = "http://" + query;
            mWebPagerAdapter.updateItem(index, googleQuery != null ? googleQuery : query, true);
        }
    }

    @Override
    public void onWebViewLoad(String url, int pageNumber) {
        mWebPagerAdapter.updateItem(pageNumber, url, false);
        if (pageNumber == currentPage) {
            searchView.setQuery(url, false);
        }
    }

    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class WebPagerAdapter extends FragmentStatePagerAdapter {

        private List<String> URLs = new ArrayList<>();

        public WebPagerAdapter(FragmentManager fm) {
            super(fm);
            URLs.add("");
        }

        public int addItem(String URL) {
            int index = URLs.size();
            URLs.add(URL);
            notifyDataSetChanged();
            return index;
        }

        public void updateItem(int index, String URL, boolean notify) {
            if (index >= 0 && index < URLs.size()) {
                URLs.set(index, URL);
                if (notify)
                    notifyDataSetChanged();
            }
        }

        @Override
        public Fragment getItem(int position) {
            String url = URLs.get(position);
            searchView.setQuery(url, false);
            return WebViewFragment.newInstance(url, position);
        }

        @Override
        public int getCount() {
            return URLs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return URLs.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }
}
