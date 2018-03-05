package co.yaw.tpw.smartinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.yaw.tpw.smartinspection.R;

public class DayCheckConfirmActivity extends AppCompatActivity {

    final Context context = this;
    private Button backBtn;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_check_confirm);

        backBtn = findViewById(R.id.back_button);
        nextBtn = findViewById(R.id.next_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VisualCheckActivity.class);
                startActivity(intent);
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DayCheckShowActivity.class);
                startActivity(intent);
            }
        });
    }
}
