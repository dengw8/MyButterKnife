package com.example.administrator.mybutterknife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.annotation.BindView;
import com.example.annotation.OnClick;
import com.example.api_module.MyButterKnife;
import com.example.api_module.provider.ActivityProvider;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.text)
    TextView textView;

    @BindView(R.id.button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // new MainActivity$$ViewInject().inject(this, this, new ActivityProvider());
        MyButterKnife.injectView(this);
    }

    @OnClick(R.id.button)
    public void click() {
        textView.setText("you click the button");
    }
}
