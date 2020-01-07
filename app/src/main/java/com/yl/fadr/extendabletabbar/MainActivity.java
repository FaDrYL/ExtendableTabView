package com.yl.fadr.extendabletabbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yl.widgets.extendabletabview.ExtendableTabView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testTabView();

    }

    private void testTabView(){
        LinearLayout ll_basic = (LinearLayout) getLayoutInflater().inflate(R.layout.tab_basic, null);
        final TextView tv_textSize_hint = ll_basic.findViewById(R.id.tab_basic_textSize_hint);
        SeekBar seekBar = ll_basic.findViewById(R.id.tab_basic_textSize);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_textSize_hint.setText(String.valueOf(progress) + "sp");
                ((TextView) findViewById(R.id.main_text)).setTextSize(TypedValue.COMPLEX_UNIT_SP, progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        ExtendableTabView tab = findViewById(R.id.extendableTabView);
        ArrayList<String> strs = new ArrayList<String>();
        strs.add("BASIC");
        strs.add("90");
        strs.add("100");
        LinearLayout[] lls = new LinearLayout[]{ll_basic,
                null,
                (LinearLayout) View.inflate(getApplicationContext(), R.layout.sample_body_3, null)};
        tab.addItems(strs, lls);
    }
}
