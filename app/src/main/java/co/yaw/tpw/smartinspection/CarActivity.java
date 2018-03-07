package co.yaw.tpw.smartinspection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yaw.tpw.smartinspection.R;

import java.util.ArrayList;
import java.util.List;

public class CarActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final Context context = this;
    private Button nextBtn;
    private Spinner carNumberSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        nextBtn = findViewById(R.id.next_button);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VisualCheckActivity.class);
                startActivity(intent);
            }
        });

        setSpinnerAdapter();
    }

    private void setSpinnerAdapter() {
        carNumberSpinner = findViewById(R.id.car_number_spinner);
        carNumberSpinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add(getResources().getString(R.string.car_spinner_prompt));
        categories.add("品川47　ぬ24-869");
        categories.add("札幌500 れ92-52");
        categories.add("仙台78  か36-57");
        categories.add("三河60　あ88-90");
        categories.add("大阪20　き78-20");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carNumberSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            String item = parent.getItemAtPosition(position).toString();
            popup(item);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void popup(String carNumber) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.car_selector, null);
        TextView carSelectorContent = alertLayout.findViewById(R.id.car_selector_content);
        carSelectorContent.setText("車両番号：" + carNumber + "\nが選択されています。");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);

        final AlertDialog dialog = alert.create();
        dialog.show();

        TextView cancleBtn = alertLayout.findViewById(R.id.cancle_button);
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView okBtn = alertLayout.findViewById(R.id.ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
