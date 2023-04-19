package com.quick.quickbus.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.quick.quickbus.BusApplication;
import com.quick.quickbus.MySharedPreferences;
import com.quick.quickbus.R;
import com.quick.quickbus.model.ResponseData;
import com.quick.quickbus.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView registerText;
    private final String host = BusApplication.host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        initView();
        registerText.setPaintFlags(registerText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        loginButton.setOnClickListener(view -> login());

        registerText.setOnClickListener(view -> register());

    }

    public void initView() {
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        registerText = findViewById(R.id.register_text);
    }

    public void register() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        new Thread(() -> {
            Looper.prepare();
            ResponseData response = HttpUtil.run(host + "/auth/register", "POST", params);
            if (response != null) {
                int code = response.getCode();
                if (code == 201) {
                    Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                } else if (code == 200) {
                    Toast.makeText(LoginActivity.this, "用户名·已存在", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
            Looper.loop();
        }).start();
    }

    public void login() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        String s = host + "/auth/login";

        new Thread(() -> {
            Looper.prepare();
            ResponseData response = HttpUtil.run(s, "POST", params);
            if (response != null) {
                int code = response.getCode();
                if (code == 200) {
                    new MySharedPreferences(LoginActivity.this, "auth").putInt("login", 1);
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "无效的用户名或者密码", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
            Looper.loop();
        }).start();
    }
}