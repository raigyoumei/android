package co.yaw.tpw.smartinspection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yaw.tpw.smartinspection.R;

public class CallMenuActivity extends AppCompatActivity {

    final Context context = this;
    private Button dayCallBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_menu);

        dayCallBtn = findViewById(R.id.day_call_button);

        dayCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , DayCheckActivity.class);
                startActivity(intent);
            }
        });
    }
}
