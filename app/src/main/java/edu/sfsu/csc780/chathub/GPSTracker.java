package edu.sfsu.csc780.chathub;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.app.Dialog;

/**
 * Created by bees on 10/11/17.
 */

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    public static final int REQUEST_CODE = 100;

    //Flag for GPS status
    private boolean isGPSOn = false;

    //Flag for Network Status
    private boolean isNetworkOn = false;

    private boolean canGetLocation = false;

    private static Location location;
    private static double latitude;
    private static double longitude;

    //Minimum distance to change Updates in meters
    private static final long MIN_DISTANCE = 10;

    //Minimum time between updates in milliseconds
    private static final long MIN_TIME = 1000 * 60 * 1; //1 minute

    //Declaring a Location Manager
    protected static LocationManager locationManager;

    //Constructor
    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            //Get GPS Status
            isGPSOn = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            //Get Network Status
            isNetworkOn = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSOn && !isNetworkOn) {
                //No network or gps provider is on
            } else {
                this.canGetLocation = true;
                //First get location from Network Provider
                if (isNetworkOn) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME, MIN_DISTANCE, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.
                                NETWORK_PROVIDER);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }

                }

                // if GPS Enabled get lat/long using GPS services
                if (isGPSOn) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME,
                                MIN_DISTANCE,
                                this);
                        Log.d("GPS On", "GPS On");
                        if (locationManager != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    @Override
    public void onLocationChanged(Location location)
    {}

    @Override
    public void onProviderDisabled(String provider)
    {}

    @Override
    public void onProviderEnabled(String provider)
    {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {}

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    /*
        Functions to get latitude and longitude
     */

    public static double getLatitude(){

        if (location != null)
        {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public static double getLongitude(){
        if (location != null)
        {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    /*
        Function to check if best networkprovider
     */

    public boolean canGetLocation()
    {
        return this.canGetLocation;
    }

    /*
        Function to show settings alert dialog

     */

    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        //Settings Dialog Title
        alertDialog.setTitle("GPS is settings");

        //Settings Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        //On pressing Settings Button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        //Showing Alert Message
        alertDialog.show();
    }

    /*
        Stop using Listener
        Calling this function will stop using GPS in the app
     */
    public void stopUsingGPS()
    {
        if (locationManager != null)
        {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

}
