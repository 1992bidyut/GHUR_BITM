package bdnath.lictproject.info.ghur.Location;


import android.Manifest;
import android.app.SearchManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import bdnath.lictproject.info.ghur.Geocode.CityNameResponse;
import bdnath.lictproject.info.ghur.Geocode.CityNameService;
import bdnath.lictproject.info.ghur.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment {

    private MapView mMapView;
    private View rootView;
    private GoogleMap googleMap;
    private CityNameService cityNameService;
    private FusedLocationProviderClient client;
    private LocationRequest request;
    private LocationCallback callback;
    public final String GEOCODE_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/";
    public final String key = "AIzaSyCy4IOHbNB0he6ASe5XqixIF4Fr0ezM8aI";

    public LocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_location, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        client = LocationServices.getFusedLocationProviderClient(getContext());
        request = new LocationRequest();
        getDeviceCurrentLocation();

        mMapView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
                googleMap.setMyLocationEnabled(true);
                LatLng start =new LatLng(23.7512507,90.365279);
                LatLng destination=new LatLng(23.7546849,90.3656922);
                Polyline polyline=googleMap.addPolyline(new PolylineOptions()
                        .add(start).add(destination).color(Color.GREEN));
                return false;
            }
        });


        return rootView;
    }
    ////////////////Search option
    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        inflater.inflate(R.menu.location_search, menu);

        SearchManager manager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search City");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(),query,Toast.LENGTH_SHORT).show();
                getLocationBySearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                break;
        }

        return true;
    }
    ///////////////////////////////////////
    private void getMapView(final double lat, final double lon){
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission
                        .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission
                        .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng myLocation = new LatLng(lat,lon);
                //googleMap.addMarker(new MarkerOptions().position(myLocation).title("Marker Title").snippet("Marker Description"));
                googleMap.addMarker(new MarkerOptions().position(myLocation));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                mMapView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        LatLng start =new LatLng(23.7512507,90.365279);
                        LatLng destination=new LatLng(23.7546849,90.3656922);
                        Polyline polyline=googleMap.addPolyline(new PolylineOptions()
                        .add(start).add(destination).color(Color.GREEN));
                        return false;
                    }
                });
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    //GEOCODE response
    public void getLocationBySearch(String address){

        //Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show();
        String customURL=String.format("json?address=%s&key=%s",address,key);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GEOCODE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        cityNameService= retrofit.create(CityNameService.class);

        Call<CityNameResponse> cityNameResponseCall =
                cityNameService.getCurrentWeather(customURL);

        cityNameResponseCall.enqueue(new Callback<CityNameResponse>() {
            @Override
            public void onResponse(Call<CityNameResponse> call, Response<CityNameResponse> response) {
                if(response.code() == 200) {
                    CityNameResponse cityNameResponse = response.body();
                    double latitude=cityNameResponse.getResults().get(0).getGeometry().getLocation().getLat();
                    double longitude=cityNameResponse.getResults().get(0).getGeometry().getLocation().getLng();
                    Toast.makeText(getContext(),latitude+";"
                            +longitude,Toast.LENGTH_SHORT).show();
                    getMapView(latitude,longitude);

                }
            }
            @Override
            public void onFailure(Call<CityNameResponse> call, Throwable t) {

            }
        });
    }
    ////////////////////////
    //Current Phone Location Provider Section

    public boolean checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},11);
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

    public void getDeviceCurrentLocation(){
        if(checkLocationPermission()){
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location == null){
                        return;
                    }
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Toast.makeText(getContext(),"Lat: "+latitude+" Long: "+longitude,Toast.LENGTH_LONG).show();
                    getMapView(latitude,longitude);
                }
            });
        }else{
            checkLocationPermission();
        }
    }

//////Location providing end
}