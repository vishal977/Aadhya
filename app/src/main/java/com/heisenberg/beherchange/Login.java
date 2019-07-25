package com.heisenberg.beherchange;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import at.markushi.ui.CircleButton;

public class Login extends AppCompatActivity {
    CircleButton login;
    Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setFontPacifico();
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(),MainActivity.class);
                startActivity(in);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(),Information.class);
                startActivity(in);
            }
        });
    }
    public void setFontPacifico()
    {
        TextView tx = findViewById(R.id.appTit);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Pacifico.ttf");
        tx.setTypeface(custom_font);
    }
}
