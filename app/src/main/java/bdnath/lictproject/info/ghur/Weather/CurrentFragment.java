package bdnath.lictproject.info.ghur.Weather;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import bdnath.lictproject.info.ghur.R;
import bdnath.lictproject.info.ghur.Weather.WeatherProvider.CurentWearherLatLongResponse;
import bdnath.lictproject.info.ghur.Weather.WeatherProvider.CurrentWeatherCity;
import bdnath.lictproject.info.ghur.Weather.WeatherProvider.CurrentWeatherCityService;
import bdnath.lictproject.info.ghur.Weather.WeatherProvider.CurrentWeatherLatLongService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentFragment extends Fragment {
    TextView cityTV,tempTV,mintempTV,conditionTV,maxtempTV,humidityTV,cloudTV,windTV,sunriseTV,sunsetTV;
    ImageView weatherLogo;
    private CurrentWeatherCityService currentWeatherCityService;

    public static final String CURRENT_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String PHOTO_URL="https://openweathermap.org/img/w/";
    private CurrentWeatherLatLongService latLongService;

    private String units = "metric"; //imperial --> farenheit
    View view;
///////////////////////////////////////////////
    public CurrentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_current, container, false);

        weatherLogo=view.findViewById(R.id.weatherLogo);
        tempTV=view.findViewById(R.id.tempTV);
        cityTV=view.findViewById(R.id.cityTV);
        mintempTV=view.findViewById(R.id.mintempTV);
        conditionTV=view.findViewById(R.id.conditionTV);
        maxtempTV=view.findViewById(R.id.maxtempTV);
        humidityTV=view.findViewById(R.id.humidityTV);
        cloudTV=view.findViewById(R.id.cloudTV);
        windTV=view.findViewById(R.id.windTV);
        sunriseTV=view.findViewById(R.id.sunriseTV);
        sunsetTV=view.findViewById(R.id.sunsetTV);
        if (WeatherFragment.address!=null){
            getSearchWeatherData(WeatherFragment.address);
        }else {
            getWeatherData(WeatherFragment.latitude,WeatherFragment.longitude);
        }


        return view;
    }

    ///get weather data to view
    private void getWeatherData(double latitude,double longitude) {
        String apiKey = getString(R.string.weather_api_key);
        // Toast.makeText(getContext(),latitude+" degree "+longitude+" degree",Toast.LENGTH_SHORT).show();
        String customUrl = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s",latitude,longitude,units,apiKey);
        //String customUrl = String.format("weather?q=Dhaka&units=%s&appid=%s",units,apiKey);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CURRENT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        latLongService= retrofit.create(CurrentWeatherLatLongService.class);

        Call<CurentWearherLatLongResponse> currentWeatherResponseCall =
                latLongService.getCurrentWeather(customUrl);

        currentWeatherResponseCall.enqueue(new Callback<CurentWearherLatLongResponse>() {
            @Override
            public void onResponse(Call<CurentWearherLatLongResponse> call, Response<CurentWearherLatLongResponse> response) {
                if(response.code() == 200){
                    //Toast.makeText(getContext(),"OK at API calling",Toast.LENGTH_SHORT).show();
                    CurentWearherLatLongResponse currentWeatherResponse =
                            response.body();
                    String logo= PHOTO_URL+currentWeatherResponse.getWeather()
                            .get(0).getIcon()+".png";
                    Picasso.get().load(Uri.parse(logo)).into(weatherLogo);
                    cityTV.setText(currentWeatherResponse.getName());
                    sunriseTV.setText(unixToTime(currentWeatherResponse.getSys().getSunrise()));
                    sunsetTV.setText(unixToTime(currentWeatherResponse.getSys().getSunset()));
                    if (WeatherFragment.fahrenhite){
                        tempTV.setText((float)(currentWeatherResponse.getMain().getTemp()* 9/5) +32+"\u00b0"+"F");
                        mintempTV.setText((float)(currentWeatherResponse.getMain().getTempMin()* 9/5) +32+"\u00b0"+"F");
                        maxtempTV.setText((float)(currentWeatherResponse.getMain().getTempMax()* 9/5) +32+"\u00b0"+"F");
                    }else {
                        tempTV.setText((float)currentWeatherResponse.getMain().getTemp()+"\u00b0"+"C");
                        mintempTV.setText((float)currentWeatherResponse.getMain().getTempMin()+"\u00b0"+"C");
                        maxtempTV.setText((float)currentWeatherResponse.getMain().getTempMax()+"\u00b0"+"C");
                    }
                    conditionTV.setText(currentWeatherResponse.getWeather().get(0).getDescription());
                    humidityTV.setText(currentWeatherResponse.getMain().getHumidity()+"%");
                    cloudTV.setText(currentWeatherResponse.getClouds().getAll()+"%");
                    double wind=currentWeatherResponse.getWind().getSpeed();
                    float res= (float) (wind*(3600/1000));
                    windTV.setText(res+"km/h");
                }else{
                    Toast.makeText(getContext(),"Some problem at API calling",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CurentWearherLatLongResponse> call, Throwable t) {

            }
        });
    }

    private String  unixToTime(long unixSeconds){
        Date date = new java.util.Date(unixSeconds*1000);
        //SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh.mm aa");
        // sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+6"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
    private void getSearchWeatherData(String city) {
        String apiKey=getString(R.string.weather_api_key);
        String customUrl=String.format("weather?q=%s&appid=%s",city,apiKey);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(CURRENT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        currentWeatherCityService= retrofit.create(CurrentWeatherCityService.class);
        Call<CurrentWeatherCity> currentWeatherCityCall=
                currentWeatherCityService.getCurrentWeatherCity(customUrl);

        currentWeatherCityCall.enqueue(new Callback<CurrentWeatherCity>() {
            @Override
            public void onResponse(Call<CurrentWeatherCity> call, Response<CurrentWeatherCity> response) {
                try {
                    if (response.code() == 200) {
                        CurrentWeatherCity currentWeatherResponse = response.body();
                        /*String logo= PHOTO_URL+currentWeatherResponse.getWeather()
                                .get(0).getIcon()+".png";
                        Picasso.get().load(Uri.parse(logo)).into(weatherLogo);*/
                        cityTV.setText(currentWeatherResponse.getName());
                        sunriseTV.setText(unixToTime(currentWeatherResponse.getSys().getSunrise()));
                        sunsetTV.setText(unixToTime(currentWeatherResponse.getSys().getSunset()));
                        if (WeatherFragment.fahrenhite){
                            tempTV.setText((float)((currentWeatherResponse.getMain().getTemp()-273.16)* 9/5) +32+"\u00b0"+"F");
                            mintempTV.setText((float)((currentWeatherResponse.getMain().getTempMin()-273.16)* 9/5) +32+"\u00b0"+"F");
                            maxtempTV.setText((float)((currentWeatherResponse.getMain().getTempMax()-273.16)* 9/5) +32+"\u00b0"+"F");
                        }else {
                            tempTV.setText((float)(currentWeatherResponse.getMain().getTemp()-273.16)+"\u00b0"+"C");
                            mintempTV.setText((float)(currentWeatherResponse.getMain().getTempMin()-273.16)+"\u00b0"+"C");
                            maxtempTV.setText((float)(currentWeatherResponse.getMain().getTempMax()-273.16)+"\u00b0"+"C");
                        }
                        conditionTV.setText(currentWeatherResponse.getWeather().get(0).getDescription());
                        humidityTV.setText(currentWeatherResponse.getMain().getHumidity()+"%");
                        cloudTV.setText(currentWeatherResponse.getClouds().getAll()+"%");
                        double wind=currentWeatherResponse.getWind().getSpeed();
                        float res= (float) (wind*(3600/1000));
                        windTV.setText(res+"km/h");
                        ///////////////////////////////////
                        /*temTv.setText(String.valueOf(currentWeatherResponse.getMain().getTemp()));
                        cityTv.setText(currentWeatherResponse.getName());
                        minTv.setText(String.valueOf(currentWeatherResponse.getMain().getTempMin()));
                        maxTv.setText(String.valueOf(currentWeatherResponse.getMain().getTempMax()));
                        huminityTv.setText(String.valueOf(currentWeatherResponse.getMain().getHumidity()) + "%");
                        pressureTv.setText(String.valueOf(currentWeatherResponse.getMain().getPressure()) + " hPa");
                        //date
                        long datetime = currentWeatherResponse.getDt();
                        Date date = new Date(datetime*1000);
                        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                        String dateText = df2.format(date);
                        dateTv.setText(dateText);

                        //time
                        long millisecond=currentWeatherResponse.getSys().getSunrise();
                        String dateString = DateFormat.format("HH:mm aa", new Date(millisecond*1000)).toString();
                        sunriseTv.setText(dateString);

                        long mil=currentWeatherResponse.getSys().getSunset();
                        String dateSun = DateFormat.format("HH:mm aa", new Date(mil*1000)).toString();
                        sunsetTv.setText(dateSun);*/

                        //Image

                        //String photo = currentWeatherResponse.getWeather().;
                        try {
                            String logo= PHOTO_URL+currentWeatherResponse.getWeather()
                                    .get(0).getIcon()+".png";
                            Picasso.get().load(Uri.parse(logo)).into(weatherLogo);
                        }
                        catch(Exception e){

                        }

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<CurrentWeatherCity> call, Throwable t) {

                Log.e("error",t.getMessage());
            }
        });
    }

}
