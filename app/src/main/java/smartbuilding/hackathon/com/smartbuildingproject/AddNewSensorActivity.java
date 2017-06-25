package smartbuilding.hackathon.com.smartbuildingproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class AddNewSensorActivity extends AppCompatActivity {
    private GrapeApplication mGrapeApplication;

    private LinearLayout mNewDetectedLayout;
    private EditText mEditName;

    private String mId = "";
    private String mTemperature = "0";
    private String mHumidity = "0";

    // Be started when a new sensor is detected..
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_sensor);

        mGrapeApplication = GrapeApplication.getInstance();
        mGrapeApplication.setCurrentActivity(this);

        mId = getIntent().getExtras().getString(Constant.TAG_ID);
        mTemperature = getIntent().getExtras().getString(Constant.TAG_TEMP);
        mHumidity = getIntent().getExtras().getString(Constant.TAG_HUM);

        mNewDetectedLayout = (LinearLayout)findViewById(R.id.layout_new_detected);
        mEditName = (EditText)findViewById(R.id.edit_newname);
    }

    public void onSetName(View view){
        mNewDetectedLayout.setVisibility(View.GONE);
    }

    public void onFinish(View view){
        Sensor sensor = new Sensor();

        if(mEditName.getText().toString().equals("")){
            sensor.setName(mId);
        }else{
            sensor.setName(mEditName.getText().toString());
        }

        sensor.setId(mId);
        sensor.setTemperature(mTemperature);
        sensor.setHumidity(mHumidity);
        mGrapeApplication.addNewSensor(sensor);
        // Go back to the ListActivity
    }
}
