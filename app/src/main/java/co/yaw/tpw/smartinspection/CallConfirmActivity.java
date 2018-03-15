package co.yaw.tpw.smartinspection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yaw.tpw.smartinspection.R;

public class CallConfirmActivity extends AppCompatActivity {

    final Context context = this;
    private Button menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_confirm);

        final Bundle b = getIntent().getExtras();
        if(b != null) {
            String forward = b.getString("forward");
            if(forward != null && forward.equals("afterCrew")) {
                View healthStatusLayout = findViewById(R.id.health_status_layout);
                View healthStatusSeparator = findViewById(R.id.health_status_separator);
                View heartbeatLayout = findViewById(R.id.heartbeat_layout);
                View heartbeatSeparator= findViewById(R.id.heartbeat_separator);
                View maxBloodPressureLayout = findViewById(R.id.max_blood_pressure_layout);
                View maxBloodPressureSeparator = findViewById(R.id.max_blood_pressure_separator);
                View minBloodPressureLayout = findViewById(R.id.min_blood_pressure_layout);
                View minBloodPressureSeparator = findViewById(R.id.min_blood_pressure_separator);
                View bodyTemperatureLayout = findViewById(R.id.body_temperature_layout);
                View bodyTemperatureSeparator = findViewById(R.id.body_temperature_separator);

                ViewGroup vg = (ViewGroup)(healthStatusLayout.getParent());
                vg.removeView(healthStatusLayout);
                vg.removeView(healthStatusSeparator);
                vg.removeView(heartbeatLayout);
                vg.removeView(heartbeatSeparator);
                vg.removeView(maxBloodPressureLayout);
                vg.removeView(maxBloodPressureSeparator);
                vg.removeView(minBloodPressureLayout);
                vg.removeView(minBloodPressureSeparator);
                vg.removeView(bodyTemperatureLayout);
                vg.removeView(bodyTemperatureSeparator);
            }
        }

        menuBtn = findViewById(R.id.menu_button);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
