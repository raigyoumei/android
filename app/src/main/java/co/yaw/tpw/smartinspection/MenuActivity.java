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
    private TextView dayCallBtn;
    private TextView beforeBizCallBtn;
    private TextView afterBizCallBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        dayCallBtn = findViewById(R.id.day_call_button);
        beforeBizCallBtn = findViewById(R.id.before_biz_call_button);
        afterBizCallBtn = findViewById(R.id.after_biz_call_button);

        dayCallBtn.setText(Html.fromHtml(getResources().getString(R.string.menu_day_call)));
        beforeBizCallBtn.setText(Html.fromHtml(getResources().getString(R.string.menu_before_biz_call)));
        afterBizCallBtn.setText(Html.fromHtml(getResources().getString(R.string.menu_after_biz_call)));

        dayCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , CallMenuActivity.class);
                startActivity(intent);
            }
        });
        beforeBizCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , EngineActivity.class);
                startActivity(intent);
            }
        });
        afterBizCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , EngineActivity.class);
                startActivity(intent);
            }
        });
    }
}
