package smartbuilding.hackathon.com.smartbuildingproject;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by KSH on 2017-06-24.
 */

public class GrapeApplication extends Application {
    // Mqtt related resources..
    private static final String mServerUri = "tcp://192.168.16.139";
    private MqttAndroidClient mMqttAndroidClient;
    private String mClientID = "";

    private static GrapeApplication mInstance;
    private ArrayList<Sensor> mSensorList;

    private String mLowTemperatureLimit = "1";
    private String mHighTemperatureLimit = "24";
    private String mLowHumidityLimit = "35";
    private String mHighHumidityLimit = "85";
    // They can be re-set in the settings

    private Activity mCurrentActivity;

    // SingleTon
    public static GrapeApplication getInstance(){
        return mInstance;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        mInstance = this;
        mSensorList = new ArrayList<>();

        mClientID = MqttClient.generateClientId() + System.currentTimeMillis();
        mMqttAndroidClient = new MqttAndroidClient(getApplicationContext(), mServerUri , mClientID);
        mMqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d(Constant.APP_TAG, "reconnect " + reconnect);
                if(reconnect){
                    if(Debug.DEBUG){
                        Log.d(Constant.APP_TAG, "connected " + serverURI);
                        subscribeToTopic(Constant.TOPIC);
                    }
                }else{
                    if(Debug.DEBUG){
                        Log.d(Constant.APP_TAG, "disconnected " + serverURI);
                    }
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                if(Debug.DEBUG){
                    Log.d(Constant.APP_TAG, "connectionLost");
                }
            }
            @Override
            public void messageArrived(String topic, MqttMessage message){
                Sensor sensor = getSensorFromMsg(message + "");

                if(Debug.DEBUG){
                    Log.d(Constant.APP_TAG, "msgArrived " + topic + " " + message);
                    Log.d(Constant.APP_TAG, "Sensor " + sensor.toString());
                }

                boolean isNewSensor = true;
                int updatedSensorIndex = 0;
                Iterator iterator = mSensorList.iterator();
                int index = 0;
                while (iterator.hasNext()) {
                    Object nextObj = iterator.next();
                    if(nextObj instanceof Sensor){
                        if(((Sensor) nextObj).getId().equals(sensor.getId())){
                            isNewSensor = false;
                            updatedSensorIndex = index;
                            break;
                        }
                    }
                    index++;
                }

                if(isNewSensor){
                    if(mCurrentActivity instanceof SensorListActivity){
                        Intent intent = new Intent(mCurrentActivity, AddNewSensorActivity.class);
                        if(Debug.DEBUG){
                            Log.d(Constant.APP_TAG, "temperature" + sensor.getTemperature() + " humidity " + sensor.getHumidity());
                        }

                        intent.putExtra(Constant.TAG_ID, sensor.getId());
                        intent.putExtra(Constant.TAG_TEMP , sensor.getTemperature());
                        intent.putExtra(Constant.TAG_HUM, sensor.getHumidity());
                        startActivity(intent);
                    }else{
                        // Other senser is been registering... wait..
                    }
                }else{
                    if(mCurrentActivity instanceof SensorListActivity){
                        checkTemperatureRange(sensor);
                        checkHumidityRange(sensor);

                        Sensor updatedSensor = mSensorList.get(updatedSensorIndex);
                        updatedSensor.setTemperature(sensor.getTemperature());
                        updatedSensor.setHumidity(sensor.getHumidity());
                        ((SensorListActivity)mCurrentActivity).refreshView();
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                if(Debug.DEBUG){
                    Log.d(Constant.APP_TAG, "deliveryComplete ");
                }
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try{
            mMqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mMqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    subscribeToTopic(Constant.TOPIC);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if(Debug.DEBUG){
                        Log.d(Constant.APP_TAG, "Failed to connect to " + mServerUri);
                    }
                }
            });
        }catch (MqttException ex){
            if(Debug.DEBUG){
                Log.e(Constant.APP_TAG, "MqttException " + ex.getMessage());
            }
        }

    }

    public void setCurrentActivity(Activity activity){
        mCurrentActivity = activity;
    }

    private Sensor getSensorFromMsg(String data){
        Sensor newSensor = new Sensor();
        String[] parsedData = data.split("[:]");
        // 0 - ID_12, 1 - T_18, 2 - H_30
        String[] idData = parsedData[0].split("[_]");
        String[] temData = parsedData[1].split("[_]");
        String[] humData = parsedData[2].split("[_]");

        newSensor.setId(idData[1]);
        newSensor.setTemperature(temData[1]);
        newSensor.setHumidity(humData[1]);
        if(Debug.DEBUG){
            Log.d(Constant.APP_TAG, "newSensor " + newSensor.toString());
        }

        return newSensor;
    }

    private boolean checkTemperatureRange(Sensor sensor){
        if(sensor.getTemperature().compareTo(getLowTemperatureLimit()) < 0 || sensor.getTemperature().compareTo(getHighTemperatureLimit()) > 0){
            // Warning..
            Toast.makeText(mCurrentActivity, getString(R.string.warning_temperature).replace("%%SS", sensor.getId())
                    .replace("%%LL", mLowTemperatureLimit)
                    .replace("%%HH", mHighTemperatureLimit), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkHumidityRange(Sensor sensor){
        if(sensor.getHumidity().compareTo(getLowHumidityLimit()) < 0 || sensor.getHumidity().compareTo(getHighHumidityLimit()) > 0){
            Toast.makeText(mCurrentActivity, getString(R.string.warning_humidity).replace("%%SS", sensor.getName())
                    .replace("%%LL", mLowHumidityLimit)
                    .replace("%%HH", mHighHumidityLimit), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void subscribeToTopic(String topic){
        final String gotTopic = topic;
        try{
            mMqttAndroidClient.subscribe(gotTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if(Debug.DEBUG){
                        Log.d(Constant.APP_TAG, "subscribed well " + gotTopic);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if(Debug.DEBUG){
                        Log.d(Constant.APP_TAG, "subscribing failed " + gotTopic);
                    }
                }
            });

        }catch (MqttException ex){
            if(Debug.DEBUG){
                Log.e(Constant.APP_TAG, "MqttException while subscribing. " + ex.getMessage());
            }
        }
    }

    public ArrayList<Sensor> getSensorList(){
        return mSensorList;
    }

    public void addNewSensor(Sensor sensor){
        mSensorList.add(sensor);

        if(mCurrentActivity instanceof AddNewSensorActivity){
            Intent intent = new Intent(mCurrentActivity, SensorListActivity.class);
            startActivity(intent);
        }
    }

    public String getLowTemperatureLimit() {
        return mLowTemperatureLimit;
    }

    public void setLowTemperatureLimit(String mLowTemperatureLimit) {
        this.mLowTemperatureLimit = mLowTemperatureLimit;
    }

    public String getHighTemperatureLimit() {
        return mHighTemperatureLimit;
    }

    public void setHighTemperatureLimit(String mHighTemperatureLimit) {
        this.mHighTemperatureLimit = mHighTemperatureLimit;
    }

    public String getLowHumidityLimit() {
        return mLowHumidityLimit;
    }

    public void setLowHumidityLimit(String mLowHumidityLimit) {
        this.mLowHumidityLimit = mLowHumidityLimit;
    }

    public String getHighHumidityLimit() {
        return mHighHumidityLimit;
    }

    public void setHighHumidityLimit(String mHighHumidityLimit) {
        this.mHighHumidityLimit = mHighHumidityLimit;
    }

    public void changedSettings(){
        for(int i=0; i<mSensorList.size(); i++){
            Sensor sensor = mSensorList.get(i);
            checkHumidityRange(sensor);
            checkTemperatureRange(sensor);
        }
    }
}
