package co.yaw.tpw.smartinspection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yaw.tpw.smartinspection.R;

import co.yaw.tpw.smartinspection.bltUtil.ConstUtil;

public class HealthStatusRegActivity extends AppCompatActivity {

    final Context context = this;

    private Button mBackBtn = null;
    private String mForward = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_status_reg);

        mBackBtn = findViewById(R.id.menu_button);

        final Bundle b = getIntent().getExtras();
        if(b != null) {
            mForward = b.getString(ConstUtil.FORWARD_KEY);

            if(mForward.equals(ConstUtil.FORWARD_BEFORE_VALUE)) {
                mBackBtn.setText(R.string.check_crew_back_before);
            } else {
                mBackBtn.setText(R.string.check_crew_back_after);
            }
        }

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CallMenuActivity.class);
                Bundle b = new Bundle();
                b.putString(ConstUtil.FORWARD_KEY, mForward);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }
}
