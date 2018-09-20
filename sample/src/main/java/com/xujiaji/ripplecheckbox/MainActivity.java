package com.xujiaji.ripplecheckbox;

import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xujiaji.library.RippleCheckBox;
import com.xujiaji.library.RippleCheckBoxUtil;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RippleCheckBox rippleCheckBox;
    private @ColorInt int[] colors = {0xff177bbd, 0xffe69310, 0xff808080,
            0xff434343, 0xffff9800, 0xff8bc34a, 0xff9e9e9e, 0xffc2185b,
    0xff99cc00, 0xffaa66cc, 0xffff8800};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListener(rippleCheckBox = findViewById(R.id.rippleCheckBox), (TextView)findViewById(R.id.textView), findViewById(R.id.line));
        addListener((RippleCheckBox)findViewById(R.id.rippleCheckBox2), (TextView)findViewById(R.id.textView2), findViewById(R.id.line2));

        addSeekBarListener(R.id.seekBarCenterRadius);
        addSeekBarListener(R.id.seekBarCenterStrokeWidth);
        addSeekBarListener(R.id.seekBarCenterColor);
        addSeekBarListener(R.id.seekBarRightStrokeWidth);
        addSeekBarListener(R.id.seekBarRightColor);
        addSeekBarListener(R.id.seekBarRippleStrokeWidth);
        addSeekBarListener(R.id.seekBarRippleColor);
        addSeekBarListener(R.id.seekBarRippleSpeed);
        addSeekBarListener(R.id.seekBarRippleMargin);
        addSeekBarListener(R.id.seekBarRightSpeed);

        addSeekBarListener(R.id.seekBarRightStartDegree);
        addSeekBarListener(R.id.seekBarRightCenterDegree);
        addSeekBarListener(R.id.seekBarRightEndDegree);

        addSeekBarListener(R.id.seekBarRightCorner);

    }

    private void addSeekBarListener(@IdRes int id) {
        SeekBar seekBar = findViewById(id);
        seekBar.setMax(120);
        final int initProgress = 60;
        seekBar.setProgress(initProgress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int defaultCenterCircleRadius = rippleCheckBox.getCenterCircleRadius();
            float defaultCenterCircleStrokeWidth = rippleCheckBox.getCenterCircleStrokeWidth();
            Random random = new Random();

            float defaultRightStrokeWidth = rippleCheckBox.getRightStrokeWidth();

            float defaultRippleStrokeWidth = rippleCheckBox.getRippleStrokeWidth();

            float defaultRightDuration = rippleCheckBox.getRightDuration();

            float defaultRippleMargin = rippleCheckBox.getRippleMargin();

            float defaultRippleDuration = rippleCheckBox.getRippleDuration();

            float defaultRightStartDegree = rippleCheckBox.getRightStartDegree();

            float defaultRightCenterDegree = rippleCheckBox.getRightCenterDegree();

            float defaultRightEndDegree = rippleCheckBox.getRightEndDegree();

            float defaultRightCorner = rippleCheckBox.getRightCorner();

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final int p = progress - initProgress;
                switch (seekBar.getId()) {
                    case R.id.seekBarCenterRadius:
                        rippleCheckBox.setCenterCircleRadius(defaultCenterCircleRadius + p);
                        rippleCheckBox.updateCenterCircle();
                        break;
                    case R.id.seekBarCenterStrokeWidth:
                        rippleCheckBox.setCenterCircleStrokeWidth(defaultCenterCircleStrokeWidth + p);
                        break;
                    case R.id.seekBarCenterColor:
                        rippleCheckBox.setCenterCircleColor(colors[random.nextInt(colors.length)]);
                        break;
                    case R.id.seekBarRightStrokeWidth:
                        rippleCheckBox.setRightStrokeWidth(defaultRightStrokeWidth + p);
                        break;
                    case R.id.seekBarRightColor:
                        rippleCheckBox.setRightColor(colors[random.nextInt(colors.length)]);
                        break;
                    case R.id.seekBarRippleStrokeWidth:
                        rippleCheckBox.setRippleStrokeWidth(defaultRippleStrokeWidth + p);
                        break;
                    case R.id.seekBarRippleColor:
                        rippleCheckBox.setRippleColor(colors[random.nextInt(colors.length)]);
                        break;
                    case R.id.seekBarRippleMargin:
                        rippleCheckBox.setRippleMargin((int) (defaultRippleMargin + p));
                        break;
                    case R.id.seekBarRippleSpeed:
                        rippleCheckBox.setRippleDuration((int) (defaultRippleDuration + p * 2));
                        break;
                    case R.id.seekBarRightSpeed:
                        rippleCheckBox.setRightDuration((int) (defaultRightDuration + p * 2));
                        break;
                    case R.id.seekBarRightStartDegree:
                        rippleCheckBox.setRightStartDegree((int) (defaultRightStartDegree + p));
                        rippleCheckBox.updateCenterCircle();
                        break;
                    case R.id.seekBarRightCenterDegree:
                        rippleCheckBox.setRightCenterDegree((int) (defaultRightCenterDegree + p));
                        rippleCheckBox.updateCenterCircle();
                        break;
                    case R.id.seekBarRightEndDegree:
                        rippleCheckBox.setRightEndDegree((int) (defaultRightEndDegree + p));
                        rippleCheckBox.updateCenterCircle();
                        break;
                    case R.id.seekBarRightCorner:
                        rippleCheckBox.setRightCorner((int) (defaultRightCorner + p));
                        break;
                }
                rippleCheckBox.invalidate();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void addListener(RippleCheckBox checkBox, final TextView tv, final View l) {
        checkBox.setOnCheckedChangeListener(new RippleCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RippleCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    l.animate()
                            .setDuration(400)
                            .translationY(- (RippleCheckBoxUtil.dp2px(MainActivity.this, 12) + tv.getHeight() / 2))
                            .start();
                    tv.animate()
                            .setDuration(400)
                            .alpha(0.3F)
                            .start();
                } else {
                    l.animate()
                            .setDuration(400)
                            .translationY(0)
                            .start();
                    tv.animate()
                            .setDuration(400)
                            .alpha(1F)
                            .start();
                }
            }
        });

    }
}
