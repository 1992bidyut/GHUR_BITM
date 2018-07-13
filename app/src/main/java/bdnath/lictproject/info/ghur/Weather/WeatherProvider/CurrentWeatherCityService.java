package bdnath.lictproject.info.ghur.Weather.WeatherProvider;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface CurrentWeatherCityService {
    @GET()
    Call<CurrentWeatherCity> getCurrentWeatherCity(@Url String urlString);
}
