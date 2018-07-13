package bdnath.lictproject.info.ghur.Location;


import android.Manifest;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import bdnath.lictproject.info.ghur.Geocode.CityNameResponse;
import bdnath.lictproject.info.ghur.Geocode.CityNameService;
import bdnath.lictproject.info.ghur.MainActivity;
import bdnath.lictproject.info.ghur.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment {

    private MapView mMapView;
    private View rootView;
    private GoogleMap map;
    private GoogleMapOptions mapOptions;
    private ClusterManager<MarkerItem> clusterManager;
    private GeoDataClient dataClient;
    private PlaceDetectionClient placeDetectionClient;
    private LatLng startLatLng;
    private LatLng endLatLng;
    private LatLng currentLatLng;

    private CityNameService cityNameService;
    private FusedLocationProviderClient client;
    private LocationRequest request;
    private LocationCallback callback;

    public final String GEOCODE_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/";
    public final String key = "AIzaSyCy4IOHbNB0he6ASe5XqixIF4Fr0ezM8aI";

    private Button instructionBTN;
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    private DirectionService service;
    private String[] instruction;

    public LocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_location, container, false);
        instructionBTN=rootView.findViewById(R.id.inst_btn);
        instructionBTN.setVisibility(View.GONE);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        dataClient = Places.getGeoDataClient(getContext());
        placeDetectionClient = Places.getPlaceDetectionClient(getContext());

        client = LocationServices.getFusedLocationProviderClient(getContext());
        request = new LocationRequest();
        getDeviceCurrentLocation();
        instructionBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (instruction!=null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setItems(instruction,null);
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            instructionBTN.setVisibility(View.GONE);
                        }
                    });
                    builder.show();
                }else {
                    Toast.makeText(getContext(),"No Instruction",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }
    private void getDirection(){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service=retrofit.create(DirectionService.class);
        String key=getString(R.string.direction_api_key);

        String slat=String.valueOf(currentLatLng.latitude);
        String slng=String.valueOf(currentLatLng.longitude);
        String elat=String.valueOf(endLatLng.latitude);
        String elng=String.valueOf(endLatLng.longitude);
        Toast.makeText(getContext(),"lat:"+endLatLng.latitude,Toast.LENGTH_SHORT).show();
        //String urlString = String.format("json?origin=23.750854,90.393527&destination=23.8066861,90.3642745&key=%s",key);
        String urlString = String.format("json?origin=%s,%s&destination=%s,%s&alternatives=true&key=%s",slat,slng,elat,elng,key);
        Call<DirectionResponse>call=service.getDirections(urlString);
        call.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                if(response.code()==200){
                    DirectionResponse directionResponse=response.body();
                    List<DirectionResponse.Route>routes=directionResponse.getRoutes();
                    for (int j=0;j<routes.size();j++){
                        List<DirectionResponse.Step>steps=
                                directionResponse.getRoutes().get(j)
                                        .getLegs().get(0).getSteps();
                        instruction=new String[steps.size()];
                        for (int i =0;i<steps.size();i++){
                            double startLat=steps.get(i).getStartLocation().getLat();
                            double startLon=steps.get(i).getStartLocation().getLng();
                            instruction[i]=String.valueOf(Html.fromHtml(steps.get(i).getHtmlInstructions()));
                            LatLng start=new LatLng(startLat,startLon);

                            double endLat=steps.get(i).getEndLocation().getLat();
                            double endLon=steps.get(i).getEndLocation().getLng();
                            LatLng end=new LatLng(endLat,endLon);
                            Polyline polyline=map.addPolyline(new PolylineOptions()
                                    .add(start).add(end)
                                    .color(Color.RED));
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {

            }
        });
    }
    ////////////////Search option
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.location_search, menu);

        SearchManager manager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search City");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
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
            case R.id.direction:
                getDirection();
                instructionBTN.setVisibility(View.VISIBLE);
                break;
            case R.id.search:
                break;
            case R.id.current_place:
                showCurrentPlace();
                break;
            case R.id.place_picker:
                showPickerDialog();
                break;
        }

        return true;
    }

    private void showPickerDialog() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            getActivity().startActivityForResult(builder.build(getActivity()),505);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==505&&resultCode==RESULT_OK){
            Place place = PlacePicker.getPlace(getContext(),data);
            Toast.makeText(getContext(),place.getName()
                    +String.valueOf(place.getLatLng().longitude)
                            +String.valueOf(place.getLatLng().latitude)
                    ,Toast.LENGTH_SHORT).show();
        }
    }
    private void showCurrentPlace() {
        if (map == null) {
            return;
        }
        if (checkLocationPermission()){
            placeDetectionClient.getCurrentPlace(null).addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    if (task.isSuccessful()&&task.getResult()!=null){
                        List<MarkerItem>items=new ArrayList<>();
                        PlaceLikelihoodBufferResponse response=task.getResult();
                        int count=response.getCount();
                        String[]placesNam=new String[count];
                        LatLng[]latLngs=new LatLng[count];
                        String[]address=new String[count];
                        for (int i=0;i<count;i++){
                            PlaceLikelihood placeLikelihood=response.get(i);
                            placesNam[i]= (String) placeLikelihood.getPlace().getName();
                            latLngs[i]=placeLikelihood.getPlace().getLatLng();
                            address[i]= (String) placeLikelihood.getPlace().getAddress();
                            MarkerItem item=new MarkerItem(latLngs[i],placesNam[i]);
                            items.add(item);
                        }
                        clusterManager.addItems(items);
                        clusterManager.cluster();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
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
                map = mMap;
                final List<MarkerItem> items=new ArrayList<>();
                clusterManager=new ClusterManager<MarkerItem>(getContext(),map);
                map.setOnCameraIdleListener(clusterManager);
                map.setOnMarkerClickListener(clusterManager);
                // For dropping a marker at a point on the Map
                LatLng myLocation = new LatLng(lat,lon);
                //map.addMarker(new MarkerOptions().position(myLocation).title("Marker Title").snippet("Marker Description"));
                map.addMarker(new MarkerOptions().position(myLocation));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(12).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //currentLatLng=new LatLng(cameraPosition.target.latitude,cameraPosition.target.longitude);
                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission
                        .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission
                        .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);
                map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        /*MarkerItem markerItem=new MarkerItem(latLng);
                        items.add(markerItem);
                        //map.addMarker(new MarkerOptions().position(latLng));
                        clusterManager.addItem(markerItem);*/

                        Marker marker=map.addMarker(new MarkerOptions().title("Custom").position(latLng));
                        endLatLng=marker.getPosition();
                        //Toast.makeText(getContext(),"lat:"+endLatLng.latitude,Toast.LENGTH_SHORT).show();

                       /* LatLng start =new LatLng(23.7512507,90.365279);
                        LatLng destination=new LatLng(23.7546849,90.3656922);
                        Polyline polyline=map.addPolyline(new PolylineOptions()
                                .add(start).add(destination).color(Color.GREEN));
*/
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
                    endLatLng=new LatLng(latitude,longitude);
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
    //////////////////////////

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
                    currentLatLng=new LatLng(latitude,longitude);
                    getMapView(latitude,longitude);
                }
            });
        }else{
            checkLocationPermission();
        }
    }

//////Location providing end
}