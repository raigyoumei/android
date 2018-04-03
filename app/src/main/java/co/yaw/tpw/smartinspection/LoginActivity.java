package co.yaw.tpw.smartinspection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yaw.tpw.smartinspection.R;

import co.yaw.tpw.smartinspection.http.HTTP;
import co.yaw.tpw.smartinspection.http.userInfo.EntryUtil;
import co.yaw.tpw.smartinspection.http.userInfo.UserEntry;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    final Context context = this;
    private Button logInBtn;
    private TextView workerIDView;
    private TextView userIDView;
    private TextView pwdView;

    private HTTP mHTTP = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        workerIDView = findViewById(R.id.workerID);
        userIDView = findViewById(R.id.userID);
        pwdView = findViewById(R.id.password);

        logInBtn = (Button) findViewById(R.id.log_in_button);
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String workerID = workerIDView.getText().toString();
                String userID = userIDView.getText().toString();
                String password = pwdView.getText().toString();

                UserEntry su = new UserEntry();
                su.setWorkerID(workerID);
                su.setUserID(userID);
                su.setPassword(password);

                EntryUtil.setEntry(context, su);

                Bundle bundle = EntryUtil.getBundle(context);
                mHTTP = new HTTP((Activity) context, bundle);
                mHTTP.doLogin(workerID, userID, password);
            }
        });
    }

}

