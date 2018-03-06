package co.yaw.tpw.smartinspection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yaw.tpw.smartinspection.R;

public class AlcoholMeasureActivity extends AppCompatActivity {

    final Context context = this;
    private Button backBtn;
    private Button nextBtn;
    private Class<?> forwardCls = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol_measure);

        final Bundle b = getIntent().getExtras();
        if(b != null) {
            String forward = b.getString("forward");
            if(forward.equals("beforeBiz")) {
                forwardCls = VitalSignMeasureActivity.class;
            } else {
                forwardCls = CallConfirmActivity.class;
            }
        }
        backBtn = findViewById(R.id.back_button);
        nextBtn = findViewById(R.id.next_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MenuActivity.class);
                startActivity(intent);
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, forwardCls);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }
}
