package co.yaw.tpw.smartinspection;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yaw.tpw.smartinspection.R;

public class VisualCheckActivity extends AppCompatActivity {

    final Context context = this;
    private Button backBtn;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_check);

        backBtn = findViewById(R.id.back_button);
        nextBtn = findViewById(R.id.next_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CarActivity.class);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DayCheckConfirmActivity.class);
                startActivity(intent);
            }
        });

        // TODO there is a bug when there are multiple items
//        LinearLayout linearLayout = findViewById(R.id.item_container);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        addSeparator(linearLayout);
//        addItem(linearLayout,"title", "info", "NG" );
//        addSeparator(linearLayout);
    }

    protected View addItem(ViewGroup viewGroup, String title, String info, String btnText) {
        LayoutInflater inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.visual_check_item, viewGroup);
        TextView checkTitle = itemView.findViewById(R.id.check_title);
        TextView checkInfo = itemView.findViewById(R.id.check_info);
        Button checkBtn = itemView.findViewById(R.id.check_btn);
        checkTitle.setText(title);
        checkInfo.setText(info);
        checkBtn.setText(btnText);

        return itemView;
    }

    protected View addSeparator(ViewGroup viewGroup) {
        LayoutInflater inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.visual_check_item_separator, viewGroup);
    }
}
