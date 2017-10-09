package edu.sfsu.csc780.chathub;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by bees on 10/9/17.
 */

public class LocationUtils {
    private static final long MIN_TIME = 5 * 1000;
    private static final long MIN_DISTANCE = 5;
    private static final int REQUEST_CODE = 100;
    private static String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static int GRANTED= PackageManager.PERMISSION_GRANTED;
    private static final String[] LOCATION_PERMISSIONS =
            {FINE_LOCATION, COARSE_LOCATION};
    private static Location sLocation;
    private static LocationListener sLocationListener;

    public static double getLat(){
        return (sLocation != null) ? sLocation.getLatitude() : 0.0;
    }

    public static double getLong(){
        return (sLocation != null) ? sLocation.getLongitude() : 0.0;
    }

    public static void startLocationUpdates() {
        //Acquire a reference to the system Location Manager
        LocationManager locationManager =
                (LocationManager) Activity.this.getSystemService(Context.LOCATION_SERVICE);

        if (sLocationListener == null){
            //Define a listener that responds to location updates
            sLocationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "lat: " + location.getLatitude()
                    + "lon: " + location.getLongitude());
                    sLocation = location;
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                public void onProviderEnabled(String provider) {

                }

                public void onProviderDisabled(String provider) {

                }
            };
        }

        if (ActivityCompat.checkSelfPermission(Activity.this, FINE_LOCATION) !=
                GRANTED && ActivityCompat.checkSelfPermission(Activity.this,
                COARSE_LOCATION) != GRANTED) {{
            Log.d(TAG, "requesting permissions for starting");
            ActivityCompat.requestPermissions(Activity.this, LOCATION_PERMISSIONS, REQUEST_CODE);
            return;
            }
        Log.d(TAG, "requesting updates");
        }

        //Get updated Location

        Location location =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            Log.d(TAG, "last known lat: " + location.getLatitude()
            + "lon: " + location.getLongitude());
            sLocation = location;
        }

        Log.d(TAG, "requesting updates");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME,
                MIN_DISTANCE, sLocationListener);

    }
}
