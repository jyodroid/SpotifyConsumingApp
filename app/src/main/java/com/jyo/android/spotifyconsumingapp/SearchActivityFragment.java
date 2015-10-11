package com.jyo.android.spotifyconsumingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jyo.android.spotifyconsumingapp.commons.InternetUtils;
import com.jyo.android.spotifyconsumingapp.connection.SearchArtistAlbumsTask;
import com.jyo.android.spotifyconsumingapp.connection.SearchArtistTask;
import com.jyo.android.spotifyconsumingapp.model.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.AlbumSimple;

public class SearchActivityFragment extends Fragment {

    private static final String LOG_TAG = SearchActivityFragment.class.getSimpleName();
    private static final String SPOTIFY_KEY = "spotify";
    private static final String ALBUM_LIST = "album_list";
    private static final String ARTIST_NAME = "artist_name";
    private static final String ARTIST_IMAGE = "artist_image";
    private static final String ARTIST_FOLLOWERS = "artist_followers";
    private static final String ARTIST_POPULARITY = "artist_popularity";

    private SearchAdapter searchAdapter;
    private String artistName;
    private Bitmap mArtistImage;
    private ViewHolder mViewHolder;
    private ArtistViewHolder mArtistViewHolder;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getActivity().getBaseContext();

        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        //Sync view
        mViewHolder = new ViewHolder(rootView);

        searchAdapter = new SearchAdapter(context, new ArrayList<AlbumSimple>());

        mViewHolder.albumList.setAdapter(searchAdapter);

        final View artistView = inflater.inflate(R.layout.list_first_item, container,false);
        mArtistViewHolder = new ArtistViewHolder(artistView);
        mViewHolder.albumList.addHeaderView(artistView);
        artistName = mViewHolder.searchArtistName.getText().toString();

        mViewHolder.searchArtistName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!InternetUtils.isInternetAvailable(getActivity().getBaseContext())) {

                    Snackbar.make(rootView, "Not internet available!", Snackbar.LENGTH_LONG).show();

                } else {
                    if (actionId == EditorInfo.IME_ACTION_DONE ||
                            (event.getAction() == KeyEvent.ACTION_DOWN &&
                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        if (null != v.getText() && 0 != v.getText().length()) {
                            artistName = v.getText().toString();
                            updateArtist(context, artistView);
                            updateAlbum(context);
                        }
                    }
                }
                return false;
            }
        });

        mViewHolder.albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!searchAdapter.isEmpty()) {

                    if (position != 0) {
                        Intent detailActivityIntent = new Intent(context, LinkActivity.class);

                        //If there is a image
                        if (0 < searchAdapter.getItem(position).images.size()) {

                            detailActivityIntent.putExtra(getString(R.string.album_image_url),
                                    searchAdapter.getItem(position).images.get(0).url);
                        }

                        //If spotify link exists
                        if (!searchAdapter.getItem(position).external_urls.get(SPOTIFY_KEY).isEmpty()) {
                            detailActivityIntent.putExtra(getString(R.string.album_link),
                                    searchAdapter.getItem(position).external_urls.get(SPOTIFY_KEY));
                        }
                        startActivity(detailActivityIntent);
                    }
                }
            }
        });
        return rootView;
    }

    private void updateArtist(Context context, View view){
        SearchArtistTask fetchArtistTask = new SearchArtistTask(context, view, mArtistImage);
        fetchArtistTask.execute(artistName);
    }

    private void updateAlbum(Context context){
        SearchArtistAlbumsTask fetchAlbumTask =
                new SearchArtistAlbumsTask(searchAdapter, context, mViewHolder.mProgressBar);
        fetchAlbumTask.execute(artistName);
    }

    static class ViewHolder {
        @Bind(R.id.listview_search)
        ListView albumList;
        @Bind(R.id.edit_txt_artist_name)
        EditText searchArtistName;
        @Bind(R.id.main_progress)
        ProgressBar mProgressBar;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ArtistViewHolder {
        @Bind(R.id.img_artist_image)
        ImageView artistImage;
        @Bind(R.id.txt_artist_name)
        TextView artistName;
        @Bind(R.id.txt_followers)
        TextView artistFollowers;
        @Bind(R.id.txt_popularity)
        TextView artistPopularity;

        public ArtistViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(
                ALBUM_LIST,
                (ArrayList<AlbumSimple>) searchAdapter.getAlbumList());

        //Artist Data
        outState.putString(
                ARTIST_NAME,
                mArtistViewHolder.artistName.getText().toString());

        outState.putString(
                ARTIST_FOLLOWERS,
                mArtistViewHolder.artistFollowers.getText().toString());

        outState.putString(
                ARTIST_POPULARITY,
                mArtistViewHolder.artistPopularity.getText().toString());

        outState.putParcelable(
                ARTIST_IMAGE,
                mArtistImage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.get(ALBUM_LIST) != null){
                searchAdapter.addAll((List<AlbumSimple>) savedInstanceState.get(ALBUM_LIST));
            }
            if (savedInstanceState.get(ARTIST_NAME) != null){
                mArtistViewHolder.artistName.setText(savedInstanceState.getString(ARTIST_NAME));
            }
            if (savedInstanceState.get(ARTIST_FOLLOWERS) != null){
                mArtistViewHolder.artistFollowers.setText(savedInstanceState.getString(ARTIST_FOLLOWERS));
            }
            if (savedInstanceState.get(ARTIST_POPULARITY) != null){
                mArtistViewHolder.artistPopularity.setText(savedInstanceState.getString(ARTIST_POPULARITY));
            }

            if (savedInstanceState.get(ARTIST_IMAGE) != null){
                mArtistViewHolder.artistImage
                        .setImageBitmap((Bitmap) savedInstanceState.getParcelable(ARTIST_IMAGE));
            }
        }
    }
}
