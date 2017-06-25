package smartbuilding.hackathon.com.smartbuildingproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by KSH on 2017-06-24.
 */

public class SensorListActivity extends Activity{
    private GrapeApplication mGrapeApplication;
    private ArrayList<Sensor> mSensorList;
    private ListView mSensorListView;
    private SensorListAdapter mSensorListAdapter;
    private LinearLayout mLayoutNoSensor;
    private LinearLayout mLayoutWithList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sensor_list);
        mGrapeApplication = GrapeApplication.getInstance();
        mGrapeApplication.setCurrentActivity(this);
        mSensorList = mGrapeApplication.getSensorList();

        mLayoutNoSensor = (LinearLayout)findViewById(R.id.layout_no_sensor);
        mSensorListView = (ListView)findViewById(R.id.layout_sensorlist);
        mLayoutWithList = (LinearLayout)findViewById(R.id.layout_with_list);

        mSensorListAdapter = new SensorListAdapter(this,mSensorList);
        mSensorListView.setAdapter(mSensorListAdapter);

        refreshView();
    }

    public void refreshView(){
        if(mSensorList.size() > 0){
            mLayoutNoSensor.setVisibility(View.GONE);
            mLayoutWithList.setVisibility(View.VISIBLE);

            mSensorListAdapter.notifyDataSetChanged();
        }else{
            mLayoutNoSensor.setVisibility(View.VISIBLE);
            mLayoutWithList.setVisibility(View.GONE);
        }
    }

    public void onSettings(View view){
        Intent intent = new Intent(SensorListActivity.this, SettingActivity.class);
        startActivity(intent);
    }
}
