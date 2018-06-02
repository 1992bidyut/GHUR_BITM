package bdnath.lictproject.info.ghur.Weather;


import android.Manifest;
import android.app.SearchManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import bdnath.lictproject.info.ghur.R;

import static android.content.Context.SEARCH_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FusedLocationProviderClient client;
    private LocationRequest request;
    private LocationCallback callback;
    public static double latitude;
    public static double longitude;
    public static String address=null;
    public static boolean fahrenhite=false;



    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view=inflater.inflate(R.layout.fragment_weather, container, false);
        tabLayout=view.findViewById(R.id.tabLayout);
        viewPager=view.findViewById(R.id.tabViewPage);

        client = LocationServices.getFusedLocationProviderClient(getContext());
        request = new LocationRequest();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);

        SearchManager manager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search City");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(),query,Toast.LENGTH_SHORT).show();
                address=query;
                setViewpager();
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
            case R.id.clw:
                address=null;
                getDeviceCurrentLocation();
                setViewpager();
                break;
            case R.id.fahrenhite:
                fahrenhite=true;
                setViewpager();
                break;
            case R.id.celsius:
                fahrenhite=false;
                setViewpager();
            case R.id.search:
                break;
        }

        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDeviceCurrentLocation();
        setViewpager();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setViewpager(){
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.addFragment(new CurrentFragment(), "Current");
        viewPagerAdapter.addFragment(new ForecastFragment(),"Forecast");

        viewPager.setAdapter(viewPagerAdapter);
    }
    private class ViewPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }

        public void addFragment(Fragment fragment, String name) {
            fragmentList.add(fragment);
            fragmentTitles.add(name);
        }
    }

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
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Toast.makeText(getContext(),"Lat: "+latitude+" Long: "+longitude,Toast.LENGTH_LONG).show();
                    setViewpager();
                    //view control point
                }
            });
        }else{
            checkLocationPermission();
        }
    }

//////Location providing end
}
