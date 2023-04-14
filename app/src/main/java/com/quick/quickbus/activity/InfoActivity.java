package com.quick.quickbus.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.quick.quickbus.R;

public class InfoActivity extends AppCompatActivity {

    private TextView source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_info);

        initView();
        String text = "<a href=\"https://github.com/CRui522/QuickBus\">QuickBus</a> by Rui";
        source.setText(Html.fromHtml(text));
        source.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void initView() {
        source = findViewById(R.id.source);
    }
}
