package co.yaw.tpw.smartinspection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.yaw.tpw.smartinspection.R;

import co.yaw.tpw.smartinspection.bltUtil.ConstUtil;

public class MenuActivity extends AppCompatActivity {

    final Context context = this;
    private TextView dayCheckBtn;
    private TextView beforeCrewCallBtn;
    private TextView afterCrewCallBtn;
    private TextView logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        dayCheckBtn = findViewById(R.id.day_check_button);
        beforeCrewCallBtn = findViewById(R.id.before_crew_call_button);
        afterCrewCallBtn = findViewById(R.id.after_crew_call_button);
        logoutBtn = findViewById(R.id.log_out_button);

        dayCheckBtn.setText(Html.fromHtml(getResources().getString(R.string.menu_day_check)));
        beforeCrewCallBtn.setText(Html.fromHtml(getResources().getString(R.string.menu_before_crew_call)));
        afterCrewCallBtn.setText(Html.fromHtml(getResources().getString(R.string.menu_after_crew_call)));

        dayCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , DayCheckStartActivity.class);
                startActivity(intent);
            }
        });
        beforeCrewCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(ConstUtil.FORWARD_BEFORE_VALUE);
            }
        });
        afterCrewCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(ConstUtil.FORWARD_AFTER_VALUE);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void startNextActivity(String forward) {
        Intent intent = new Intent(context , CallMenuActivity.class);
        Bundle b = new Bundle();
        b.putString(ConstUtil.FORWARD_KEY, forward);
        intent.putExtras(b);
        startActivity(intent);
    }
}
