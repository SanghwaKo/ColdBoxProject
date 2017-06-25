package smartbuilding.hackathon.com.smartbuildingproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by KSH on 2017-06-24.
 */

public class SettingActivity extends Activity {
    private GrapeApplication mGrapeApplication;
    private EditText mTempLow, mTempHigh;
    private EditText mHumLow, mHumHigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_range);

        mTempLow = (EditText)findViewById(R.id.set_temp_low);
        mTempHigh = (EditText)findViewById(R.id.set_temp_high);
        mHumLow = (EditText)findViewById(R.id.set_hum_low);
        mHumHigh = (EditText)findViewById(R.id.set_hum_high);

        mGrapeApplication = GrapeApplication.getInstance();
        mGrapeApplication.setCurrentActivity(this);
    }

    public void onConfirm(View view){
        if(Debug.DEBUG){
            Log.d(Constant.APP_TAG, "lowTLimit : " + mTempLow.getText().toString() +
                    " hightTLimit : " + mTempHigh.getText().toString() +
                " lowHLimit : " + mHumLow.getText().toString()
                    + " highHLimit : " + mHumHigh.getText().toString());
        }

        mGrapeApplication.setLowTemperatureLimit(mTempLow.getText().toString());
        mGrapeApplication.setHighTemperatureLimit(mTempHigh.getText().toString());
        mGrapeApplication.setLowHumidityLimit(mHumLow.getText().toString());
        mGrapeApplication.setHighHumidityLimit(mHumHigh.getText().toString());

        mGrapeApplication.changedSettings();
        finish();
    }
}
