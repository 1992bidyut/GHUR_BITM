package bdnath.lictproject.info.ghur.Weather;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import bdnath.lictproject.info.ghur.R;
import bdnath.lictproject.info.ghur.Weather.WeatherProvider.ForecastLatLongResponse;
import bdnath.lictproject.info.ghur.Weather.WeatherProvider.ForecastLatLongService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {
    private View view;
    private GraphView TempGraph;
    private GraphView humidityGraph;
    private GraphView cloudGraph;
    private TextView scaleTV;
    private static final String CURRENT_BASE_URL="http://api.openweathermap.org/data/2.5/";

    private ForecastLatLongService latLongService;
    private String units = "metric"; //imperial --> farenheit

    public ForecastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_forecast, container, false);
        TempGraph=view.findViewById(R.id.TempGraph);
        humidityGraph=view.findViewById(R.id.humidityGraph);
        cloudGraph=view.findViewById(R.id.cloudGraph);
        scaleTV=view.findViewById(R.id.scaleTV);
        if(WeatherFragment.fahrenhite){
            scaleTV.setText("scaled in "+"\u00b0"+"F");
        }else {
            scaleTV.setText("scaled in "+"\u00b0"+"C");
        }

        getWeatherData(WeatherFragment.latitude,WeatherFragment.longitude);
        return view;
    }
    ///get Forecast Data
    public void getWeatherData(double latitude,double longitude){
        String apiKey=getString(R.string.weather_api_key);
        String customUrl=String.format("forecast?lat=%f&lon=%f&units=%s&cnt=10&appid=%s",latitude,longitude,units,apiKey);

        Retrofit retrofit=new Retrofit.Builder().baseUrl(CURRENT_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        latLongService= retrofit.create(ForecastLatLongService.class);

        Call<ForecastLatLongResponse> forecastLatLongResponseCall=latLongService.getForecastWeather(customUrl);

        forecastLatLongResponseCall.enqueue(new Callback<ForecastLatLongResponse>() {
            @Override
            public void onResponse(Call<ForecastLatLongResponse> call, Response<ForecastLatLongResponse> response) {
                if (response.code() == 200) {
                    ForecastLatLongResponse forcastWeatherResponse = response.body();
                    List<ForecastLatLongResponse.ListFor> lists= new ArrayList<>();

                    lists = forcastWeatherResponse.getList();

                    //Maximum Temperature
                    DataPoint[] dataPointsMax=new DataPoint[lists.size()];
                    for (int i=0 ; i < lists.size(); i++){
                        if (WeatherFragment.fahrenhite){
                            dataPointsMax[i]=new DataPoint(i,(lists.get(i).getMain().getTempMax()* 9/5 +32));
                        }else {
                            dataPointsMax[i]=new DataPoint(i,lists.get(i).getMain().getTempMax());
                        }
                    }
                    LineGraphSeries<DataPoint> seriesMax = new LineGraphSeries<DataPoint>(dataPointsMax);
                    TempGraph.addSeries(seriesMax);
                    seriesMax.setColor(Color.RED);
                    seriesMax.setDrawDataPoints(true);

                    //Minimum Temperature
                    DataPoint[] dataPointsMin=new DataPoint[lists.size()];
                    for (int i=0 ; i < lists.size(); i++){
                        if (WeatherFragment.fahrenhite){
                            dataPointsMin[i]=new DataPoint(i,(lists.get(i).getMain().getTempMin()* 9/5 +32));
                        }else {
                            dataPointsMin[i]=new DataPoint(i,lists.get(i).getMain().getTempMin());
                        }

                    }
                    LineGraphSeries<DataPoint> seriesMin = new LineGraphSeries<DataPoint>(dataPointsMin);
                    TempGraph.addSeries(seriesMin);
                    seriesMin.setDrawDataPoints(true);

                    //Humidity
                    DataPoint[] dataPointsHumi=new DataPoint[lists.size()];
                    for (int i=0 ; i < lists.size(); i++){
                        dataPointsHumi[i]=new DataPoint(i,lists.get(i).getMain().getHumidity());
                    }
                    LineGraphSeries<DataPoint> seriesHumi = new LineGraphSeries<DataPoint>(dataPointsHumi);
                    humidityGraph.addSeries(seriesHumi);
                    seriesHumi.setColor(Color.YELLOW);
                    seriesHumi.setDrawDataPoints(true);

                    //Cloudiness
                    DataPoint[] dataPointsCloud=new DataPoint[lists.size()];
                    for (int i=0 ; i < lists.size(); i++){
                        dataPointsCloud[i]=new DataPoint(i,lists.get(i).getClouds().getAll());
                    }
                    LineGraphSeries<DataPoint> seriesCloud = new LineGraphSeries<DataPoint>(dataPointsCloud);
                    cloudGraph.addSeries(seriesCloud);
                    seriesCloud.setColor(Color.WHITE);
                    seriesCloud.setDrawDataPoints(true);

                    Toast.makeText(getActivity(), "Forecast Value Retrived", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "Value Not Retrived", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<ForecastLatLongResponse> call, Throwable t) {
                Log.e("forecast", "onFailure: "+t.getMessage());
            }
        });
    }
}
