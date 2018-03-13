package co.yaw.tpw.smartinspection.maskview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wonderkiln.camerakit.CameraView;
import com.yaw.tpw.smartinspection.R;

/**
 * Created by renxiaodong on 2018/03/12.
 */

public class CameraMaskView extends CameraView {
    static private TextView tv;
    public CameraMaskView(@NonNull Context context) {
        super(context);
    }

    public CameraMaskView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraMaskView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void start() {
        super.start();
        post(new Runnable() {
            @Override
            public void run() {
                show();
            }
        });
    }

    @Override
    public void stop() {
        super.stop();
        post(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        });
    }

    private void show() {
        setVisibility(VISIBLE);
        tv = getRootView().findViewById(R.id.cameraMessage);
        tv.setVisibility(INVISIBLE);
    }

    private void hide() {
        setVisibility(INVISIBLE);
        tv = getRootView().findViewById(R.id.cameraMessage);
        tv.setVisibility(VISIBLE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean isStarted = isStarted();
        if(isStarted) {
            Path clipPath = new Path();
            RectF oval = new RectF(0, 0, canvas.getWidth(), canvas.getWidth());
            clipPath.addOval(oval, Path.Direction.CW);
            canvas.clipPath(clipPath);

            super.onDraw(canvas);
        }
    }
}