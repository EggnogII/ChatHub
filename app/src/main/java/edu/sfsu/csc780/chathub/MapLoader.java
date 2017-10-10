package edu.sfsu.csc780.chathub;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by bees on 10/9/17.
 */

public class MapLoader extends AsyncTaskLoader<Bitmap> {

    //Constructor


    public MapLoader(Context context) {
        super(context);
    }

    @Override
    public Bitmap loadInBackground() {
        double lat = LocationUtils.getLat();
        double lon = LocationUtils.getLon();

        StringBuilder urlBuilder= new StringBuilder("https//maps.google" +
            ".com/maps/api/staticmap?center=");
        urlBuilder.append(lat);
        urlBuilder.append(",");
        urlBuilder.append(lon);
        urlBuilder.append("&zoom=15&size=400x300");
        urlBuilder.append("&markers=color:blue%7Clabel:A%7C");
        urlBuilder.append(lat);
        urlBuilder.append(",");
        urlBuilder.append(lon);
        Log.d(TAG, "map url:" + urlBuilder.toString());

        Bitmap bmp = null;
        HttpURLConnection urlConnection= null;
        try{
            URL url= new URL(urlBuilder.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            bmp = BitmapFactory.decodeStream(in);
            in.close();
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return bmp;
    }
}
