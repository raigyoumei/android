package co.yaw.tpw.smartinspection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.yaw.tpw.smartinspection.R;

public class MenuActivity extends AppCompatActivity {

    final Context context = this;
    private TextView dayCheckBtn;
    private TextView beforeCrewCallBtn;
    private TextView afterCrewCallBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        dayCheckBtn = findViewById(R.id.day_check_button);
        beforeCrewCallBtn = findViewById(R.id.before_crew_call_button);
        afterCrewCallBtn = findViewById(R.id.after_crew_call_button);

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
                startNextActivity("beforeCrew");
            }
        });
        afterCrewCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity("afterCrew");
            }
        });
    }

    protected void startNextActivity(String forward) {
        Intent intent = new Intent(context , AlcoholMeasureActivity.class);
        Bundle b = new Bundle();
        b.putString("forward", forward);
        intent.putExtras(b);
        startActivity(intent);
    }
}
