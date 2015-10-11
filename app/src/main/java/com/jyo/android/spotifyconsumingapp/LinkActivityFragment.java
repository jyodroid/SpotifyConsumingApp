package com.jyo.android.spotifyconsumingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.jyo.android.spotifyconsumingapp.commons.InternetUtils;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class LinkActivityFragment extends Fragment {

    public LinkActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Intent intent = getActivity().getIntent();

        final View rootView = inflater.inflate(R.layout.fragment_link, container, false);
        ViewHolder viewHolder = new ViewHolder(rootView);
//        viewHolder.albumLink.setText(intent.getStringExtra(getString(R.string.album_link)));

        if(null != intent.getStringExtra(getString(R.string.album_image_url))){
            Picasso.with(getActivity().getBaseContext())
                    .load(intent.getStringExtra(getString(R.string.album_image_url)))
                    .into(viewHolder.albumImage);
        }else {
            viewHolder.albumImage.setImageDrawable(
                    ContextCompat.getDrawable(getActivity().getBaseContext(), R.drawable.no_image));

        }

        viewHolder.buttonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!InternetUtils.isInternetAvailable(getActivity().getBaseContext())){
                    Snackbar.make(rootView, "Not internet available!", Snackbar.LENGTH_LONG).show();

                }else {
                    Uri linkUri = Uri.parse(
                            intent.getStringExtra(getString(R.string.album_link)))
                            .buildUpon().build();
                    Intent intentIntent = new Intent(Intent.ACTION_VIEW, linkUri);
                    getActivity().startActivity(intentIntent);
                }
            }
        });

        return rootView;
    }

    static class ViewHolder {
        @Bind(R.id.img_album_image)
        ImageView albumImage;
        @Bind(R.id.btn_link)
        Button buttonLink;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
