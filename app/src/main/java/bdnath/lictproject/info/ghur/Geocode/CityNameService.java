package bdnath.lictproject.info.ghur.Geocode;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface CityNameService {
    @GET()
    Call<CityNameResponse> getCurrentWeather(@Url String urlString);
}
