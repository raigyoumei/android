package co.yaw.tpw.smartinspection;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.yaw.tpw.smartinspection.R;

import java.util.ArrayList;
import java.util.List;

public class AlcoholMeasureActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final Context context = this;
    private Button backBtn;
    private Button nextBtn;
    private Class<?> forwardCls = null;
    private Spinner alcoholSensorSpinner;

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

        setSpinnerAdapter();
    }

    private void setSpinnerAdapter() {
        alcoholSensorSpinner = findViewById(R.id.alcohol_sensor_spinner);
        alcoholSensorSpinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add(getResources().getString(R.string.alcohol_sensor_spinner_prompt));
        categories.add("ES3256151SDF");
        categories.add("FS3256151SDF");
        categories.add("GS3256151SDF");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alcoholSensorSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position > 0){
            String item = parent.getItemAtPosition(position).toString();
            Toast.makeText(parent.getContext(), "選択: " + item, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
