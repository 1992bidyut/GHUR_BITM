package bdnath.lictproject.info.ghur.Location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class PhoneLocation extends AppCompatActivity{
    private FusedLocationProviderClient client;
    private LocationRequest request;
    private LocationCallback callback;
    public double latitude, longitude;
    public LatLng latLng;
    private Context context;


    public PhoneLocation(final Context context) {
        this.context=context;

        client = LocationServices.getFusedLocationProviderClient(context);
        request = new LocationRequest();
        callback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Toast.makeText(context,"Form  class Lat: "+latitude+" Long: "+longitude,Toast.LENGTH_LONG).show();

                }
            }
        };
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(5000);
        getDeviceCurrentLocation();
        getDeviceUpdateLocations();
    }

    private void getDeviceUpdateLocations() {
        if(checkLocationPermission()){
            client.requestLocationUpdates(request,callback,null);
        }else{
            checkLocationPermission();
        }
    }

    public boolean checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},11);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 11 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getDeviceCurrentLocation();
        }
    }

    public LatLng getDeviceCurrentLocation(){

        if(checkLocationPermission()){
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location == null){
                        return;
                    }
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    latLng=new LatLng(latitude,longitude);

                }
            });
        }else{
            checkLocationPermission();
        }
        return latLng;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
