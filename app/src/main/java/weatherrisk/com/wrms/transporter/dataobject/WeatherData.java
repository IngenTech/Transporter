package weatherrisk.com.wrms.transporter.dataobject;

/**
 * Created by WRMS on 21-03-2016.
 */
public class WeatherData {
    private String date;
    private String time;
    private String geoCoord;
    private String temperature;
    private String windSpeed;
    private String windDirection;
    private String rainfall;
    private String humidity;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGeoCoord() {
        return geoCoord;
    }

    public void setGeoCoord(String geoCoord) {
        this.geoCoord = geoCoord;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getRainfall() {
        return rainfall;
    }

    public void setRainfall(String rainfall) {
        this.rainfall = rainfall;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
