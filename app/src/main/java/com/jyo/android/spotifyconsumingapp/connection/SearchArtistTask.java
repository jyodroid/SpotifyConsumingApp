package com.jyo.android.spotifyconsumingapp.connection;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jyo.android.spotifyconsumingapp.R;
import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by JohnTangarife on 10/10/15.
 */
public class SearchArtistTask extends AsyncTask<String, Void, Artist> {

    private final String LOG_TAG = SearchArtistTask.class.getCanonicalName();

    private View mView;
    private Context context;
    private String artistName;

    public SearchArtistTask(Context context, View view){
        this.mView = view;
        this.context = context;
    }

    @Override
    protected Artist doInBackground(String... params) {

        //Use of spotify wrapper
        try {
            this.artistName = params[0];
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();

            ArtistsPager artists = spotifyService.searchArtists(artistName);
            if (0 == artists.artists.items.size()){
                return null;
            }else {
                return artists.artists.items.get(0);
            }
        }catch (Exception e){
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Artist result) {

        //declare and clean fields
        TextView artistName = (TextView) mView.findViewById(R.id.txt_artist_name);
        artistName.setText("");

        TextView artistPopularity = (TextView) mView.findViewById(R.id.txt_popularity);
        artistPopularity.setText("");

        TextView artistFollowers = (TextView) mView.findViewById(R.id.txt_followers);
        artistFollowers.setText("");

        ImageView image = (ImageView) mView.findViewById(R.id.img_artist_image);
        image.setImageDrawable(null);

        //Update artist info in view
        if (result == null){
            if(context != null){
                CharSequence text = "No no artist named "+ this.artistName + " found";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }else {
            artistName.setText(result.name);

            artistPopularity.setText(String.format(context.getString(R.string.popularity_content),
                    result.popularity));

            artistFollowers.setText(String.format(context.getString(R.string.followers_content),
                    result.followers.total));

            if(result.images != null && result.images.size() > 0){
                Picasso.with(context).load(result.images.get(0).url).into(image);
            }else {
                image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.no_image));
            }
        }
    }
}
