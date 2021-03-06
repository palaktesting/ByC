package com.nap.bycab.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;
import com.nap.bycab.R;
import com.nap.bycab.activity.MainActivity;
import com.nap.bycab.fragment.HomeFragment;
import com.nap.bycab.models.CommonResponse;
import com.nap.bycab.models.Order;

import org.apache.commons.lang3.time.StopWatch;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Palak on 27-12-2015.
 */
public class LocationBackgroundService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Location prevLocation;
    private DistanceCalculator distanceCalculator;
    private double distance = 0;
    int notif_id = 16;
    private ServiceCallback serviceCallback;
//    private NotificationManager mNotifyManager;
//    private NotificationCompat.Builder mBuilder;
    protected GoogleApiClient mGoogleApiClient;
    protected Boolean mRequestingLocationUpdates;
    protected Location mCurrentLocation;
    protected LocationRequest mLocationRequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 20000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final IBinder mBinder = new MyBinder();
    private boolean recordDistance;
    private Chronometer etTimeVal;
    StopWatch timer = new StopWatch();

    public LocationBackgroundService() {

        Log.v(AppConstants.DEBUG_TAG, "SERVICE LocationBackgroundService");
        distanceCalculator = new DistanceCalculator();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.v(AppConstants.DEBUG_TAG, "SERVICE onCreate");
//location update
        mRequestingLocationUpdates = false;

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if(mGoogleApiClient.isConnected()) startUpdatesButtonHandler();
            }
        }.start();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v(AppConstants.DEBUG_TAG, "SERVICE onStartCommand");
        return START_STICKY;
    }

    public void canRecordDistance(boolean b) {
        recordDistance = b;
    }

    public long getTimerValues() {
        // like this...
        //TODO return time values with some logic...

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                Log.e("service calling","hi nirav");
//            }
//        }, 0, 1000);


        return  timer.getSplitTime();
    }

    public class MyBinder extends Binder {
        public LocationBackgroundService getService() {
            return LocationBackgroundService.this;
        }
    }
    //TODO enable bellow  in future
//    public void createNotification(long base){
//
//        Log.v(AppConstants.DEBUG_TAG, "SERVICE createNotification base:" + base);
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |   Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtra("isNotificationLocation", true);
//        intent.putExtra("notificationType", AppConstants.NOTIFICATION_TYPE_LOCATION_COUNTING);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 6, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        long timeDifference = 0;
//        timeDifference  = base - SystemClock.elapsedRealtime();
//
//       /* RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification_view);
//        Log.v(AppConstants.DEBUG_TAG,"noti chrono "+timeDifference + SystemClock.elapsedRealtime());
//        remoteViews.setChronometer(R.id.tvTimeLeft, timeDifference + SystemClock.elapsedRealtime(), null, true);
//        remoteViews.setTextViewText(R.id.title, " palak");*/
//
//
//        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mBuilder = new NotificationCompat.Builder(LocationBackgroundService.this);
//        mBuilder.setContentIntent(contentIntent)
//                .setContentTitle("ByKab is running..")
//                .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText("Your counter is ticking"))
//                .setContentText("Your counter is ticking")
//                .setSmallIcon(R.drawable.common_signin_btn_icon_disabled_dark)
//                .setUsesChronometer(true)
//                .setVibrate(new long[]{1000})
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//
//        timer.start();
//
//
//        startForeground(notif_id, mBuilder.getNotification());
//    }

    public void setCallback(ServiceCallback callbacks) {
        serviceCallback = callbacks;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //Location Update Code
    protected synchronized void buildGoogleApiClient() {
        Log.i(AppConstants.DEBUG_TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /** calculates the distance between two locations in meters */
    private float compareLocation(double lat1, double lng1, double lat2, double lng2) {

        Log.e(AppConstants.DEBUG_TAG,""+lat1+","+lng1+" "+lat2+","+lng2);

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        //Log.v(AppConstants.DEBUG_TAG,"compareLocation dist :"+dist);
        return dist; // output distance, in meters
    }

    @Override
    public void onLocationChanged(Location location) {

        if(prevLocation != null){
            BigDecimal num = new BigDecimal(compareLocation(prevLocation.getLatitude(),prevLocation.getLongitude(),location.getLatitude(),location.getLongitude()));
            String numWithNoExponents = num.toPlainString();

            Log.d(AppConstants.DEBUG_TAG, "meter numWithNoExponents " + numWithNoExponents);
//            Toast.makeText(this, "float value: "+num.floatValue(), Toast.LENGTH_SHORT).show();
            if(recordDistance && num.floatValue() > 100){
                distance = distance + distanceCalculator.distance(prevLocation.getLatitude(),prevLocation.getLongitude(), location.getLatitude(),location.getLongitude(),"K");
                Log.v(AppConstants.DEBUG_TAG,"onLocationChanged distance : "+ distance);
                prevLocation = location;
//                Toast.makeText(this, "dist: "+distance, Toast.LENGTH_SHORT).show();
                if(serviceCallback != null) serviceCallback.updateDistance(distance);
            }

            if(serviceCallback != null) serviceCallback.updatePath();
        }
        else{
            prevLocation = location;
        }

        mCurrentLocation = location;
        double latitude = mCurrentLocation.getLatitude();
        double longitude = mCurrentLocation.getLongitude();

        try {
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 15);
//            HomeFragment.map.animateCamera(cameraUpdate);
            HomeFragment.map.clear();

            try {
                Order order=PrefUtils.getRunningRide(getApplicationContext());

                if(order != null){
                    Log.e("marker info",Double.parseDouble(order.getLatitude())+" "+Double.parseDouble(order.getLongitude())+" "+order.getPickUpLocation());
                    HomeFragment.map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(order.getLatitude()), Double.parseDouble(order.getDLatitude()))).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_smarker)).snippet(order.getPickUpLocation()));
                    HomeFragment.map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(order.getLongitude()), Double.parseDouble(order.getDLongitude()))).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dmarker)).snippet(order.getDropLocation()));
                }
            }
            catch (Exception e){
                e.printStackTrace();
                Log.e(AppConstants.DEBUG_TAG,"error while adding marker: "+e+"");
            }
            HomeFragment.map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).snippet("Me"));
           // Toast.makeText(this, mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

            //TODO nkdroid
//            callLocationUpdate();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public Location getCurrentLocation(){
        return  mCurrentLocation;
    }

    public void callLocationUpdate() {

        JSONObject object=new JSONObject();
        try {
            object.put("Id", PrefUtils.getCurrentDriver(this).getDriverId()+"");
            object.put("Latitude",mCurrentLocation.getLatitude()+"");
            object.put("Longitude",mCurrentLocation.getLongitude()+"");

            Log.e("location update :",object+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();*/
        new PostServiceCall(AppConstants.UPDATE_LOCATION,object){

            @Override
            public void response(String response) {
                //progressDialog.dismiss();
                Log.e("login Response: ",response+"");
                CommonResponse commonResponse=new GsonBuilder().create().fromJson(response,CommonResponse.class);

                if(commonResponse.getResponseId().equalsIgnoreCase("0")){
//                    Toast.makeText(getApplicationContext(),""+commonResponse.getResponseMessage(),Toast.LENGTH_SHORT).show();
                    /*Snackbar snackbar=Snackbar.make(mDrawerLayout, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();*/

                }  else {
                    /*Snackbar snackbar=Snackbar.make(mDrawerLayout, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();*/
                }
            }

            @Override
            public void error(String error) {
//                Toast.makeText(getApplicationContext(),""+error.toString(),Toast.LENGTH_SHORT).show();
                //progressDialog.dismiss();
            }
        }.call();


    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(AppConstants.DEBUG_TAG, "Connection suspended");
        if(mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(AppConstants.DEBUG_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(AppConstants.DEBUG_TAG, "Connected to GoogleApiClient");
        if (mCurrentLocation != null) {
            try {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                //prevLocation = mCurrentLocation;
                final double latitude = mCurrentLocation.getLatitude();
                final double longitude = mCurrentLocation.getLongitude();
                new CountDownTimer(2000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        try {
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
                            HomeFragment.map.animateCamera(cameraUpdate);
                            HomeFragment.map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).snippet("Me"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }catch ( Exception e){
                e.printStackTrace();
            }
        }
//        Toast.makeText(this, mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    //testin..
    public void startUpdatesButtonHandler() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }


    public void stopUpdatesButtonHandler(View view) {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
    }


    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.v(AppConstants.DEBUG_TAG,"SERVICE onDestroy");

        mGoogleApiClient.disconnect();

        stopSelf();

    }
    //TODO enable bellow  in future
//    public void completeNotification(){
//
//        timer.stop();
//        //TODO set timer value to prefutils running ride... and delete running ride object once u reach fair activity..
//        Log.v(AppConstants.DEBUG_TAG,"SERVICE completeNotification");
//        stopForeground(true);
//        mBuilder.setAutoCancel(true);
//        mBuilder.setOngoing(false);
//        mNotifyManager.notify(notif_id, mBuilder.getNotification());
//        mNotifyManager.cancel(notif_id);
//
//    }
}
