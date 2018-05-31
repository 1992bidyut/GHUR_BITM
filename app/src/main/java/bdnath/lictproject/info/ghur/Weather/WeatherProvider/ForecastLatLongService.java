package bdnath.lictproject.info.ghur.Weather.WeatherProvider;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ForecastLatLongService {
    @GET()
    Call<ForecastLatLongResponse> getForecastWeather(@Url String urlString);
}
