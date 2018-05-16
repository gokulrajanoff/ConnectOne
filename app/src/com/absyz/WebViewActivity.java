package com.absyz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.webkit.WebViewClient;
public class WebViewActivity extends AppCompatActivity {
    private android.webkit.WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        wv = findViewById(R.id.mywebView);
        Intent url = getIntent();
        String AccessURL=url.getStringExtra("URL");
        wv.setWebViewClient(new WebViewClient());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl(AccessURL);

    }

}
