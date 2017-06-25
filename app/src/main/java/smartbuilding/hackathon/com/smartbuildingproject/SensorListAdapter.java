package smartbuilding.hackathon.com.smartbuildingproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by KSH on 2017-06-24.
 */

public class SensorListAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<Sensor> sensorList;

    public SensorListAdapter(Context context, ArrayList<Sensor> sensorList){
        this.context = context;
        this.sensorList = sensorList;
    }
    @Override
    public int getCount() {
        return sensorList.size();
    }

    @Override
    public Object getItem(int position) {
        return sensorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SensorRowView sensorRowView;
        Sensor sensor = sensorList.get(position);

        if(convertView == null){
            sensorRowView = new SensorRowView(context);
        }else{
            sensorRowView = (SensorRowView)convertView;
        }

        sensorRowView.setSensorName(sensor.getName());
        sensorRowView.setSensorTemperature(sensor.getTemperature());
        sensorRowView.setSensorHumididy(sensor.getHumidity());

        return sensorRowView;
    }

    class SensorRowView extends LinearLayout{
        private Context context;

        private TextView sensorName;
        private TextView sensorTemperature;
        private TextView sensorHumidity;

        public SensorRowView(Context context){
            super(context);

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.sensor_row, this, true);

            sensorName = (TextView)findViewById(R.id.sensor_name);
            sensorTemperature = (TextView)findViewById(R.id.sensor_temperature);
            sensorHumidity = (TextView)findViewById(R.id.sensor_humidity);
        }

        public void setSensorName(String name){
            sensorName.setText(name);
        }

        public void setSensorTemperature(String temperature){
            sensorTemperature.setText(context.getString(R.string.temperature).replace("%%TT", temperature));
        }

        public void setSensorHumididy(String humidity){
            sensorHumidity.setText(context.getString(R.string.humidity).replace("%%HH", humidity));
        }
    }
}
