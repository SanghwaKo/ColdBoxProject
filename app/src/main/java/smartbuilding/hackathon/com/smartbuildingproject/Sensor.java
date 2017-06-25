package smartbuilding.hackathon.com.smartbuildingproject;

/**
 * Created by KSH on 2017-06-24.
 */

public class Sensor {
    private String id = "";
    private String temperature = "0";
    private String humidity = "0";
    private String name = "";

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getId() {
        return id;

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString(){
        return "Name : " + name + " ID : " + id + " Temperature : " + temperature + " Humidity : " + humidity;
    }

    @Override
    public int hashCode(){
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sensor sensor = (Sensor) o;

        if (id != null ? !id.equals(sensor.id) : sensor.id != null) return false;
        if (temperature != null ? !temperature.equals(sensor.temperature) : sensor.temperature != null)
            return false;
        if (humidity != null ? !humidity.equals(sensor.humidity) : sensor.humidity != null)
            return false;
        return name != null ? name.equals(sensor.name) : sensor.name == null;

    }
}
