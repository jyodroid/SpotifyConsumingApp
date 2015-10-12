package com.jyo.android.spotifyconsumingapp.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyo.android.spotifyconsumingapp.R;
import com.jyo.android.spotifyconsumingapp.commons.CountryFinder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.AlbumSimple;

/**
 * Created by JohnTangarife on 10/10/15.
 */
public class SearchAdapter extends ArrayAdapter<AlbumSimple> {

    private static final String LOG_TAG = SearchAdapter.class.getSimpleName();

    private List<AlbumSimple> albumResults;
    private Context context;

    // Flag to determine if we want to use the artist view.
    private boolean mArtistLayout = true;

    private static final int VIEW_TYPE_ARTIST = 0;
    private static final int VIEW_TYPE_ALBUM = 1;

    public SearchAdapter(Context context, List<AlbumSimple> albumResults){
        super(context, R.layout.list_item_serach, albumResults);
        this.context = context;
        this.albumResults = albumResults;
    }

    @Override
    public int getCount() {
        return albumResults.size();
    }

    @Override
    public AlbumSimple getItem(int position) {
        return albumResults.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int COUNTRIES_LIMIT = 5;

        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.list_item_serach, null);
        ViewHolder viewHolder = new ViewHolder(item);

        AlbumSimple album = getItem(position);
        viewHolder.albumName.setText(album.name);

        //Check if countries are more than the limit
        if (album.available_markets.size() < COUNTRIES_LIMIT){

            //If are less than the limit, Show the countries name
            StringBuilder builder = new StringBuilder("Countries availability: ");
            for (String countryCode: album.available_markets) {
                try {
                    String countryName = CountryFinder.findCountryByCode(countryCode, context);
                    builder.append(countryName);
                    builder.append(" - ");
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error Parsing ISO-3166-2 String", e);
                }
            }
            String countriesListStr = builder.toString().trim();

            //Remove the last separator from the string
            viewHolder.countriesList.setText(
                    countriesListStr.substring(0, countriesListStr.length()-2).trim());
        }else {
            viewHolder.countriesList.setText("Available in more than 5 countries");
        }

        if(album.images != null && album.images.size() > 0){
            Picasso.with(context).load(album.images.get(0).url).into(viewHolder.thumbnail);
        }else {
            viewHolder.thumbnail.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.no_image));
        }

        return item;
    }

    static class ViewHolder {
        @Bind(R.id.txt_album_name)
        TextView albumName;
        @Bind(R.id.img_search_thumbnail)
        ImageView thumbnail;
        @Bind(R.id.txt_countries)
        TextView countriesList;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public List<AlbumSimple> getAlbumList(){
        return this.albumResults;
    }
}