package com.jyo.android.spotifyconsumingapp.connection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jyo.android.spotifyconsumingapp.model.SearchAdapter;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumsPager;

/**
 * Created by JohnTangarife on 25/06/15.
 */
public class SearchArtistAlbumsTask extends AsyncTask<String, Void, AlbumsPager> {

    private final String LOG_TAG = SearchAdapter.class.getCanonicalName();
    private SearchAdapter searchAdapter;
    private Context context;
    private ProgressBar mProgressBar;

    public SearchArtistAlbumsTask(SearchAdapter searchAdapter, Context context, ProgressBar progressBar) {

        this.searchAdapter = searchAdapter;
        this.context = context;
        this.mProgressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected AlbumsPager doInBackground(String... params) {

        //Use of spotify wrapper
        try {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();

            return spotifyService.searchAlbums(params[0]);

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(AlbumsPager result) {
        //Obtaining albums
        searchAdapter.clear();
        if (0 == result.albums.items.size()) {
            if (context != null) {
                CharSequence text = "No Album found for artist. Please type another artist";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        } else {
            searchAdapter.addAll(result.albums.items);
        }
        mProgressBar.setVisibility(View.GONE);
    }
}
