package com.nsoni.flickr;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.nsoni.flickr.model.Photo;
import com.nsoni.flickr.model.Result;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends BaseActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private List<Photo> mPhotoList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private int mPageCount = 1;
    private String searchWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mRecyclerView = findViewById(R.id.item_list);
        setupRecyclerView(mRecyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new PhotosRecyclerViewAdapter(this, mPhotoList, mTwoPane));
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Photos");
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Show soft keyboard for the user to enter the value.
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String photoSearchWord) {
                searchWord = photoSearchWord;
                updatePhotoList(photoSearchWord, 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String photoSearchWord) {
                return false;
            }
        });
        searchView.setIconified(false);

        return true;
    }

    void morePhotoList() {
        updatePhotoList(searchWord, ++mPageCount);
    }

    private void updatePhotoList(String photoSearchWord, int pageCount) {
        FlickrClient client = ServiceGenerator
                .getInstance(ItemListActivity.this)
                .createService(FlickrClient.class);

        Call<Result> resultCall = client.lookupPhotos(photoSearchWord, pageCount);

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, final Response<Result> response) {
                if (response.body() != null && response.body().getPhotos() != null) {
                    ItemListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPhotoList.addAll(response.body().getPhotos().getPhoto());
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable throwable) {

            }
        });
    }
}
