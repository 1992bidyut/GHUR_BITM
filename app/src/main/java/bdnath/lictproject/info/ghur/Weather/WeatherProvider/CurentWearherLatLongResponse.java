package bdnath.lictproject.info.ghur.Weather.WeatherProvider;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurentWearherLatLongResponse {
    @SerializedName("coord")
    @Expose
    private CurentWearherLatLongResponse.Coord coord;
    @SerializedName("weather")
    @Expose
    private List<CurentWearherLatLongResponse.Weather> weather = null;
    @SerializedName("base")
    @Expose
    private String base;
    @SerializedName("main")
    @Expose
    private CurentWearherLatLongResponse.Main main;
    @SerializedName("visibility")
    @Expose
    private Integer visibility;
    @SerializedName("wind")
    @Expose
    private CurentWearherLatLongResponse.Wind wind;
    @SerializedName("clouds")
    @Expose
    private CurentWearherLatLongResponse.Clouds clouds;
    @SerializedName("dt")
    @Expose
    private Integer dt;
    @SerializedName("sys")
    @Expose
    private CurentWearherLatLongResponse.Sys sys;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("cod")
    @Expose
    private Integer cod;

    public CurentWearherLatLongResponse.Coord getCoord() {
        return coord;
    }

    public void setCoord(CurentWearherLatLongResponse.Coord coord) {
        this.coord = coord;
    }

    public List<CurentWearherLatLongResponse.Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<CurentWearherLatLongResponse.Weather> weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public CurentWearherLatLongResponse.Main getMain() {
        return main;
    }

    public void setMain(CurentWearherLatLongResponse.Main main) {
        this.main = main;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public CurentWearherLatLongResponse.Wind getWind() {
        return wind;
    }

    public void setWind(CurentWearherLatLongResponse.Wind wind) {
        this.wind = wind;
    }

    public CurentWearherLatLongResponse.Clouds getClouds() {
        return clouds;
    }

    public void setClouds(CurentWearherLatLongResponse.Clouds clouds) {
        this.clouds = clouds;
    }

    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public CurentWearherLatLongResponse.Sys getSys() {
        return sys;
    }

    public void setSys(CurentWearherLatLongResponse.Sys sys) {
        this.sys = sys;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public class Clouds {

        @SerializedName("all")
        @Expose
        private Integer all;

        public Integer getAll() {
            return all;
        }

        public void setAll(Integer all) {
            this.all = all;
        }

    }

    public class Coord {

        @SerializedName("lon")
        @Expose
        private Double lon;
        @SerializedName("lat")
        @Expose
        private Double lat;

        public Double getLon() {
            return lon;
        }

        public void setLon(Double lon) {
            this.lon = lon;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

    }

    public class Main {

        @SerializedName("temp")
        @Expose
        private Integer temp;
        @SerializedName("pressure")
        @Expose
        private Integer pressure;
        @SerializedName("humidity")
        @Expose
        private Integer humidity;
        @SerializedName("temp_min")
        @Expose
        private Integer tempMin;
        @SerializedName("temp_max")
        @Expose
        private Integer tempMax;

        public Integer getTemp() {
            return temp;
        }

        public void setTemp(Integer temp) {
            this.temp = temp;
        }

        public Integer getPressure() {
            return pressure;
        }

        public void setPressure(Integer pressure) {
            this.pressure = pressure;
        }

        public Integer getHumidity() {
            return humidity;
        }

        public void setHumidity(Integer humidity) {
            this.humidity = humidity;
        }

        public Integer getTempMin() {
            return tempMin;
        }

        public void setTempMin(Integer tempMin) {
            this.tempMin = tempMin;
        }

        public Integer getTempMax() {
            return tempMax;
        }

        public void setTempMax(Integer tempMax) {
            this.tempMax = tempMax;
        }

    }

    public class Sys {

        @SerializedName("type")
        @Expose
        private Integer type;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("message")
        @Expose
        private Double message;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("sunrise")
        @Expose
        private Integer sunrise;
        @SerializedName("sunset")
        @Expose
        private Integer sunset;

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Double getMessage() {
            return message;
        }

        public void setMessage(Double message) {
            this.message = message;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Integer getSunrise() {
            return sunrise;
        }

        public void setSunrise(Integer sunrise) {
            this.sunrise = sunrise;
        }

        public Integer getSunset() {
            return sunset;
        }

        public void setSunset(Integer sunset) {
            this.sunset = sunset;
        }

    }

    public class Weather {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("main")
        @Expose
        private String main;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("icon")
        @Expose
        private String icon;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

    }

    public class Wind {

        @SerializedName("speed")
        @Expose
        private Double speed;
        @SerializedName("deg")
        @Expose
        private Integer deg;

        public Double getSpeed() {
            return speed;
        }

        public void setSpeed(Double speed) {
            this.speed = speed;
        }

        public Integer getDeg() {
            return deg;
        }

        public void setDeg(Integer deg) {
            this.deg = deg;
        }

    }
}
