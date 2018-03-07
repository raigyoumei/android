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
                View vitalSignLayout = findViewById(R.id.vital_sign_layout);
                View vitalSignSeparator = findViewById(R.id.vital_sign_separator);
                View healthStatusLayout = findViewById(R.id.health_status_layout);
                View healthStatusSeparator = findViewById(R.id.health_status_separator);

                ViewGroup vg = (ViewGroup)(vitalSignLayout.getParent());
                vg.removeView(vitalSignLayout);
                vg.removeView(vitalSignSeparator);
                vg.removeView(healthStatusLayout);
                vg.removeView(healthStatusSeparator);
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
