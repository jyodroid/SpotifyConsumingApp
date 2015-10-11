package com.jyo.android.spotifyconsumingapp.commons;

import android.content.Context;

import com.jyo.android.spotifyconsumingapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JohnTangarife on 10/10/15.
 */
public class CountryFinder {

    public static String findCountryByCode(String code, Context context) throws JSONException {

        final String UNKNOWN_COUNTRY = "Unknown country";
        final String NAME = "Name";
        final String CODE = "Code";

        //Bring the Countries Json Codes
        String countryCodes = context.getString(R.string.iso_3166_2_codes);

        //Working with a Json Object
        JSONArray countryCodesJson = new JSONArray(countryCodes);
        for (int index = 0; index < countryCodesJson.length(); index++){
            JSONObject country = countryCodesJson.getJSONObject(index);
            if (country.getString(CODE).equals(code)){
                return country.getString(NAME);
            }
        }

        return UNKNOWN_COUNTRY;
    }
}
